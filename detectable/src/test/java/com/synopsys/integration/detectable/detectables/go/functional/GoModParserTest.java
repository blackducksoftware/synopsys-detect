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
package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModGraphParser;
import com.synopsys.integration.detectable.detectables.go.vendr.parse.VndrParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.detectable.util.graph.GraphAssert;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class GoModParserTest {
    @Test
    public void parsesAthensExample() throws IOException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        List<String> listCommand = FunctionalTestFiles.asListOfStrings("/go/gomod/gomod_list_athens.txt");
        List<String> graphCommand = FunctionalTestFiles.asListOfStrings("/go/gomod/gomod_go_mod_graph_athens.txt");

        GoModGraphParser goModGraphParser = new GoModGraphParser(externalIdFactory);
        final List<CodeLocation> codeLocations = goModGraphParser.parseListAndGoModGraph(listCommand, graphCommand);

        Assert.assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, codeLocation.getDependencyGraph());
        graphAssert.hasRootDependency("github.com/codegangsta/negroni", "v1.0.0");
        graphAssert.hasRootDependency("github.com/sirupsen/logrus", "v1.1.1");
        graphAssert.hasParentChildRelationship("github.com/sirupsen/logrus", "v1.1.1", "github.com/davecgh/go-spew", "v1.1.1");
    }
}
