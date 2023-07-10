package com.synopsys.integration.detectable.detectables.cocoapods.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cocoapods.model.Pod;
import com.synopsys.integration.detectable.detectables.cocoapods.model.PodSource;
import com.synopsys.integration.detectable.detectables.cocoapods.model.PodfileLock;
import java.util.Collection;
import java.util.stream.Stream;

public class PodlockParser {
    private static final List<String> fuzzyVersionIdentifiers = new ArrayList<>(Arrays.asList(">", "<", "~>", "="));

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public PodlockParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph extractDependencyGraph(String podLockText) throws IOException, MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        YAMLMapper mapper = new YAMLMapper();
        PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class);

        Map<LazyId, Forge> forgeOverrides = createForgeOverrideMap(podfileLock);

        List<String> knownPods = determineAllPodNames(podfileLock);
        if (podfileLock.getPods() != null) {
            for (Pod pod : podfileLock.getPods()) {
                logger.trace(String.format("Processing pod %s", pod.getName()));
                processPod(pod, forgeOverrides, lazyBuilder, knownPods);
            }
        }

        if (podfileLock.getDependencies() != null) {
            for (Pod dependency : podfileLock.getDependencies()) {
                logger.trace(String.format("Processing pod dependency from pod lock file %s", dependency.getName()));
                String podText = dependency.getName();
                Optional<LazyId> dependencyId = parseDependencyId(podText);
                dependencyId.ifPresent(lazyBuilder::addChildToRoot);
            }
        }
        logger.trace("Attempting to build the dependency graph.");
        DependencyGraph dependencyGraph = lazyBuilder.build();
        logger.trace("Completed the dependency graph.");
        return dependencyGraph;
    }

    /*
     * Create an override map because GitHub has better KB support so we should override COCOAPODS forge when we know where it is from.
     */
    private Map<LazyId, Forge> createForgeOverrideMap(PodfileLock podfileLock) {
        Map<LazyId, Forge> forgeOverrideMap = new HashMap<>();
        if (null != podfileLock.getExternalSources()) {
            List<PodSource> podSources = podfileLock.getExternalSources().getSources();
            for (PodSource podSource : podSources) {
                Optional<LazyId> dependencyId = parseDependencyId(podSource.getName());
                if (dependencyId.isPresent()) {
                    if (null != podSource.getGit() && podSource.getGit().contains("github")) {
                        forgeOverrideMap.put(dependencyId.get(), Forge.COCOAPODS);
                    } else if (null != podSource.getPath() && podSource.getPath().contains("node_modules")) {
                        forgeOverrideMap.put(dependencyId.get(), Forge.NPMJS);
                    }
                }
            }
        }

        return forgeOverrideMap;
    }

    private Forge getForge(LazyId dependencyId, Map<LazyId, Forge> forgeOverrides) {
        if (forgeOverrides.containsKey(dependencyId)) {
            return forgeOverrides.get(dependencyId);
        }

        return Forge.COCOAPODS;
    }

    private List<String> determineAllPodNames(PodfileLock podfileLock) {
        return Optional.ofNullable(podfileLock.getPods())
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(Pod::getName)
            .map(this::parseRawPodName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private void processPod(Pod pod, Map<LazyId, Forge> forgeOverrides, LazyExternalIdDependencyGraphBuilder lazyBuilder, List<String> knownPods) {
        String podText = pod.getName();
        Optional<LazyId> dependencyIdMaybe = parseDependencyId(podText);
        String name = parseCorrectPodName(podText).orElse(null);
        String version = parseVersion(podText).orElse(null);
        if (dependencyIdMaybe.isPresent()) {
            LazyId dependencyId = dependencyIdMaybe.get();

            Forge forge = getForge(dependencyId, forgeOverrides);
            ExternalId externalId = externalIdFactory.createNameVersionExternalId(forge, name, version);

            lazyBuilder.setDependencyInfo(dependencyId, name, version, externalId);

            for (String child : pod.getDependencies()) {
                logger.trace(String.format("Processing pod dependency %s", child));
                Optional<LazyId> childId = parseDependencyId(child);
                Optional<String> childName = parseRawPodName(child);
                if (childId.isPresent() && childName.isPresent() && !dependencyId.equals(childId.get())) {
                    //Some transitives may appear but are not actually present in the pod list.
                    //This supposedly happens because platform specific transitives are present but not actually used.
                    //So here if a transitive appears but is not actually a pod, we filter it out.
                    if (knownPods.contains(childName.get())) {
                        lazyBuilder.addParentWithChild(dependencyId, childId.get());
                    } else {
                        logger.info("Transitive POD not included because it is not an actual POD: " + childName.toString());
                    }
                }
            }
        }
    }

    private Optional<String> parseCorrectPodName(String podText) {
        // due to the way the KB deals with subspecs we should use the super name if it exists as this pod's name.
        Optional<String> podName = parseRawPodName(podText);
        if (podName.isPresent()) {
            Optional<String> superPodName = parseSuperPodName(podName.get());
            if (superPodName.isPresent()) {
                return superPodName;
            } else {
                return podName;
            }
        }

        return Optional.empty();
    }

    private Optional<String> parseSuperPodName(String podName) {
        if (podName.contains("/")) {
            return Optional.of(podName.split("/")[0].trim());
        }

        return Optional.empty();
    }

    private Optional<LazyId> parseDependencyId(String podText) {
        Optional<String> name = parseCorrectPodName(podText);

        return name.map(LazyId::fromName);
    }

    private Optional<String> parseVersion(String podText) {
        String[] segments = podText.split(" ");
        if (segments.length > 1) {
            String version = segments[1];
            version = version.replace("(", "").replace(")", "").trim();
            if (!isVersionFuzzy(version)) {
                return Optional.of(version);
            }
        }

        return Optional.empty();
    }

    private boolean isVersionFuzzy(String versionName) {
        for (String identifier : fuzzyVersionIdentifiers) {
            if (versionName.contains(identifier)) {
                return true;
            }
        }

        return false;
    }

    private Optional<String> parseRawPodName(String podText) {
        if (StringUtils.isNotBlank(podText)) {
            return Optional.of(podText.split(" ")[0].trim());
        }

        return Optional.empty();
    }

}
