package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class LicenseManifestParser {


    public Map<String, String> collectImageRecipes(List<String> licenseManifestLines) throws IntegrationException {
        Map<String, String> imageRecipes = new HashMap<>(1 + (licenseManifestLines.size()/5));
        NameVersion recipeNameVersion = new NameVersion();
        int lineNumber = 0;
        for (String line : licenseManifestLines) {
            lineNumber++;
            String trimmedLine = line.trim();
            if (StringUtils.isBlank(trimmedLine)) {
                recipeNameVersion = new NameVersion();
                continue;
            }
            parseValueFromLine(recipeNameVersion, trimmedLine);
            if ((recipeNameVersion.getName() != null) && (recipeNameVersion.getVersion() != null)) {
                if ((imageRecipes.containsKey(recipeNameVersion.getName())) && (!imageRecipes.get(recipeNameVersion.getName()).equals(recipeNameVersion.getVersion()))) {
                    throw new IntegrationException(String.format("Error parsing license.manifest file: Recipe %s: Found version %s near line %d, but previously found version: %s", recipeNameVersion.getName(), recipeNameVersion.getVersion(), lineNumber, imageRecipes.get(recipeNameVersion.getName())));
                }
                imageRecipes.put(recipeNameVersion.getName(), recipeNameVersion.getVersion());
                recipeNameVersion = new NameVersion();
            }
        }
        return imageRecipes;
    }

    private void parseValueFromLine(NameVersion recipeNameVersion, String trimmedLine) throws IntegrationException {
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
            recipeNameVersion.setName(value);
        } else if ("PACKAGE VERSION".equals(key)) {
            recipeNameVersion.setVersion(value);
        }
    }

}
