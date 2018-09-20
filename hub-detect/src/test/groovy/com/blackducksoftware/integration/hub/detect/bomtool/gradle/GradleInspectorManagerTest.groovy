package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import com.blackducksoftware.integration.hub.detect.BeanConfiguration
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.blackducksoftware.integration.hub.detect.version.DetectVersion
import com.blackducksoftware.integration.hub.detect.version.DetectVersionRange
import org.junit.Test
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder

class GradleInspectorManagerTest {
    final TestUtil testUtil = new TestUtil();
    final BeanConfiguration beanConfiguration = new BeanConfiguration(null)
    final DocumentBuilder xmlDocumentBuilder = beanConfiguration.xmlDocumentBuilder()

    @Test
    void detectVersionFromXML() {
        final GradleInspectorManager gradleInspectorManager = new GradleInspectorManager(null, null, xmlDocumentBuilder, null, null)
        final Document xmlDocument = getMavenMetadataXML()

        final DetectVersionRange latest = DetectVersionRange.fromString("*")
        final DetectVersionRange lockedMajor = DetectVersionRange.fromString("0.*")
        final DetectVersionRange lockedMinor = DetectVersionRange.fromString("0.2.*")
        final DetectVersionRange lockedPatch = DetectVersionRange.fromString("0.5.0")
        final DetectVersionRange invalidVersionRange = DetectVersionRange.fromString("2.*")

        final DetectVersion wildcardVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, latest).get()
        assert wildcardVersion.toVersionString() == "1.1.0"

        final DetectVersion lockedMajorVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedMajor).get()
        assert lockedMajorVersion.toVersionString() == "0.7.0"

        final DetectVersion lockedMinorVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedMinor).get()
        assert lockedMinorVersion.toVersionString() == "0.2.2"

        final DetectVersion lockedPatchVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedPatch).get()
        assert lockedPatchVersion.toVersionString() == "0.5.0"

        final Optional<DetectVersion> invalidVersionRangeVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, invalidVersionRange)
        assert !invalidVersionRangeVersion.isPresent()
    }

    Document getMavenMetadataXML() {
        final InputStream mavenMetadataString = testUtil.getResourceAsInputStream("/gradle/maven-metadata.xml")

        return xmlDocumentBuilder.parse(mavenMetadataString)
    }
}