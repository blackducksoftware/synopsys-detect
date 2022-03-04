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

public class MavenCorruptOutputTest {

    private List<String> getInput() {
        return Arrays.asList(
            "[\u001B[1;34mINFO\u001B[m] \u001B[1m--- \u001B[0;32mmaven-dependency-plugin:2.8:tree\u001B[m \u001B[1m(default-cli)\u001B[m @ \u001B[36msonar-stash-plugin\u001B[0;1m ---\u001B[m",
            "[INFO] Downloading from mx-artifacts-plugin: http://nexus-dev/nexus/content/groups/mx-artifacts/org/eclipse/m2e/lifecycle-mapping/1.0.0/lifecycle-mapping-1.0.0.pom",
            "[\u001B[1;34mINFO\u001B[m] com.blackducksoftware.integration:hub-teamcity-common:jar:3.2.0-SNAPSHOT",
            "7509 [main] [INFO] +- com.blackducksoftware.integration:hub-common:jar:13.1.2:compile",
            "7560 [main] [INFO] |  +- com.blackducksoftware.integration:hub-common-rest:jar:2.1.3:compile",
            "7638 [main] [INFO] |  |  +- com.blackducksoftware.integration:integration-common:jar:6.0.2:compile"
        );
    }

    @Test
    public void test() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        MavenCodeLocationPackager packager = new MavenCodeLocationPackager(externalIdFactory);

        List<MavenParseResult> results = packager.extractCodeLocations(
            "",
            getInput(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );

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
