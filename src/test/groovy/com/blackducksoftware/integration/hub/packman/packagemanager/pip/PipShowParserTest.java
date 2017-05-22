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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class PipShowParserTest {

    @Test
    public void pipShowParserTest() throws IOException {
        final PipShowMapParser pipShowParser = new PipShowMapParser();

        final String sampleText = IOUtils.toString(getClass().getResourceAsStream("/pip/pipShowSample.txt"), StandardCharsets.UTF_8);
        final Map<String, String> pipPackage = pipShowParser.parse(sampleText);
        final List<String> expectedRequirements = new ArrayList<>();
        expectedRequirements.add("Delorean");
        expectedRequirements.add("pynamodb");

        assertEquals("blackduck-sample-project", pipPackage.get("Name"));
        assertEquals("0.0.9", pipPackage.get("Version"));
        assertEquals("A sample project for using the hub-pip", pipPackage.get("Summary"));
        assertEquals("https://github.com/blackducksoftware/hub_python_plugin", pipPackage.get("Home-page"));
        assertEquals("Black Duck Software", pipPackage.get("Author"));
        assertEquals("UNKNOWN", pipPackage.get("Author-email"));
        assertEquals("Apache 2.0", pipPackage.get("License"));
        assertEquals("/usr/local/lib/python2.7/site-packages", pipPackage.get("Location"));
        assertEquals(9, pipPackage.size());

        final String[] requirements = pipPackage.get("Requires").split(",");
        assertEquals(2, expectedRequirements.size());
        for (final String requirement : requirements) {
            assertTrue(expectedRequirements.contains(requirement.trim()));
        }
    }
}
