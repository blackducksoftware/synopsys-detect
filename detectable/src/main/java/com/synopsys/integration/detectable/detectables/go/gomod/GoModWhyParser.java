package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoModWhyParser {
    private static final String MISSING_MODULE_PREFIX = "(main module does not need module";

    public Set<String> createModuleExclusionList(List<String> lines) {
        Set<String> exclusionModules = new LinkedHashSet<>();
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith(MISSING_MODULE_PREFIX)) {
                int closingParen = trimmedLine.lastIndexOf(")");
                if (closingParen > 0 && closingParen > MISSING_MODULE_PREFIX.length()) {
                    String moduleName = trimmedLine.substring(MISSING_MODULE_PREFIX.length(), closingParen);
                    exclusionModules.add(moduleName.trim());
                }
            }
        }
        return exclusionModules;
    }
}
