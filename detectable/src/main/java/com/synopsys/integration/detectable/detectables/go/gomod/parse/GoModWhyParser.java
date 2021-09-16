/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoModWhyParser {
    private static final String MISSING_MODULE_PREFIX = "(main module does not need module";

    public Set<String> createModuleExclusionList(List<String> lines) {
        // find lines that look like the following and extract the module name i.e. cloud.google.com/go:
        // (main module does not need module cloud.google.com/go)
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
