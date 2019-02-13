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
package com.synopsys.integration.detectable.detectables.cpan.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class CpanListParserFunctionalTest {
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory());

    @Test
    public void getDirectModuleNamesTest() {
        final List<String> showDepsText = FunctionalTestFiles.asListOfStrings("/cpan/showDeps.txt");
        final List<String> names = cpanListParser.getDirectModuleNames(showDepsText);

        assertEquals(4, names.size());
        assertTrue(names.contains("ExtUtils::MakeMaker"));
        assertTrue(names.contains("Test::More"));
        assertTrue(names.contains("perl"));
        assertTrue(names.contains("ExtUtils::MakeMaker"));
    }

    @Test
    public void makeDependencyNodesTest() {
        final List<String> cpanListText = FunctionalTestFiles.asListOfStrings("/cpan/cpanList.txt");
        final List<String> showDepsText = FunctionalTestFiles.asListOfStrings("/cpan/showDeps.txt");

        final DependencyGraph dependencyGraph = cpanListParser.parse(cpanListText, showDepsText);

        GraphCompare.assertEqualsResource("/cpan/expectedDependencyNodes_graph.json", dependencyGraph);
    }
}
