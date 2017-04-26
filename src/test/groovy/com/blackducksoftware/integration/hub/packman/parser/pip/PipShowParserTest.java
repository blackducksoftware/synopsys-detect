/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.parser.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.blackducksoftware.integration.hub.packman.packagemanager.pip.model.PipPackage;
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.parsers.PipShowParser;

public class PipShowParserTest {

    @Test
    public void pipShowParserTest() throws IOException {
        final PipShowParser pipShowParser = new PipShowParser();
        final String sampleText = IOUtils.toString(getClass().getResourceAsStream("/pip/pipShowSample.txt"), StandardCharsets.UTF_8);
        final PipPackage pipPackage = pipShowParser.parse(sampleText);

        final List<String> expectedRequirements = new ArrayList<>();
        expectedRequirements.add("Delorean");
        expectedRequirements.add("pynamodb");

        assertEquals("blackduck-sample-project", pipPackage.name);
        assertEquals("0.0.9", pipPackage.version);
        assertEquals(expectedRequirements.size(), pipPackage.requires.size());

        for (final String requirement : pipPackage.requires) {
            assertTrue(expectedRequirements.contains(requirement));
        }
    }
}
