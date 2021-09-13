/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conda.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;

public class CondaVersionMap {
    private static final String PYPI_CHANNEL = "pypi";
    private Map<String, BiFunction<CondaListElement, String, String>> versionMap;

    private BiFunction<CondaListElement, String, String> DEFAULT_VERSION_FUNCTION = (element, platform) -> String.format("%s-%s-%s", element.version, element.buildString, platform);

    public CondaVersionMap() {
        Map<String, BiFunction<CondaListElement, String, String>> versionMap = new HashMap<>();

        BiFunction<CondaListElement, String, String> pypiVersionFunction = (element, platform) -> element.version;
        versionMap.put(PYPI_CHANNEL, pypiVersionFunction);

        this.versionMap = versionMap;
    }

    //TODO- delete one of these methods

    public String getVersionIfElse(CondaListElement element, String platform) {
        if (element.channel.equals(PYPI_CHANNEL)) {
            return element.version;
        } else {
            return String.format("%s-%s-%s", element.version, element.buildString, platform);
        }
    }

    public String getVersionMap(CondaListElement element, String platform) {
        return versionMap.getOrDefault(element.channel, DEFAULT_VERSION_FUNCTION).apply(element, platform);
    }

}
