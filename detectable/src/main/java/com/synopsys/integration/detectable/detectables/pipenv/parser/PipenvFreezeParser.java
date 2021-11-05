/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pipenv.parser;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreezeEntry;

public class PipenvFreezeParser {
    private static final String VERSION_SEPARATOR = "==";

    public PipFreeze parse(List<String> pipFreezeOutput) {
        List<PipFreezeEntry> entries = pipFreezeOutput.stream()
            .map(line -> line.split(VERSION_SEPARATOR))
            .filter(pieces -> pieces.length == 2)
            .map(pieces -> new PipFreezeEntry(pieces[0], pieces[1]))
            .collect(Collectors.toList());

        return new PipFreeze(entries);
    }
}
