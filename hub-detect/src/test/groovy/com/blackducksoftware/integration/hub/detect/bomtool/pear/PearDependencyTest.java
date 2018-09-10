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
package com.blackducksoftware.integration.hub.detect.bomtool.pear;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PearDependencyTest {
    private DetectConfiguration detectConfiguration;
    private PearParser pearParser;

    private TestUtil testUtil;

    @Before
    public void init() {
        detectConfiguration = Mockito.mock(DetectConfiguration.class);
        pearParser = new PearParser(new ExternalIdFactory(), detectConfiguration);
        testUtil = new TestUtil();
    }

    @Test
    public void findDependencyNamesTest() {
        Mockito.when(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PEAR_ONLY_REQUIRED_DEPS)).thenReturn(true);

        final String dependenciesList = testUtil.getResourceAsUTF8String("/pear/dependencies-list.txt");
        final ExecutableOutput exeOutput = new ExecutableOutput(dependenciesList, "");

        final List<String> actual = pearParser.findDependencyNames(exeOutput.getStandardOutputAsList());
        final List<String> expected = Arrays.asList(
                "Archive_Tar",
                "Structures_Graph",
                "Console_Getopt",
                "XML_Util",
                "PEAR_Frontend_Web",
                "PEAR_Frontend_Gtk",
                "xml",
                "pcre");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createPearDependencyNodeFromListTest() {
        final String installedPackages = testUtil.getResourceAsUTF8String("/pear/installed-packages.txt");
        final ExecutableOutput exeOutput = new ExecutableOutput(installedPackages, "");

        final List<String> dependencyNames = Arrays.asList(
                "Archive_Tar",
                "Console_Getopt",
                "Structures_Graph");
        final DependencyGraph actual = pearParser.createPearDependencyGraphFromList(exeOutput.getStandardOutputAsList(), dependencyNames);

        DependencyGraphResourceTestUtil.assertGraph("/pear/dependency-node-list_graph.json", actual);
    }
}
