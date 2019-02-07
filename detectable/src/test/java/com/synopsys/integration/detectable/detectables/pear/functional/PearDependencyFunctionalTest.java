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
package com.synopsys.integration.detectable.detectables.pear.functional;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectables.pear.PearParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class PearDependencyFunctionalTest {
    private PearParser pearParser;

    @BeforeAll
    public void init() {
        pearParser = new PearParser(new ExternalIdFactory());
    }

    @Test
    public void findDependencyNamesTest() {
        final String dependenciesList = FunctionalTestFiles.asString("/pear/dependencies-list.txt");
        final ExecutableOutput exeOutput = new ExecutableOutput(dependenciesList, "");

        final List<String> actual = pearParser.findDependencyNames(exeOutput.getStandardOutputAsList(), true);
        final List<String> expected = Arrays.asList(
            "Archive_Tar",
            "Structures_Graph",
            "Console_Getopt",
            "XML_Util",
            "PEAR_Frontend_Web",
            "PEAR_Frontend_Gtk",
            "xml",
            "pcre"
        );

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createPearDependencyNodeFromListTest() {
        final String installedPackages = FunctionalTestFiles.asString("/pear/installed-packages.txt");
        final ExecutableOutput exeOutput = new ExecutableOutput(installedPackages, "");

        final List<String> dependencyNames = Arrays.asList(
            "Archive_Tar",
            "Console_Getopt",
            "Structures_Graph"
        );
        final DependencyGraph actual = pearParser.createPearDependencyGraphFromList(exeOutput.getStandardOutputAsList(), dependencyNames);

        GraphAssert.assertGraph("/pear/dependency-node-list_graph.json", actual);
    }
}
