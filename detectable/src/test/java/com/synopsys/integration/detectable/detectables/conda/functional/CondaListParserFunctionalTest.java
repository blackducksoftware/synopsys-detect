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
package com.synopsys.integration.detectable.detectables.conda.functional;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.conda.CondaListParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphAssert;

@FunctionalTest
public class CondaListParserFunctionalTest {
    private CondaListParser condaListParser;

    @Before
    public void init() {
        condaListParser = new CondaListParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void smallParseTest() {
        final String condaInfoJson = FunctionalTestFiles.asString("/conda/condaInfo.json");
        final String condaListJson = FunctionalTestFiles.asString("/conda/condaListSmall.json");
        final DependencyGraph dependencyGraph = condaListParser.parse(condaListJson, condaInfoJson);

        GraphAssert.assertGraph("/conda/condaListSmallExpected_graph.json", dependencyGraph);
    }

    @Test
    public void largeParseTest() {
        final String condaInfoJson = FunctionalTestFiles.asString("/conda/condaInfo.json");
        final String condaListJson = FunctionalTestFiles.asString("/conda/condaListLarge.json");
        final DependencyGraph dependencyGraph = condaListParser.parse(condaListJson, condaInfoJson);

        GraphAssert.assertGraph("/conda/condaListLargeExpected_graph.json", dependencyGraph);
    }
}
