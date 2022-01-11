package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class LicenseManifestParser {
    private static final String RECIPE_NAME_KEY = "RECIPE NAME";
    private static final String PACKAGE_VERSION_KEY = "PACKAGE VERSION";

    public Map<String, String> collectImageRecipes(List<String> licenseManifestLines) throws IntegrationException {
        Map<String, String> imageRecipes = new HashMap<>(1 + (licenseManifestLines.size()/5));
        NameVersion recipeNameVersion = new NameVersion();
        int lineNumber = 0;
        for (String line : licenseManifestLines) {
            lineNumber++;
            if (aboutToStartNewRecipe(line)) {
                recipeNameVersion = new NameVersion();
                continue;
            }
            Map.Entry<String, String> currentLineKeyValuePair = getKeyValuePair(line);
            if (RECIPE_NAME_KEY.equals(currentLineKeyValuePair.getKey())) {
                recipeNameVersion.setName(currentLineKeyValuePair.getValue());
            } else if (PACKAGE_VERSION_KEY.equals(currentLineKeyValuePair.getKey())) {
                recipeNameVersion.setVersion(currentLineKeyValuePair.getValue());
            }
            if (recipeIsComplete(recipeNameVersion)) {
                if ((imageRecipes.containsKey(recipeNameVersion.getName())) && (!imageRecipes.get(recipeNameVersion.getName()).equals(recipeNameVersion.getVersion()))) {
                    throw new IntegrationException(String.format("Error parsing license.manifest file: Recipe %s: Found version %s near line %d, but previously found version: %s", recipeNameVersion.getName(), recipeNameVersion.getVersion(), lineNumber, imageRecipes.get(recipeNameVersion.getName())));
                }
                imageRecipes.put(recipeNameVersion.getName(), recipeNameVersion.getVersion());
                recipeNameVersion = new NameVersion();
            }
        }
        return imageRecipes;
    }

    private boolean recipeIsComplete(NameVersion recipeNameVersion) {
        return (recipeNameVersion.getName() != null) && (recipeNameVersion.getVersion() != null);
    }

    private boolean aboutToStartNewRecipe(String line) {
        return StringUtils.isBlank(line.trim());
    }

    private Map.Entry<String, String> getKeyValuePair(String line) throws IntegrationException {
        String trimmedLine = line.trim();
        if (!trimmedLine.contains(":")) {
            throw new IntegrationException(String.format("Unexpected line format in license.manifest file: %s", trimmedLine));
        }
        String[] lineParts = trimmedLine.split(":\\s*");
        if (lineParts.length != 2) {
            throw new IntegrationException(String.format("Unexpected line format in license.manifest file: %s", trimmedLine));
        }
        return new AbstractMap.SimpleEntry<>(lineParts[0], lineParts[1]);
    }
}
