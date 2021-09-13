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

import com.synopsys.integration.bdio.model.Forge;

public class CondaForgeMap {
    private final Map<String, Forge> forgeMap;

    private static final String PYPI_CHANNEL = "pypi";
    private static final Forge DEFAULT_FORGE = Forge.ANACONDA;

    public CondaForgeMap() {
        Map<String, Forge> forgeMap = new HashMap<>();
        forgeMap.put(PYPI_CHANNEL, Forge.PYPI);

        this.forgeMap = forgeMap;
    }

    public Forge getForge(String channel) {
        return forgeMap.getOrDefault(channel, DEFAULT_FORGE);
    }
}
