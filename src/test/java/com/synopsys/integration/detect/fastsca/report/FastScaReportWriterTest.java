package com.synopsys.integration.detect.fastsca.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Strings;
import com.synopsys.integration.detect.fastsca.model.FastScaComponent;
import com.synopsys.integration.detect.fastsca.model.FastScaComponentOrigin;
import com.synopsys.integration.detect.fastsca.model.FastScaComponentVersion;
import com.synopsys.integration.detect.fastsca.model.FastScaIdentification;
import com.synopsys.integration.detect.fastsca.model.FastScaLicense;
import com.synopsys.integration.detect.fastsca.model.FastScaLicenseDefinition;
import com.synopsys.integration.detect.fastsca.model.FastScaMatch;
import com.synopsys.integration.detect.fastsca.model.FastScaMatchType;
import com.synopsys.integration.detect.fastsca.model.FastScaMetadata;
import com.synopsys.integration.detect.fastsca.model.FastScaRelatedVulnerability;
import com.synopsys.integration.detect.fastsca.model.FastScaReport;
import com.synopsys.integration.detect.fastsca.model.FastScaUpgradeGuidance;
import com.synopsys.integration.detect.fastsca.model.FastScaUpgradeGuidanceSuggestion;
import com.synopsys.integration.detect.fastsca.model.FastScaVulnerability;
import com.synopsys.kb.httpclient.model.Cvss2AccessComplexity;
import com.synopsys.kb.httpclient.model.Cvss2AccessVector;
import com.synopsys.kb.httpclient.model.Cvss2Authentication;
import com.synopsys.kb.httpclient.model.Cvss2AvailabilityImpact;
import com.synopsys.kb.httpclient.model.Cvss2ConfidentialityImpact;
import com.synopsys.kb.httpclient.model.Cvss2Exploitability;
import com.synopsys.kb.httpclient.model.Cvss2IntegrityImpact;
import com.synopsys.kb.httpclient.model.Cvss2RemediationLevel;
import com.synopsys.kb.httpclient.model.Cvss2ReportConfidence;
import com.synopsys.kb.httpclient.model.Cvss2Score;
import com.synopsys.kb.httpclient.model.Cvss2TemporalMetrics;
import com.synopsys.kb.httpclient.model.Cvss3AttackComplexity;
import com.synopsys.kb.httpclient.model.Cvss3AttackVector;
import com.synopsys.kb.httpclient.model.Cvss3AvailabilityImpact;
import com.synopsys.kb.httpclient.model.Cvss3ConfidentialityImpact;
import com.synopsys.kb.httpclient.model.Cvss3ExploitCodeMaturity;
import com.synopsys.kb.httpclient.model.Cvss3IntegrityImpact;
import com.synopsys.kb.httpclient.model.Cvss3PrivilegesRequired;
import com.synopsys.kb.httpclient.model.Cvss3RemediationLevel;
import com.synopsys.kb.httpclient.model.Cvss3ReportConfidence;
import com.synopsys.kb.httpclient.model.Cvss3Scope;
import com.synopsys.kb.httpclient.model.Cvss3Score;
import com.synopsys.kb.httpclient.model.Cvss3TemporalMetrics;
import com.synopsys.kb.httpclient.model.Cvss3UserInteraction;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;
import com.synopsys.kb.httpclient.model.RiskProfile;
import com.synopsys.kb.httpclient.model.VulnerabilityReference;
import com.synopsys.kb.httpclient.model.VulnerabilitySeverity;
import com.synopsys.kb.httpclient.model.VulnerabilitySource;
import com.synopsys.kb.httpclient.model.VulnerabilityStatus;

public class FastScaReportWriterTest {
	private FastScaReportWriter fastScaReportWriter;
	
	private FastScaReport fastScaReport;
	
	private Path tempDirectoryPath;
	
	@BeforeEach
	public void beforeEach() throws IOException {
		this.fastScaReportWriter = new FastScaReportWriter();
		
		this.fastScaReport = constructFastScaReport();

		tempDirectoryPath = Files.createTempDirectory(FastScaReportWriterTest.class.getSimpleName());
	}
	
	@AfterEach
	public void afterEach() throws IOException {
        try {
            Files.delete(tempDirectoryPath);
        } catch (DirectoryNotEmptyException e) {
        	File tempDirectoryFile = tempDirectoryPath.toFile();
            FileUtils.deleteDirectory(tempDirectoryFile);
        }
	}
	
	@Test
	public void testWriteToString() {
		String json = fastScaReportWriter.write(fastScaReport);
		
		Assertions.assertFalse(Strings.isNullOrEmpty(json), "JSON must not be null or empty.");
	}
	
	@Test
	public void testWriteToFile() throws IOException {
		File tempDirectoryFile = tempDirectoryPath.toFile();
		File destinationFile = new File(tempDirectoryFile, "testWriteToFile.json");
		
		try {
			fastScaReportWriter.write(fastScaReport, destinationFile);
			
			Path destinationPath = destinationFile.toPath();
			List<String> lines = Files.readAllLines(destinationPath, Charset.forName("UTF-8"));
			
			Assertions.assertFalse(lines.isEmpty(), "Lines should not be empty.");
		} finally {
			if (destinationFile != null) {
				destinationFile.delete();
			}
		}
	}
	
	private FastScaReport constructFastScaReport() {
        FastScaIdentification identification1 = constructIdentification(UUID.randomUUID(), null, null);
        FastScaIdentification identification2 = constructIdentification(UUID.randomUUID(), UUID.randomUUID(), null);
        FastScaIdentification identification3 = constructIdentification(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<FastScaIdentification> identifications = List.of(identification1, identification2, identification3);
        
        FastScaMetadata meta = new FastScaMetadata(UUID.randomUUID(), "Detect", "8.0.0", OffsetDateTime.now(), "Black Duck", "2023.7.0");
        
        return new FastScaReport(identifications, meta);
	}
	
    private FastScaIdentification constructIdentification(UUID componentId, @Nullable UUID componentVersionId, @Nullable UUID componentOriginId) {
        FastScaComponent component = new FastScaComponent(componentId, "ComponentName", "This is a component description.", "https://www.homepage.com",
                "https://www.openhub.net");

        FastScaUpgradeGuidance upgradeGuidance = null;
        FastScaComponentVersion componentVersion = null;
        if (componentVersionId != null) {
            FastScaLicense license = new FastScaLicense(UUID.randomUUID(), "LicenseName", LicenseCodeSharing.PERMISSIVE, LicenseOwnership.OPEN_SOURCE,
                    "LN", LicenseRestriction.UNRESTRICTED);
            FastScaLicenseDefinition licenseDefinition = new FastScaLicenseDefinition(license);
            componentVersion = new FastScaComponentVersion(componentVersionId, "1.0.0", OffsetDateTime.now(), licenseDefinition);
            FastScaUpgradeGuidanceSuggestion shortTermSuggestion = new FastScaUpgradeGuidanceSuggestion(UUID.randomUUID(), null, "2.0.0", null, null,
                    new RiskProfile(0, 0, 0, 0, 0));
            FastScaUpgradeGuidanceSuggestion longTermSuggestion = new FastScaUpgradeGuidanceSuggestion(UUID.randomUUID(), null, "3.0.0", null, null,
                    new RiskProfile(0, 0, 0, 0, 0));
            upgradeGuidance = new FastScaUpgradeGuidance(componentId, UUID.randomUUID(), null, "ComponentName", "1.0.0", null, null, shortTermSuggestion,
                    longTermSuggestion);
        }

        FastScaComponentOrigin componentOrigin = null;
        if (componentOriginId != null) {
            componentOrigin = new FastScaComponentOrigin(componentOriginId, "maven", "com.synopsys:component:1.0.0", false,
                    "pkg:maven/com.synopsys/component@1.0.0");
            FastScaUpgradeGuidanceSuggestion shortTermSuggestion = new FastScaUpgradeGuidanceSuggestion(UUID.randomUUID(), UUID.randomUUID(), "2.0.0",
                    "maven", "com.synopsys:component:2.0.0", new RiskProfile(0, 0, 0, 0, 0));
            FastScaUpgradeGuidanceSuggestion longTermSuggestion = new FastScaUpgradeGuidanceSuggestion(UUID.randomUUID(), UUID.randomUUID(), "3.0.0",
                    "maven", "com.synopsys:component:3.0.0", new RiskProfile(0, 0, 0, 0, 0));
            upgradeGuidance = new FastScaUpgradeGuidance(componentId, UUID.randomUUID(), UUID.randomUUID(), "ComponentName", "1.0.0", "maven",
                    "com.synopsys:component:1.0.0", shortTermSuggestion, longTermSuggestion);
        }

        Collection<FastScaVulnerability> vulnerabilities = new ArrayList<>();
        if (componentVersionId != null || componentOriginId != null) {
            for (int i = 0; i < 5; i++) {
                Cvss2TemporalMetrics cvss2TemporalMetrics = new Cvss2TemporalMetrics(1.0d, Cvss2Exploitability.NOT_DEFINED, Cvss2RemediationLevel.NOT_DEFINED,
                        Cvss2ReportConfidence.NOT_DEFINED);
                Cvss2Score cvss2Score = new Cvss2Score(1.0d, 1.0d, 1.0d, VulnerabilitySeverity.LOW, Cvss2AccessVector.ADJACENT_NETWORK,
                        Cvss2AccessComplexity.LOW,
                        Cvss2Authentication.NONE, Cvss2ConfidentialityImpact.NONE, Cvss2IntegrityImpact.NONE, Cvss2AvailabilityImpact.NONE,
                        VulnerabilitySource.BDSA, "vector", cvss2TemporalMetrics);
                Cvss3TemporalMetrics cvss3TemporalMetrics = new Cvss3TemporalMetrics(1.0d, Cvss3ExploitCodeMaturity.NOT_DEFINED,
                        Cvss3RemediationLevel.NOT_DEFINED,
                        Cvss3ReportConfidence.NOT_DEFINED);
                Cvss3Score cvss3Score = new Cvss3Score(1.0d, 1.0d, 1.0d, VulnerabilitySeverity.LOW, Cvss3AttackVector.LOCAL, Cvss3AttackComplexity.LOW,
                        Cvss3ConfidentialityImpact.LOW, Cvss3IntegrityImpact.LOW, Cvss3AvailabilityImpact.LOW, Cvss3PrivilegesRequired.LOW,
                        Cvss3Scope.UNCHANGED,
                        Cvss3UserInteraction.NONE, VulnerabilitySource.BDSA, "vector", cvss3TemporalMetrics);
                VulnerabilityReference reference = new VulnerabilityReference("source", "https://www.source.com", "35710", "UNKNOWN");
                List<VulnerabilityReference> references = List.of(reference);
                FastScaVulnerability vulnerability = new FastScaVulnerability("BDSA-2023-000" + (i + 1), VulnerabilitySource.BDSA, "This is a title.",
                        "This is a description.", "This is a technical description.", "This is a workaround.", "This is a solution.", OffsetDateTime.now(),
                        OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(), cvss2Score, cvss3Score,
                        references, VulnerabilityStatus.AFFECTED, Set.of("RCE"),
                        new FastScaRelatedVulnerability(VulnerabilitySource.NVD, "CVE-2023-000" + (i + 1)));
                vulnerabilities.add(vulnerability);
            }
        }

        Collection<FastScaMatch> matches = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<String> dependencyTree = List.of("project", "subproject", "com.synopsys:component:1.0.0");
            FastScaMatch match = new FastScaMatch(FastScaMatchType.DIRECT_DEPENDENCY, dependencyTree);
            matches.add(match);
        }

        return new FastScaIdentification(component, componentVersion, componentOrigin, upgradeGuidance, vulnerabilities, matches);
    }
}
