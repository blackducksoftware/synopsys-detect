package com.blackducksoftware.integration.hub.detect.util

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test

import static org.junit.Assert.assertEquals

class BdioFileNamerTest {
    @Test
    public void testNormal() {
        // pieces are short enough it should maintain the order without sorting or hashing
        String expected = 'MAVEN_TestGroup_TestName_TestVersion_common_bdio.jsonld'

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory()
        final ExternalId externalId = externalIdFactory.createMavenExternalId("TestGroup", "TestName", "TestVersion")
        final BdioFileNamer bdioFileNamer = new BdioFileNamer()
        bdioFileNamer.integrationEscapeUtil = new IntegrationEscapeUtil()
        final String shortenedName = bdioFileNamer.generateShortenedFilename(BomToolType.MAVEN, "common", externalId)

        assertEquals(expected, shortenedName)
    }

    @Test
    public void testLongGroup() {
        String group = "TestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroupTestGroup"
        String name = "second"
        String version = "shouldbethird"
        String path = "first"

        String hashedGroup = DigestUtils.sha1Hex(group).substring(0, 15)
        // pieces are hashed if they are too big and the pieces are all sorted by length
        String expected = "MAVEN_first_second_shouldbethird_${hashedGroup}_bdio.jsonld"

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        final BdioFileNamer bdioFileNamer = new BdioFileNamer();
        bdioFileNamer.integrationEscapeUtil = new IntegrationEscapeUtil();
        final String shortenedName = bdioFileNamer.generateShortenedFilename(BomToolType.MAVEN, path, externalId);

        assertEquals(expected, shortenedName)
    }

    @Test
    public void testLongName() {
        String group = "shouldbethird"
        String name = "TestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestName"
        String version = "second"
        String path = "first"

        String hashedName = DigestUtils.sha1Hex(name).substring(0, 15)
        // pieces are hashed if they are too big and the pieces are all sorted by length
        String expected = "MAVEN_first_second_shouldbethird_${hashedName}_bdio.jsonld"

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        final BdioFileNamer bdioFileNamer = new BdioFileNamer();
        bdioFileNamer.integrationEscapeUtil = new IntegrationEscapeUtil();
        final String shortenedName = bdioFileNamer.generateShortenedFilename(BomToolType.MAVEN, path, externalId);

        assertEquals(expected, shortenedName)
    }

    @Test
    public void testLongPath() {
        String group = "shouldbethird"
        String name = "first"
        String version = "second"
        String path = "LongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPathLongPath"

        String hashedPath = DigestUtils.sha1Hex(path).substring(0, 15)
        // pieces are hashed if they are too big and the pieces are all sorted by length
        String expected = "MAVEN_first_second_shouldbethird_${hashedPath}_bdio.jsonld"

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        final BdioFileNamer bdioFileNamer = new BdioFileNamer();
        bdioFileNamer.integrationEscapeUtil = new IntegrationEscapeUtil();
        final String shortenedName = bdioFileNamer.generateShortenedFilename(BomToolType.MAVEN, path, externalId);

        assertEquals(expected, shortenedName)
    }

}
