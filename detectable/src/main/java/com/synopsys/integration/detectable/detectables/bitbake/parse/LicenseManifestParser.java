package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;

public class LicenseManifestParser {

    public Map<String, String> collectImageRecipes(List<String> licenseManifestLines) throws IntegrationException {
        Map<String, String> imageRecipes = new HashMap<>(1 + (licenseManifestLines.size()/5));
        String recipeName = null;
        String recipeVersion = null;
        int lineNumber = 0;
        for (String line : licenseManifestLines) {
            lineNumber++;
            String trimmedLine = line.trim();
            if (StringUtils.isBlank(trimmedLine)) {
                recipeName = null;
                recipeVersion = null;
                continue;
            }
            if (!trimmedLine.contains(":")) {
                throw new IntegrationException(String.format("Unexpected line format in license.manifest file: %s", trimmedLine));
            }
            String[] lineParts = trimmedLine.split(":\\s*");
            if (lineParts.length != 2) {
                throw new IntegrationException(String.format("Unexpected line format in license.manifest file: %s", trimmedLine));
            }
            String key = lineParts[0].trim();
            String value = lineParts[1].trim();
            if ("RECIPE NAME".equals(key)) {
                recipeName = value;
            } else if ("PACKAGE VERSION".equals(key)) {
                recipeVersion = value;
            }
            if ((recipeName != null) && (recipeVersion != null)) {
                if ((imageRecipes.containsKey(recipeName)) && (!imageRecipes.get(recipeName).equals(recipeVersion))) {
                    throw new IntegrationException(String.format("Error parsing license.manifest file: Recipe %s: Found version %s near line %d, but previously found version: %s", recipeName, recipeVersion, lineNumber, imageRecipes.get(recipeName)));
                }
                imageRecipes.put(recipeName, recipeVersion);
                recipeName = null;
                recipeVersion = null;
            }
        }
        return imageRecipes;
    }
}
