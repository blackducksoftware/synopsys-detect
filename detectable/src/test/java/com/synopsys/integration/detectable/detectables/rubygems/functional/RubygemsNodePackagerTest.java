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
package com.synopsys.integration.detectable.detectables.rubygems.functional;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.GemlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class RubygemsNodePackagerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void packagerTest() {
        final List<String> actualText = FunctionalTestFiles.asListOfStrings("/rubygems/Gemfile.lock");
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph projects = rubygemsNodePackager.parseProjectDependencies(actualText);
        Assert.assertEquals(8, projects.getRootDependencies().size());

        GraphAssert.assertGraph("/rubygems/expectedPackager_graph.json", projects);
    }

    @Test
    void findsAllVersions() {
        //Finds all versions of the package not just the first matching architecture.
        final List<String> actualText = FunctionalTestFiles.asListOfStrings("/rubygems/Gemfile-rails.lock");
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph graph = rubygemsNodePackager.parseProjectDependencies(actualText);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, graph);
        graphAssert.noDependency(createExternalId("nokogiri", ""));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2"));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2-java"));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2-x86-mingw32"));
    }

    private ExternalId createExternalId(final String name, final String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version);
    }
}
