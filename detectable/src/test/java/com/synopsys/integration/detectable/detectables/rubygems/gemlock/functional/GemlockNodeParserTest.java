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
public class GemlockNodeParserTest {

    @Test
    public void testParsingEqualsGemfileLock() throws MissingExternalIdException {
        List<String> gemfileLockContents = Arrays.asList(
            "GEM",
            "  remote: https://artifactory.ilabs.io/artifactory/api/gems/gem-public/",
            "  specs:",
            "    SyslogLogger (2.0)",
            "    activesupport (4.1.15)",
            "      json (~> 1.7, >= 1.7.7)",
            "    json (1.8.6)",
            "",
            "PLATFORMS",
            "  ruby",
            "",
            "DEPENDENCIES",
            "  SyslogLogger (~> 2.0)",
            "  activesupport (~> 4.1.15)"
        );
        GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("activesupport", "4.1.15");
        graphAssert.hasRootDependency("SyslogLogger", "2.0");
        graphAssert.hasParentChildRelationship("activesupport", "4.1.15", "json", "1.8.6");
    }

    @Test
    public void testMissingVersionsGemfileLock() throws MissingExternalIdException {
        List<String> gemfileLockContents = Arrays.asList(
            "GEM",
            "  remote: https://artifactory.rds.lexmark.com/artifactory/api/gems/rubygems/",
            "  remote: https://artifactory.rds.lexmark.com/artifactory/api/gems/enterprise-gem-IDP/",
            "  specs:",
            "    activesupport (4.2.7.1)",
            "      thread_safe (~> 0.3, >= 0.3.4)",
            "      tzinfo (~> 1.1)",
            "    bullet (5.5.0)",
            "      activesupport (>= 3.0.0)",
            "      uniform_notifier (~> 1.10.0)",
            "    devise (4.2.1)",
            "    thread_safe (0.3.6-java)",
            "    tzinfo (1.2.3)",
            "      thread_safe (~> 0.1)",
            "    uniform_notifier (1.10.0)",
            "",
            "PLATFORMS",
            "  java",
            "",
            "DEPENDENCIES",
            "  bullet (~> 5.5.0)",
            "  devise (~> 4.2.1)"
        );
        GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("bullet", "5.5.0");
        graphAssert.hasRootDependency("devise", "4.2.1");
        graphAssert.hasParentChildRelationship("bullet", "5.5.0", "activesupport", "4.2.7.1");
        graphAssert.hasParentChildRelationship("activesupport", "4.2.7.1", "tzinfo", "1.2.3");
        graphAssert.hasParentChildRelationship("tzinfo", "1.2.3", "thread_safe", "0.3.6-java");
        graphAssert.hasParentChildRelationship("bullet", "5.5.0", "uniform_notifier", "1.10.0");
    }
}
