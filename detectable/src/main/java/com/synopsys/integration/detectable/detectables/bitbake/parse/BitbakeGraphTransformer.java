package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.ExcludedDependencyTypeFilter;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDependencyType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeNode;

public class BitbakeGraphTransformer {
    private static final String NATIVE_SUFFIX = "-native";
    public static final String VERSION_WITH_EPOCH_PREFIX_REGEX = "^[0-9]+:.*";
    public static final String VIRTUAL_PREFIX = "virtual/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public BitbakeGraphTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(BitbakeGraph bitbakeGraph, Map<String, String> recipeLayerMap, Map<String, String> imageRecipes, ExcludedDependencyTypeFilter<BitbakeDependencyType> excludedDependencyTypeFilter) {
        Map<String, Dependency> namesToExternalIds = generateExternalIds(bitbakeGraph, recipeLayerMap, imageRecipes, excludedDependencyTypeFilter);
        return buildGraph(bitbakeGraph, namesToExternalIds);
    }

    @NotNull
    private Map<String, Dependency> generateExternalIds(final BitbakeGraph bitbakeGraph, final Map<String, String> recipeLayerMap, final Map<String, String> imageRecipes, ExcludedDependencyTypeFilter<BitbakeDependencyType> excludedDependencyTypeFilter) {
        Map<String, Dependency> namesToExternalIds = new HashMap<>();
        for (BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            String name = bitbakeNode.getName();

            if (bitbakeNode.getVersion().isPresent()) {
                String version = bitbakeNode.getVersion().get();
                if (excludedDependencyTypeFilter.shouldReportDependencyType(BitbakeDependencyType.BUILD) || !isBuildDependency(imageRecipes, name, version)) {
                    Optional<Dependency> dependency = generateExternalId(name, version, recipeLayerMap).map(Dependency::new);
                    dependency.ifPresent(value -> namesToExternalIds.put(bitbakeNode.getName(), value));
                }
            } else if (name.startsWith(VIRTUAL_PREFIX)) {
                logger.debug("Virtual component '{}' found. Excluding from graph.", name);
            } else {
                logger.debug("No version found for component '{}'. It is likely not a real component.", name);
            }
        }
        return namesToExternalIds;
    }

    @NotNull
    private MutableDependencyGraph buildGraph(final BitbakeGraph bitbakeGraph, final Map<String, Dependency> namesToExternalIds) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            String name = bitbakeNode.getName();

            if (namesToExternalIds.containsKey(name)) {
                Dependency dependency = namesToExternalIds.get(bitbakeNode.getName());
                dependencyGraph.addChildToRoot(dependency);

                for (String child : bitbakeNode.getChildren()) {
                    if (namesToExternalIds.containsKey(child)) {
                        Dependency childDependency = namesToExternalIds.get(child);
                        dependencyGraph.addParentWithChild(dependency, childDependency);
                    }
                }
            }
        }
        return dependencyGraph;
    }

    private boolean isBuildDependency(Map<String, String> imageRecipes, String recipeName, String recipeVersion) {
        if (foundInImageRecipes(imageRecipes, recipeName, recipeVersion)) {
            if (recipeName.endsWith(NATIVE_SUFFIX)) {
                logger.trace("{} classified as a build dependency due to it's '{}' suffix", recipeName, NATIVE_SUFFIX);
                return true;
            } else {
                return false;
            }
        } else {
            logger.trace("{} classified as a build dependency since it was not found in the license manifest", recipeName);
            return true;
        }
    }

    private boolean foundInImageRecipes(Map<String, String> imageRecipes, String recipeName, String recipeVersion) {
        if (imageRecipes.containsKey(recipeName)) {
            String imageRecipeVersion = imageRecipes.get(recipeName);
            String epochlessRecipeVersion = removeEpochPrefix(recipeVersion);
            if (epochlessRecipeVersion.startsWith(imageRecipeVersion)) {
                return true;
            } else {
                logger.debug("Recipe {}/{} is included in the image, but version {} is not.", recipeName, imageRecipeVersion, recipeVersion);
            }
        }
        return false;
    }

    private String removeEpochPrefix(final String recipeVersion) {
        String epochlessRecipeVersion = recipeVersion;
        if (recipeVersion.matches(VERSION_WITH_EPOCH_PREFIX_REGEX)) {
            int colonPos = recipeVersion.indexOf(':');
            epochlessRecipeVersion = recipeVersion.substring(colonPos+1);
            logger.trace("epochlessVersion for {}: {}", recipeVersion, epochlessRecipeVersion);
        }
        return epochlessRecipeVersion;
    }

    private Optional<ExternalId> generateExternalId(String dependencyName, String dependencyVersion, Map<String, String> recipeLayerMap) {
        String priorityLayerName = recipeLayerMap.get(dependencyName);
        ExternalId externalId = null;

        if (priorityLayerName != null) {
            externalId = externalIdFactory.createYoctoExternalId(priorityLayerName, dependencyName, dependencyVersion);
        } else {
            logger.debug("Failed to find component '{}' in component layer map.", dependencyName);
            if (dependencyName.endsWith(NATIVE_SUFFIX)) {
                String alternativeName = dependencyName.replace(NATIVE_SUFFIX, "");
                logger.debug("Generating alternative component name '{}' for '{}=={}'", alternativeName, dependencyName, dependencyVersion);
                externalId = generateExternalId(alternativeName, dependencyVersion, recipeLayerMap).orElse(null);
            } else {
                logger.debug("'{}=={}' is not an actual component. Excluding from graph.", dependencyName, dependencyVersion);
            }
        }

        if (externalId != null && externalId.getVersion().contains("AUTOINC")) {
            externalId.setVersion(externalId.getVersion().replaceFirst("AUTOINC\\+[\\w|\\d]*", "X"));
        }

        return Optional.ofNullable(externalId);
    }
}
