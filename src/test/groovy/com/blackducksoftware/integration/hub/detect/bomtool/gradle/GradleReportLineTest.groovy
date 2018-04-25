package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import org.junit.Assert
import org.junit.Test

class GradleReportLineTest {
    @Test
    void testMiscWinningIndicators() {
        def line = '|    |    |    |    +--- commons-codec:commons-codec:1.7 -> 1.9'
        def expected = [
            'commons-codec',
            '1.9',
            'commons-codec:commons-codec:1.9'
        ]
        assertDependency(line, expected)

        line = '|    |    |    |    +--- joda-time:joda-time:2.2 -> 2.7'
        expected = [
            'joda-time',
            '2.7',
            'joda-time:joda-time:2.7'
        ]
        assertDependency(line, expected)
    }

    @Test
    void testParsingWinningIndicatorIn4_6() {
        def line = '+--- org.springframework.boot:spring-boot-starter -> 2.0.0.RELEASE'
        def expected = [
            'spring-boot-starter',
            '2.0.0.RELEASE',
            'org.springframework.boot:spring-boot-starter:2.0.0.RELEASE'
        ]
        assertDependency(line, expected)
    }

    @Test
    void testParsingWinningIndicator() {
        def line = '+--- org.springframework.boot:spring-boot-starter: -> 1.4.3.RELEASE'
        def expected = [
            'spring-boot-starter',
            '1.4.3.RELEASE',
            'org.springframework.boot:spring-boot-starter:1.4.3.RELEASE'
        ]
        assertDependency(line, expected)
    }

    @Test
    void testParsingWinningIndcatorWithFullGav() {
        def line = '|    |    |    |    +--- org.bouncycastle:bcprov-jdk15:1.46 -> org.bouncycastle:bcprov-jdk15on:1.47'
        def expected = [
            'bcprov-jdk15on',
            '1.47',
            'org.bouncycastle:bcprov-jdk15on:1.47'
        ]
        assertDependency(line, expected)
    }

    @Test
    void testParsingWinningIndicatorLine() {
        def line = '|    \\--- com.squareup.okhttp3:okhttp-urlconnection:3.4.2 (*)'
        def expected = [
            'okhttp-urlconnection',
            '3.4.2',
            'com.squareup.okhttp3:okhttp-urlconnection:3.4.2'
        ]
        assertDependency(line, expected)
    }

    @Test
    void testParsingStandardLine() {
        def line = '|    +--- com.blackducksoftware.integration:integration-bdio:12.1.0'
        def expected = [
            'integration-bdio',
            '12.1.0',
            'com.blackducksoftware.integration:integration-bdio:12.1.0'
        ]
        assertDependency(line, expected)
    }

    private void assertDependency(String line, def expectedResults) {
        def externalIdFactory = new ExternalIdFactory()
        def gradleReportLine = new GradleReportLine(line)
        def dependency = gradleReportLine.createDependencyNode(externalIdFactory)

        Assert.assertEquals(expectedResults[0], dependency.name)
        Assert.assertEquals(expectedResults[1], dependency.version)
        Assert.assertEquals(expectedResults[2], dependency.externalId.createExternalId())
    }
}