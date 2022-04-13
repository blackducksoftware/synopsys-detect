package com.synopsys.integration.detectable.detectables.gradle.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportLineParser;

public class GradleReportLineTest {
    @Test
    public void testMiscWinningIndicators() {
        String line = "|    |    |    |    +--- commons-codec:commons-codec:1.7 -> 1.9";
        String[] expected = new String[] {
            "commons-codec",
            "commons-codec",
            "1.9",
            "commons-codec:commons-codec:1.9"
        };
        assertDependency(line, expected);

        line = "|    |    |    |    +--- joda-time:joda-time:2.2 -> 2.7";
        expected = new String[] {
            "joda-time",
            "joda-time",
            "2.7",
            "joda-time:joda-time:2.7"
        };
        assertDependency(line, expected);
    }

    @Test
    public void testParsingWinningIndicatorIn4_6() {
        final String line = "+--- org.springframework.boot:spring-boot-starter -> 2.0.0.RELEASE";
        String[] expected = new String[] {
            "org.springframework.boot",
            "spring-boot-starter",
            "2.0.0.RELEASE",
            "org.springframework.boot:spring-boot-starter:2.0.0.RELEASE"
        };
        assertDependency(line, expected);
    }

    @Test
    public void testParsingWinningIndicator() {
        final String line = "+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE";
        String[] expected = new String[] {
            "org.springframework.boot",
            "spring-boot-starter",
            "1.4.3.RELEASE",
            "org.springframework.boot:spring-boot-starter:1.4.3.RELEASE"
        };
        assertDependency(line, expected);
    }

    @Test
    public void testParsingWinningIndcatorWithFullGav() {
        final String line = "|    |    |    |    +--- org.bouncycastle:bcprov-jdk15:1.46 -> org.bouncycastle:bcprov-jdk15on:1.47";
        String[] expected = new String[] {
            "org.bouncycastle",
            "bcprov-jdk15on",
            "1.47",
            "org.bouncycastle:bcprov-jdk15on:1.47"
        };
        assertDependency(line, expected);
    }

    @Test
    public void testParsingWinningIndicatorLine() {
        final String line = "|    \\--- com.squareup.okhttp3:okhttp-urlconnection:3.4.2 (*)";
        String[] expected = new String[] {
            "com.squareup.okhttp3",
            "okhttp-urlconnection",
            "3.4.2",
            "com.squareup.okhttp3:okhttp-urlconnection:3.4.2"
        };
        assertDependency(line, expected);
    }

    @Test
    public void testParsingStandardLine() {
        final String line = "|    +--- com.blackducksoftware.integration:integration-bdio:12.1.0";
        String[] expected = new String[] {
            "com.blackducksoftware.integration",
            "integration-bdio",
            "12.1.0",
            "com.blackducksoftware.integration:integration-bdio:12.1.0"
        };
        assertDependency(line, expected);
    }

    private void assertDependency(String line, String[] expectedResults) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        GradleReportLineParser gradleReportLineParser = new GradleReportLineParser();
        GradleTreeNode gradleTreeNode = gradleReportLineParser.parseLine(line);
        GradleGav gav = gradleTreeNode.getGav().get();
        ExternalId externalId = externalIdFactory.createMavenExternalId(gav.getGroup(), gav.getName(), gav.getVersion());
        Dependency dependency = new Dependency(gav.getName(), gav.getVersion(), externalId);

        Assertions.assertEquals(expectedResults[0], dependency.getExternalId().getGroup());
        Assertions.assertEquals(expectedResults[1], dependency.getName());
        Assertions.assertEquals(expectedResults[2], dependency.getVersion());
        Assertions.assertEquals(expectedResults[3], dependency.getExternalId().createExternalId());
    }
}
