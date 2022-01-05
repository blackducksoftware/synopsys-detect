package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeNode;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeGraphTransformer {
    private static final String NATIVE_SUFFIX = "-native";

    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final ExternalIdFactory externalIdFactory;

    public BitbakeGraphTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(BitbakeGraph bitbakeGraph, Map<String, String> recipeLayerMap, Map<String, String> imageRecipes, boolean includeDevDependencies) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        Map<String, Dependency> namesToExternalIds = new HashMap<>();

        for (BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            String name = bitbakeNode.getName();

            if (bitbakeNode.getVersion().isPresent()) {
                String version = bitbakeNode.getVersion().get();
                if (qualifies(includeDevDependencies, imageRecipes, name, version)) {
                    Optional<Dependency> dependency = generateExternalId(name, version, recipeLayerMap, includeDevDependencies).map(Dependency::new);
                    dependency.ifPresent(value -> namesToExternalIds.put(bitbakeNode.getName(), value));
            }
            } else if (name.startsWith("virtual/")) {
                logger.debug(String.format("Virtual component '%s' found. Excluding from graph.", name));
            } else {
                logger.debug(String.format("No version found for component '%s'. It is likely not a real component.", name));
            }
        }

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

    private boolean qualifies(boolean includeDevDependencies, Map<String, String> imageRecipes, String recipeName, String recipeVersion) {
        if (includeDevDependencies) {
            return true;
        }
        if (foundInImageRecipes(imageRecipes, recipeName, recipeVersion)) {
            return true;
        }
        logger.debug(String.format("Excluding dev dependency %s/%s", recipeName, recipeName));
        return false;
    }

    private boolean foundInImageRecipes(Map<String, String> imageRecipes, String recipeName, String recipeVersion) {
        if (imageRecipes.containsKey(recipeName)) {
            String imageRecipeVersion = imageRecipes.get(recipeName);
            String epochlessRecipeVersion = removeEpochPrefix(recipeVersion);
            if (epochlessRecipeVersion.startsWith(imageRecipeVersion)) {
                return true;
            } else {
                logger.debug(String.format("Recipe %s/%s is included in the image, but version %s is not.", recipeName, imageRecipeVersion, recipeVersion));
            }
        }
        return false;
    }

    private String removeEpochPrefix(final String recipeVersion) {
        String epochlessRecipeVersion = recipeVersion;
        if (recipeVersion.matches("^[0-9]+:.*")) {
            logger.info(String.format("%s has an epoch", recipeVersion));
            int colonPos = recipeVersion.indexOf(':');
            epochlessRecipeVersion = recipeVersion.substring(colonPos+1);
            logger.info(String.format("epochlessVersion: %s", epochlessRecipeVersion));
        }
        return epochlessRecipeVersion;
    }

    private Optional<ExternalId> generateExternalId(String dependencyName, String dependencyVersion, Map<String, String> recipeLayerMap, boolean includeDevDependencies) {
        String priorityLayerName = recipeLayerMap.get(dependencyName);
        ExternalId externalId = null;

        if (priorityLayerName != null) {
            externalId = externalIdFactory.createYoctoExternalId(priorityLayerName, dependencyName, dependencyVersion);
        } else {
            logger.debug(String.format("Failed to find component '%s' in component layer map.", dependencyName));

            if (dependencyName.endsWith(NATIVE_SUFFIX)) {
                if (includeDevDependencies) {
                    String alternativeName = dependencyName.replace(NATIVE_SUFFIX, "");
                    logger.debug(String.format("Generating alternative component name '%s' for '%s==%s'", alternativeName, dependencyName, dependencyVersion));
                    externalId = generateExternalId(alternativeName, dependencyVersion, recipeLayerMap, includeDevDependencies).orElse(null);
                } else {
                    logger.debug(String.format("Recipe %s excluded because dev dependencies are being excluded"));
                }
            } else {
                logger.debug(String.format("'%s==%s' is not an actual component. Excluding from graph.", dependencyName, dependencyVersion));
            }
        }

        if (externalId != null && externalId.getVersion().contains("AUTOINC")) {
            externalId.setVersion(externalId.getVersion().replaceFirst("AUTOINC\\+[\\w|\\d]*", "X"));
        }

        return Optional.ofNullable(externalId);
    }
}
