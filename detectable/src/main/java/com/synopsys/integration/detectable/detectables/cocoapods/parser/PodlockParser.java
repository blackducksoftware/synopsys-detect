/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cocoapods.model.Pod;
import com.synopsys.integration.detectable.detectables.cocoapods.model.PodSource;
import com.synopsys.integration.detectable.detectables.cocoapods.model.PodfileLock;

public class PodlockParser {
    private static final List<String> fuzzyVersionIdentifiers = new ArrayList<>(Arrays.asList(">", "<", "~>", "="));

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public PodlockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph extractDependencyGraph(final String podLockText) throws IOException, MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        final YAMLMapper mapper = new YAMLMapper();
        final PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class);

        final Map<DependencyId, Forge> forgeOverrides = createForgeOverrideMap(podfileLock);

        List<String> knownPods = determineAllPodNames(podfileLock);
        for (final Pod pod : podfileLock.getPods()) {
            logger.trace(String.format("Processing pod %s", pod.getName()));
            processPod(pod, forgeOverrides, lazyBuilder, knownPods);
        }

        for (final Pod dependency : podfileLock.getDependencies()) {
            logger.trace(String.format("Processing pod dependency from pod lock file %s", dependency.getName()));
            final String podText = dependency.getName();
            final Optional<DependencyId> dependencyId = parseDependencyId(podText);
            dependencyId.ifPresent(lazyBuilder::addChildToRoot);
        }
        logger.trace("Attempting to build the dependency graph.");
        final DependencyGraph dependencyGraph = lazyBuilder.build();
        logger.trace("Completed the dependency graph.");
        return dependencyGraph;
    }

    /*
     * Create an override map because GitHub has better KB support so we should override COCOAPODS forge when we know where it is from.
     */
    private Map<DependencyId, Forge> createForgeOverrideMap(final PodfileLock podfileLock) {
        final Map<DependencyId, Forge> forgeOverrideMap = new HashMap<>();
        if (null != podfileLock.getExternalSources()) {
            final List<PodSource> podSources = podfileLock.getExternalSources().getSources();
            for (final PodSource podSource : podSources) {
                final Optional<DependencyId> dependencyId = parseDependencyId(podSource.getName());
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

    private Forge getForge(final DependencyId dependencyId, final Map<DependencyId, Forge> forgeOverrides) {
        if (forgeOverrides.containsKey(dependencyId)) {
            return forgeOverrides.get(dependencyId);
        }

        return Forge.COCOAPODS;
    }

    private List<String> determineAllPodNames(PodfileLock podfileLock) {
        return podfileLock.getPods().stream()
                   .map(Pod::getName)
                   .map(this::parseRawPodName)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .collect(Collectors.toList());
    }

    private void processPod(final Pod pod, final Map<DependencyId, Forge> forgeOverrides, final LazyExternalIdDependencyGraphBuilder lazyBuilder, List<String> knownPods) {
        final String podText = pod.getName();
        final Optional<DependencyId> dependencyIdMaybe = parseDependencyId(podText);
        final String name = parseCorrectPodName(podText).orElse(null);
        final String version = parseVersion(podText).orElse(null);
        if (dependencyIdMaybe.isPresent()) {
            final DependencyId dependencyId = dependencyIdMaybe.get();

            final Forge forge = getForge(dependencyId, forgeOverrides);
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(forge, name, version);

            lazyBuilder.setDependencyInfo(dependencyId, name, version, externalId);

            for (final String child : pod.getDependencies()) {
                logger.trace(String.format("Processing pod dependency %s", child));
                final Optional<DependencyId> childId = parseDependencyId(child);
                final Optional<String> childName = parseRawPodName(child);
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

    private Optional<String> parseCorrectPodName(final String podText) {
        // due to the way the KB deals with subspecs we should use the super name if it exists as this pod's name.
        final Optional<String> podName = parseRawPodName(podText);
        if (podName.isPresent()) {
            final Optional<String> superPodName = parseSuperPodName(podName.get());
            if (superPodName.isPresent()) {
                return superPodName;
            } else {
                return podName;
            }
        }

        return Optional.empty();
    }

    private Optional<String> parseSuperPodName(final String podName) {
        if (podName.contains("/")) {
            return Optional.of(podName.split("/")[0].trim());
        }

        return Optional.empty();
    }

    private Optional<DependencyId> parseDependencyId(final String podText) {
        final Optional<String> name = parseCorrectPodName(podText);

        return name.map(NameDependencyId::new);
    }

    private Optional<String> parseVersion(final String podText) {
        final String[] segments = podText.split(" ");
        if (segments.length > 1) {
            String version = segments[1];
            version = version.replace("(", "").replace(")", "").trim();
            if (!isVersionFuzzy(version)) {
                return Optional.of(version);
            }
        }

        return Optional.empty();
    }

    private boolean isVersionFuzzy(final String versionName) {
        for (final String identifier : fuzzyVersionIdentifiers) {
            if (versionName.contains(identifier)) {
                return true;
            }
        }

        return false;
    }

    private Optional<String> parseRawPodName(final String podText) {
        if (StringUtils.isNotBlank(podText)) {
            return Optional.of(podText.split(" ")[0].trim());
        }

        return Optional.empty();
    }

}
