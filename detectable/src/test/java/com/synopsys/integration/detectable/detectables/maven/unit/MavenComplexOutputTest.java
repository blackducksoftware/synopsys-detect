package com.synopsys.integration.detectable.detectables.maven.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCodeLocationPackager;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenParseResult;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class MavenComplexOutputTest {

    private String getInput() {
        return String.join(System.lineSeparator(), Arrays.asList(
            "[INFO] Reactor Build Order:",
            "[INFO] ",
            "[INFO] murex-parent                                                       [pom]",
            "[INFO] MX Minimal Viable Runtime :: Parent POM                            [pom]",
            "[INFO] murex.boot:boot-client:jar:v3.1.42                                 [jar]",
            "[INFO] MX FileServer :: Parent POM                                        [pom]",
            "[INFO] murex.fileserver:fileserver-client:jar:v3.1.42                     [jar]",
            "[INFO] REST Service Framework :: Parent                                   [pom]",
            "[INFO] REST Service Framework :: Logging                                  [jar]",
            "[INFO] common-parent                                                      [pom]",
            "[INFO] -------------------< murex.integration:integration >--------------------",
            "[INFO] Building murex.integration:integration:pom:v3.1.42 v3.1.42     [70/2593]",
            "[INFO] --------------------------------[ pom ]---------------------------------",
            "[WARNING] The POM for org.eclipse.m2e:lifecycle-mapping:jar:1.0.0 is missing, no dependency information available",
            "[WARNING] Failed to retrieve plugin descriptor for org.eclipse.m2e:lifecycle-mapping:1.0.0: Plugin org.eclipse.m2e:lifecycle-mapping:1.0.0 or one of its dependencies could not be resolved: Failure to find org.eclipse.m2e:lifecycle-mapping:jar:1.0.0 in http://nexus-dev/nexus/content/groups/mx-artifacts/ was cached in the local repository, resolution will not be reattempted until the update interval of mx-artifacts-plugin has elapsed or updates are forced",
            "[INFO]",
            "7275 [main] [INFO] --- maven-dependency-plugin:2.10:tree (default-cli) @ hub-teamcity-common ---",
            "7450 [main] [INFO] com.blackducksoftware.integration:hub-teamcity-common:jar:3.2.0-SNAPSHOT",
            "7509 [main] [INFO] +- com.blackducksoftware.integration:hub-common:jar:13.1.2:compile",
            "7560 [main] [INFO] |  +- com.blackducksoftware.integration:hub-common-rest:jar:2.1.3:compile",
            "7638 [main] [INFO] |  |  +- com.blackducksoftware.integration:integration-common:jar:6.0.2:compile"
        ));
    }

    @Test
    public void test() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        MavenCodeLocationPackager packager = new MavenCodeLocationPackager(externalIdFactory);

        List<MavenParseResult> results = packager.extractCodeLocations("", getInput(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.MAVEN, results.get(0).getCodeLocation().getDependencyGraph());
        graphAssert.hasRootSize(1);

        ExternalId hubCommon = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "hub-common", "13.1.2");
        ExternalId integrationCommon = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "integration-common", "6.0.2");
        ExternalId hubCommonRest = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "hub-common-rest", "2.1.3");

        graphAssert.hasRootDependency(hubCommon);
        graphAssert.hasParentChildRelationship(hubCommonRest, integrationCommon);
        graphAssert.hasParentChildRelationship(hubCommon, hubCommonRest);
    }
}
