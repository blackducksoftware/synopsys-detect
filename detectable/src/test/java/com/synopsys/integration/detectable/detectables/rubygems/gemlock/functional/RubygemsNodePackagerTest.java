package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
public class RubygemsNodePackagerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void findsAllVersions() throws MissingExternalIdException {
        //Finds all versions of the package not just the first matching architecture.
        List<String> actualText = Arrays.asList(
            "GEM",
            "  remote: https://rubygems.org/",
            "  specs:",
            "    nokogiri (1.8.2)",
            "      mini_portile2 (~> 2.3.0)",
            "    nokogiri (1.8.2-java)",
            "    nokogiri (1.8.2-x86-mingw32)",
            "    nokoparent (3.1.0)",
            "      nokogiri (~> 1.8)",
            "",
            "PLATFORMS",
            "  java",
            "  ruby",
            "  x86-mingw32",
            "",
            "DEPENDENCIES",
            "  nokoparent (>= 1)",
            "  nokogiri (>= 1.8.1)"
        );
        GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        DependencyGraph graph = rubygemsNodePackager.parseProjectDependencies(actualText);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, graph);

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("nokoparent", "3.1.0");
        graphAssert.hasRootDependency("nokogiri", "1.8.2");
        graphAssert.hasNoDependency("nokogiri", "");
        graphAssert.hasDependency("nokogiri", "1.8.2");
        graphAssert.hasDependency("nokogiri", "1.8.2-java");
        graphAssert.hasDependency("nokogiri", "1.8.2-x86-mingw32");
        graphAssert.hasParentChildRelationship("nokoparent", "3.1.0", "nokogiri", "1.8.2");
        graphAssert.hasParentChildRelationship("nokogiri", "1.8.2", "mini_portile2", "");
    }

}
