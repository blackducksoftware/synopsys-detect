package com.synopsys.integration.detect.fastsca.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.synopsys.bd.kb.httpclient.api.IBdComponentApi;
import com.synopsys.bd.kb.httpclient.api.IBdComponentVersionApi;
import com.synopsys.bd.kb.httpclient.api.IBdKbHttpApi;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResponse;
import com.synopsys.bd.kb.httpclient.api.MigratableHttpResult;
import com.synopsys.bd.kb.httpclient.impl.BdComponentFinder;
import com.synopsys.bd.kb.httpclient.impl.BdLicenseDefinitionFinder;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;
import com.synopsys.bd.kb.httpclient.model.BdComponentVariantHierarchy;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersionHierarchy;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.bd.kb.httpclient.model.BdLicenseDefinition;
import com.synopsys.integration.detect.fastsca.model.FastScaComponent;
import com.synopsys.integration.detect.fastsca.model.FastScaComponentOrigin;
import com.synopsys.integration.detect.fastsca.model.FastScaComponentVersion;
import com.synopsys.integration.detect.fastsca.model.FastScaDependency;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyTree;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyType;
import com.synopsys.integration.detect.fastsca.model.FastScaEvidence;
import com.synopsys.integration.detect.fastsca.model.FastScaIdentification;
import com.synopsys.integration.detect.fastsca.model.FastScaLicense;
import com.synopsys.integration.detect.fastsca.model.FastScaLicenseDefinition;
import com.synopsys.integration.detect.fastsca.model.FastScaMatch;
import com.synopsys.integration.detect.fastsca.model.FastScaMatchType;
import com.synopsys.integration.detect.fastsca.model.FastScaMetadata;
import com.synopsys.integration.detect.fastsca.model.FastScaRelatedVulnerability;
import com.synopsys.integration.detect.fastsca.model.FastScaReport;
import com.synopsys.integration.detect.fastsca.model.FastScaUpgradeGuidance;
import com.synopsys.integration.detect.fastsca.model.FastScaVulnerability;
import com.synopsys.kb.httpclient.api.HttpResponse;
import com.synopsys.kb.httpclient.api.HttpResult;
import com.synopsys.kb.httpclient.api.IComponentApi;
import com.synopsys.kb.httpclient.api.IComponentVariantApi;
import com.synopsys.kb.httpclient.api.PageRequest;
import com.synopsys.kb.httpclient.model.BdsaVulnerability;
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.ComponentSearchResult;
import com.synopsys.kb.httpclient.model.CveVulnerability;
import com.synopsys.kb.httpclient.model.LicenseDefinition;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;
import com.synopsys.kb.httpclient.model.Page;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;
import com.synopsys.kb.httpclient.model.VulnerabilityScorePriority;
import com.synopsys.kb.httpclient.model.VulnerabilitySource;
import com.synopsys.kb.httpclient.model.VulnerabilitySourcePriority;

/**
 * Assembles a fastSCA report given evidence.
 * 
 * @author skatzman
 */
public class FastScaReportAssembly {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final IBdKbHttpApi bdKbHttpApi;
	
	public FastScaReportAssembly(IBdKbHttpApi bdKbHttpApi) {
		this.bdKbHttpApi = Objects.requireNonNull(bdKbHttpApi, "BD KB HTTP API must be initialized.");
	}
	
	/**
	 * Assembles a fastSCA report given a set of packages and metadata.
	 * 
	 * Used for a package manager scan.
	 * 
	 * @param evidences The evidences.
	 * @param meta The metadata.
	 * @return Returns the fastSCA report.
	 */
	public FastScaReport assemble(Set<FastScaEvidence> evidences, FastScaMetadata meta) {
		Objects.requireNonNull(evidences, "Evidences must be initialized.");
		Objects.requireNonNull(meta, "Metadata must be initialized.");
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			Collection<FastScaIdentification> identifications = assembleIdentifications(evidences);
		
			logger.info("Identified {} components.", identifications.size());
			
			return new FastScaReport(identifications, meta);
		} finally {
			stopwatch = stopwatch.stop();
			long durationMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
			logger.info("Completed fastSCA report assembly [Duration: {}ms].", durationMs);
		}
	}
	
	private Collection<FastScaIdentification> assembleIdentifications(Set<FastScaEvidence> evidences) {
		Collection<FastScaIdentification> identifications = new ArrayList<>();
		
		/* TODO: Rework lookup algorithm to reduce the total number of round trips to the KnowledgeBase for
		 * duplicate components, component versions, component variants, and licenses. 
		 */		
		
		if (!evidences.isEmpty()) {
			SetMultimap<UUID, FastScaEvidence> componentIdEvidences = HashMultimap.create();
			SetMultimap<UUID, FastScaEvidence> componentVersionIdEvidences = HashMultimap.create();
			SetMultimap<UUID, FastScaEvidence> componentVariantIdEvidences = HashMultimap.create();
			
			for (FastScaEvidence evidence : evidences) {
				FastScaDependency dependency = evidence.getDependency();
				String searchTermFilter = dependency.getComponentSearchTerm();
				
				ComponentSearchResult componentSearchResult = searchComponents(searchTermFilter).orElse(null);
				if (componentSearchResult != null) {
					UUID componentId = componentSearchResult.getComponentId().orElse(null);
					UUID componentVersionId = componentSearchResult.getVersionId().orElse(null);
					UUID componentVariantId = componentSearchResult.getVariantId().orElse(null);
					if (componentVariantId != null) {
						// Component variant match
						componentVariantIdEvidences.put(componentVariantId, evidence);
					} else if (componentVersionId != null) {
						// Component version match.
						componentVersionIdEvidences.put(componentVersionId, evidence);
					} else if (componentId != null) {
						// Component match.
						componentIdEvidences.put(componentId, evidence);
					} // No matched component identifier for this search term.
				} //  No match found for this search term. 
			}
			
			Collection<FastScaIdentification> componentIdentifications = findComponents(componentIdEvidences);
			identifications.addAll(componentIdentifications);
			
			Collection<FastScaIdentification> componentVersionIdentifications = findComponentVersions(componentVersionIdEvidences);
			identifications.addAll(componentVersionIdentifications);
			
			Collection<FastScaIdentification> componentVariantIdentifications = findComponentVariants(componentVariantIdEvidences);
			identifications.addAll(componentVariantIdentifications);
		}
		
		return identifications;
	}
	
	private Optional<ComponentSearchResult> searchComponents(String searchTermFilter) {
		ComponentSearchResult componentSearchResult = null;
		
		IComponentApi componentApi = bdKbHttpApi.getKbHttpApi().getComponentApi();
		
		PageRequest pageRequest = new PageRequest(0, 10, Collections.emptyList());
		HttpResult<Page<ComponentSearchResult>> httpResult = componentApi.searchComponentsV3(pageRequest, searchTermFilter, false);
		HttpResponse<Page<ComponentSearchResult>> httpResponse = httpResult.getHttpResponse().orElse(null);
		if (httpResponse != null) {
			Page<ComponentSearchResult> componentSearchResultPage = httpResponse.getMessageBody().orElse(null);
			if (componentSearchResultPage != null) {
				List<ComponentSearchResult> componentSearchResults = componentSearchResultPage.getItems();
				if (!componentSearchResults.isEmpty()) {
					componentSearchResult = componentSearchResults.get(0);
				} // No component match found for this search.
			} // Gracefully accept no component match found for this search.
		} else {
			// HTTP response could not be retrieved - failure condition.
			httpResult.getCause().ifPresentOrElse((cause) -> {
				throw new RuntimeException("Unable to search components for '" + searchTermFilter + "'.", cause);	
			}, () -> {
				throw new RuntimeException("Unable to search components for '" + searchTermFilter + "'.");
			});     
		}
		
		return Optional.ofNullable(componentSearchResult);
	}
	
	private Collection<FastScaIdentification> findComponents(SetMultimap<UUID, FastScaEvidence> componentIdEvidences) {
		Collection<FastScaIdentification> identifications = new ArrayList<>();
		
		// TODO: Enhance algorithm to better handle unexpected response codes and absent components.
		if (!componentIdEvidences.isEmpty()) {
			IBdComponentApi bdComponentApi = bdKbHttpApi.getBdComponentApi();
			
			for (UUID componentId : componentIdEvidences.keySet()) {
				MigratableHttpResult<Component> httpResult = bdComponentApi.findComponentV4(componentId);
				Component component = extract(httpResult, "Unable to find component (" + componentId + ").");
				if (component != null) {
					FastScaComponent fastScaComponent = new FastScaComponent(component);
					Set<FastScaEvidence> evidences = componentIdEvidences.get(componentId);
					Collection<FastScaMatch> matches = convert(evidences);
					FastScaIdentification identification = new FastScaIdentification(fastScaComponent, null, null, null, null, matches);
					identifications.add(identification);
				} // Gracefully accept component metadata could not be found for this match and continue.
			}
		}
		
		return identifications;
	}
	
	private Collection<FastScaIdentification> findComponentVersions(SetMultimap<UUID, FastScaEvidence> componentVersionIdEvidences) {
		Collection<FastScaIdentification> identifications = new ArrayList<>();
		
		// TODO: Enhance algorithm to better handle unexpected response codes and absent component versions, 
		// vulnerabilities, and upgrade guidance.
		if (!componentVersionIdEvidences.isEmpty()) {
			BdComponentFinder bdComponentFinder = bdKbHttpApi.getBdComponentFinder();
			BdLicenseDefinitionFinder bdLicenseDefinitionFinder = bdKbHttpApi.getBdLicenseDefinitionFinder();
			
			for (UUID componentVersionId : componentVersionIdEvidences.keySet()) {
				BdComponentVersionHierarchy hierarchy = bdComponentFinder.findComponentVersionHierarchy(componentVersionId, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3).orElse(null);
				if (hierarchy != null) {
					MigratableHttpResult<Component> componentHttpResult = hierarchy.getComponentResult();
					Component component = extract(componentHttpResult, "Unable to find component.");
					
					MigratableHttpResult<BdComponentVersion> componentVersionHttpResult = hierarchy.getComponentVersionResult();
					BdComponentVersion bdComponentVersion = extract(componentVersionHttpResult, "Unable to find component version (" + componentVersionId + ").");
					if (component != null && bdComponentVersion != null) {
						LicenseDefinition licenseDefinition = bdComponentVersion.getLicenseDefinitionOrDefault();
						BdLicenseDefinition bdLicenseDefinition = bdLicenseDefinitionFinder.find(licenseDefinition).orElse(null);
						if (bdLicenseDefinition != null) {
							FastScaComponent fastScaComponent = new FastScaComponent(component);
							
							FastScaLicenseDefinition fastScaLicenseDefinition = convert(bdLicenseDefinition);
							FastScaComponentVersion fastScaComponentVersion = new FastScaComponentVersion(bdComponentVersion, fastScaLicenseDefinition);
							
							Collection<FastScaVulnerability> vulnerabilities = null;
							FastScaUpgradeGuidance upgradeGuidance = null;
							if (bdComponentVersion.getRiskProfile().areVulnerabilitiesPresent()) {
								UUID destinationComponentVersionId = bdComponentVersion.getId();
								vulnerabilities = findComponentVersionVulnerabilities(destinationComponentVersionId);
								upgradeGuidance = findComponentVersionUpgradeGuidance(destinationComponentVersionId);
							}
							
							Set<FastScaEvidence> evidences = componentVersionIdEvidences.get(componentVersionId);
							Collection<FastScaMatch> matches = convert(evidences);
							FastScaIdentification identification = new FastScaIdentification(fastScaComponent, fastScaComponentVersion, null, upgradeGuidance, vulnerabilities, matches);
							identifications.add(identification);
						} // Gracefully accept component version metadata could not be found for this match and continue.
					} // Gracefully accept component version metadata could not be found for this match and continue.
				} // Gracefully accept component version metadata could not be found for this match and continue.
			}
		}
		
		return identifications;
	}
	
	private Collection<FastScaVulnerability> findComponentVersionVulnerabilities(UUID componentVersionId) {
		Collection<FastScaVulnerability> vulnerabilities = new ArrayList<>();
		
		IBdComponentVersionApi bdComponentVersionApi = bdKbHttpApi.getBdComponentVersionApi();
		
		// TODO: Implement pagination functionality if more than 100 vulnerabilities are present per component version.
		// TODO: Enhance algorithm to account for migrated vulnerabilities response to a different component version.
		PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
		MigratableHttpResult<Page<CveVulnerability>> cveVulnerabilityPageHttpResult = bdComponentVersionApi.findCveVulnerabilitiesV7(pageRequest, componentVersionId, null);
		Page<CveVulnerability> cveVulnerabilityPage = extract(cveVulnerabilityPageHttpResult, "Unable to find component version CVE vulnerabilities (" + componentVersionId + ").");
		if (cveVulnerabilityPage != null) {
			List<CveVulnerability> cveVulnerabilities = cveVulnerabilityPage.getItems();
			
			List<FastScaVulnerability> fastScaCveVulnerabilities = cveVulnerabilities.stream().map((cveVulnerability) -> new FastScaVulnerability(cveVulnerability, null)).collect(Collectors.toList());
			vulnerabilities.addAll(fastScaCveVulnerabilities);
		} // Gracefully accept component version CVE vulnerabilities could not be found and continue.
		
		MigratableHttpResult<Page<BdsaVulnerability>> bdsaVulnerabilityPageHttpResult = bdComponentVersionApi.findBdsaVulnerabilitiesV7(pageRequest, componentVersionId, null);
		Page<BdsaVulnerability> bdsaVulnerabilityPage = extract(bdsaVulnerabilityPageHttpResult, "Unable to find component version BDSA vulnerabilities (" + componentVersionId + ").");
		if (bdsaVulnerabilityPage != null) {
			List<BdsaVulnerability> bdsaVulnerabilities = bdsaVulnerabilityPage.getItems();
			List<FastScaVulnerability> fastScaBdsaVulnerabilities = bdsaVulnerabilities.stream().map((bdsaVulnerability) -> {
				FastScaRelatedVulnerability fastScaRelatedVulnerability = bdsaVulnerability.getRelatedCveVulnerabilityId().map((cveVulnerabilityId) -> new FastScaRelatedVulnerability(VulnerabilitySource.NVD, cveVulnerabilityId)).orElse(null);
				
				return new FastScaVulnerability(bdsaVulnerability, fastScaRelatedVulnerability);
			}).collect(Collectors.toList());
			vulnerabilities.addAll(fastScaBdsaVulnerabilities);
		} // Gracefully accept component version BDSA vulnerabilities could not be found and continue.
		
		return vulnerabilities;
	}
	
	@Nullable
	private FastScaUpgradeGuidance findComponentVersionUpgradeGuidance(UUID componentVersionId) {
		FastScaUpgradeGuidance fastScaUpgradeGuidance = null;
		
		// TODO: Enhance algorithm to account for migrated upgrade guidance response to a different component version.
		IBdComponentVersionApi bdComponentVersionApi = bdKbHttpApi.getBdComponentVersionApi();
		
		MigratableHttpResult<UpgradeGuidance> httpResult = bdComponentVersionApi.findUpgradeGuidanceV4(componentVersionId, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);
		UpgradeGuidance upgradeGuidance = extract(httpResult, "Unable to find component version upgrade guidance (" + componentVersionId + ").");
		if (upgradeGuidance != null) {
			fastScaUpgradeGuidance = new FastScaUpgradeGuidance(upgradeGuidance);
		} // Gracefully accept component version upgrade guidance could not be found and continue.
		
		return fastScaUpgradeGuidance;
	}
	
	private Collection<FastScaIdentification> findComponentVariants(SetMultimap<UUID, FastScaEvidence> componentVariantIdEvidences) {
		Collection<FastScaIdentification> identifications = new ArrayList<>();
		
		// TODO: Enhance algorithm to better handle unexpected response codes and absent component variants, 
		// vulnerabilities, and upgrade guidance.		
		if (!componentVariantIdEvidences.isEmpty()) {
			BdComponentFinder bdComponentFinder = bdKbHttpApi.getBdComponentFinder();
			BdLicenseDefinitionFinder bdLicenseDefinitionFinder = bdKbHttpApi.getBdLicenseDefinitionFinder();
			
			for (UUID componentVariantId : componentVariantIdEvidences.keySet()) {
				BdComponentVariantHierarchy hierarchy = bdComponentFinder.findComponentVariantHierarchy(componentVariantId, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3).orElse(null);
				if (hierarchy != null) {
					MigratableHttpResult<Component> componentHttpResult = hierarchy.getComponentResult();
					Component component = extract(componentHttpResult, "Unable to find component.");
					
					MigratableHttpResult<BdComponentVersion> componentVersionHttpResult = hierarchy.getComponentVersionResult();
					BdComponentVersion bdComponentVersion = extract(componentVersionHttpResult, "Unable to find component version.");
					
					HttpResult<BdComponentVariant> componentVariantHttpResult = hierarchy.getComponentVariantResult();
					BdComponentVariant bdComponentVariant = extract(componentVariantHttpResult, "Unable to find component variant (" + componentVariantId + ").");
					
					if (component != null && bdComponentVersion != null && bdComponentVariant != null) {
						LicenseDefinition licenseDefinition = bdComponentVersion.getLicenseDefinitionOrDefault();
						BdLicenseDefinition bdLicenseDefinition = bdLicenseDefinitionFinder.find(licenseDefinition).orElse(null);
						if (bdLicenseDefinition != null) {
							FastScaComponent fastScaComponent = new FastScaComponent(component);
							
							FastScaLicenseDefinition fastScaLicenseDefinition = convert(bdLicenseDefinition);
							FastScaComponentVersion fastScaComponentVersion = new FastScaComponentVersion(bdComponentVersion, fastScaLicenseDefinition);
							
							FastScaComponentOrigin fastScaComponentOrigin = new FastScaComponentOrigin(bdComponentVariant);
							Collection<FastScaVulnerability> vulnerabilities = null;
							FastScaUpgradeGuidance upgradeGuidance = null;
							if (bdComponentVersion.getRiskProfile().areVulnerabilitiesPresent()) {
								vulnerabilities = findComponentVariantVulnerabilities(componentVariantId);
								upgradeGuidance = findComponentVariantUpgradeGuidance(componentVariantId);
							}
							
							Set<FastScaEvidence> evidences = componentVariantIdEvidences.get(componentVariantId);
							Collection<FastScaMatch> matches = convert(evidences);
							FastScaIdentification identification = new FastScaIdentification(fastScaComponent, fastScaComponentVersion, fastScaComponentOrigin, upgradeGuidance, vulnerabilities, matches);
							identifications.add(identification);
						} // Gracefully accept component version metadata could not be found for this match and continue.
					} // Gracefully accept component version metadata could not be found for this match and continue.
				} // Gracefully accept component version metadata could not be found for this match and continue.
			}
		}
		
		return identifications;
	}
	
	private Collection<FastScaVulnerability> findComponentVariantVulnerabilities(UUID componentVariantId) {
		Collection<FastScaVulnerability> vulnerabilities = new ArrayList<>();
		
		IComponentVariantApi componentVariantApi = bdKbHttpApi.getKbHttpApi().getComponentVariantApi();
		
		// TODO: Implement pagination functionality if more than 100 vulnerabilities are present per component version.
		PageRequest pageRequest = new PageRequest(0, 100, Collections.emptyList());
		HttpResult<Page<CveVulnerability>> cveVulnerabilityPageHttpResult = componentVariantApi.findCveVulnerabilitiesV7(pageRequest, componentVariantId, null);
		Page<CveVulnerability> cveVulnerabilityPage = extract(cveVulnerabilityPageHttpResult, "Unable to find component variant CVE vulnerabilities (" + componentVariantId + ").");
		if (cveVulnerabilityPage != null) {
			List<CveVulnerability> cveVulnerabilities = cveVulnerabilityPage.getItems();
			
			List<FastScaVulnerability> fastScaCveVulnerabilities = cveVulnerabilities.stream().map((cveVulnerability) -> new FastScaVulnerability(cveVulnerability, null)).collect(Collectors.toList());
			vulnerabilities.addAll(fastScaCveVulnerabilities);
		} // Gracefully accept component variant CVE vulnerabilities could not be found and continue.
		
		HttpResult<Page<BdsaVulnerability>> bdsaVulnerabilityPageHttpResult = componentVariantApi.findBdsaVulnerabilitiesV7(pageRequest, componentVariantId, null);
		Page<BdsaVulnerability> bdsaVulnerabilityPage = extract(bdsaVulnerabilityPageHttpResult, "Unable to find component variant BDSA vulnerabilities (" + componentVariantId + ").");
		if (bdsaVulnerabilityPage != null) {
			List<BdsaVulnerability> bdsaVulnerabilities = bdsaVulnerabilityPage.getItems();
			List<FastScaVulnerability> fastScaBdsaVulnerabilities = bdsaVulnerabilities.stream().map((bdsaVulnerability) -> {
				FastScaRelatedVulnerability fastScaRelatedVulnerability = bdsaVulnerability.getRelatedCveVulnerabilityId().map((cveVulnerabilityId) -> new FastScaRelatedVulnerability(VulnerabilitySource.NVD, cveVulnerabilityId)).orElse(null);
				
				return new FastScaVulnerability(bdsaVulnerability, fastScaRelatedVulnerability);
			}).collect(Collectors.toList());
			vulnerabilities.addAll(fastScaBdsaVulnerabilities);
		} // Gracefully accept component variant BDSA vulnerabilities could not be found and continue.
		
		return vulnerabilities;
	}
	
	
	@Nullable
	private FastScaUpgradeGuidance findComponentVariantUpgradeGuidance(UUID componentVariantId) {
		FastScaUpgradeGuidance fastScaUpgradeGuidance = null;
		
		IComponentVariantApi componentVariantApi = bdKbHttpApi.getKbHttpApi().getComponentVariantApi();
		HttpResult<UpgradeGuidance> httpResult = componentVariantApi.findUpgradeGuidanceV4(componentVariantId, VulnerabilitySourcePriority.BDSA, VulnerabilityScorePriority.CVSS_3);
		UpgradeGuidance upgradeGuidance = extract(httpResult, "Unable to find component variant upgrade guidance (" + componentVariantId + ").");
		if (upgradeGuidance != null) {
			fastScaUpgradeGuidance = new FastScaUpgradeGuidance(upgradeGuidance);
		} // Gracefully accept component variant upgrade guidance could not be found and continue.
		
		return fastScaUpgradeGuidance;
	}
	
	@Nullable
	private FastScaLicenseDefinition convert(BdLicenseDefinition sourceBdLicenseDefinition) {
		final FastScaLicenseDefinition result;
		
		BdLicense bdLicense = sourceBdLicenseDefinition.getLicense().orElse(null);
		LicenseDefinitionType type = sourceBdLicenseDefinition.getType().orElse(null);
		List<BdLicenseDefinition> bdLicenseDefinitions = sourceBdLicenseDefinition.getLicenseDefinitions();
		if (bdLicense != null) {
			FastScaLicense fastScaLicense = new FastScaLicense(bdLicense);
			
			result = new FastScaLicenseDefinition(fastScaLicense);
		} else if (type != null && !bdLicenseDefinitions.isEmpty()) {
			List<FastScaLicenseDefinition> fastScaLicenseDefinitions = new ArrayList<>();
			for (BdLicenseDefinition bdLicenseDefinition : bdLicenseDefinitions) {
				FastScaLicenseDefinition fastScaLicenseDefinition = convert(bdLicenseDefinition);
				fastScaLicenseDefinitions.add(fastScaLicenseDefinition);
			}
			
			result = new FastScaLicenseDefinition(type, fastScaLicenseDefinitions);
		} else {
			throw new RuntimeException("Unable to convert license definition.");
		}
		
		return result;
	}
	
	private Collection<FastScaMatch> convert(Set<FastScaEvidence> sourceEvidences) {
		Collection<FastScaMatch> matches = new ArrayList<>();
		
		for (FastScaEvidence sourceEvidence : sourceEvidences) {
			Set<FastScaDependencyTree> sourceDependencyTrees = sourceEvidence.getDependencyTrees();
			for (FastScaDependencyTree sourceDependencyTree : sourceDependencyTrees) {
				FastScaDependencyType dependencyType = sourceDependencyTree.getDependencyType();
				FastScaMatchType matchType = convert(dependencyType);
				List<String> dependencyTree = sourceDependencyTree.getDependencyTree();
				FastScaMatch match = new FastScaMatch(matchType, dependencyTree);
				matches.add(match);
			}
		}
		
		return matches;
	}
	
	private FastScaMatchType convert(FastScaDependencyType dependencyType) {
		FastScaMatchType matchType = null;
		
		if (FastScaDependencyType.DIRECT.equals(dependencyType)) {
			matchType = FastScaMatchType.DIRECT_DEPENDENCY;
		} else if (FastScaDependencyType.TRANSITIVE.equals(dependencyType)) {
			matchType = FastScaMatchType.TRANSITIVE_DEPENDENCY;
		} else {
			throw new UnsupportedOperationException("Unable to convert to match type (" + dependencyType + ").");
		}
		
		return matchType;
	}
	
	@Nullable
	private <T> T extract(MigratableHttpResult<T> httpResult, String failureMessage) {
		T value = null;
		
		MigratableHttpResponse<T> httpResponse = httpResult.getMigratableHttpResponse().orElse(null);
		if (httpResponse != null) {
			value = httpResponse.getMessageBodyOrElseThrow(() -> new RuntimeException(failureMessage)).orElse(null);
		} else {
			// HTTP response could not be retrieved - failure condition.
			httpResult.getCause().ifPresentOrElse((cause) -> {
				throw new RuntimeException(failureMessage, cause);	
			}, () -> {
				throw new RuntimeException(failureMessage);
			});   
		}
		
		return value;
	}
	
	@Nullable
	private <T> T extract(HttpResult<T> httpResult, String failureMessage) {
		T value = null;
		
		HttpResponse<T> httpResponse = httpResult.getHttpResponse().orElse(null);
		if (httpResponse != null) {
			value = httpResponse.getMessageBodyOrElseThrow(() -> new RuntimeException(failureMessage)).orElse(null);
		} else {
			// HTTP response could not be retrieved - failure condition.
			httpResult.getCause().ifPresentOrElse((cause) -> {
				throw new RuntimeException(failureMessage, cause);	
			}, () -> {
				throw new RuntimeException(failureMessage);
			});   
		}
		
		return value;
	}
}
