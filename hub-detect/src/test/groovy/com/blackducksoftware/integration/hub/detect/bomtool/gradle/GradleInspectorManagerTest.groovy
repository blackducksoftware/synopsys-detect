package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import com.blackducksoftware.integration.hub.detect.BeanConfiguration
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.github.zafarkhaja.semver.Version
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

        final String latest = "*"
        final String lockedMajor = "0.*"
        final String lockedMinor = "0.2.*"
        final String lockedPatch = "0.5.0"
        final String invalidVersionRange = "2.*"

        final Version wildcardVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, latest).get()
        assert wildcardVersion.toString() == "1.1.0"

        final Version lockedMajorVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedMajor).get()
        assert lockedMajorVersion.toString() == "0.7.0"

        final Version lockedMinorVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedMinor).get()
        assert lockedMinorVersion.toString() == "0.2.2"

        final Version lockedPatchVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, lockedPatch).get()
        assert lockedPatchVersion.toString() == "0.5.0"

        final Optional<Version> invalidVersionRangeVersion = gradleInspectorManager.detectVersionFromXML(xmlDocument, invalidVersionRange)
        assert !invalidVersionRangeVersion.isPresent()
    }

    Document getMavenMetadataXML() {
        final InputStream mavenMetadataString = testUtil.getResourceAsInputStream("/gradle/maven-metadata.xml")

        return xmlDocumentBuilder.parse(mavenMetadataString)
    }
}