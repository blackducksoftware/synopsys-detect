package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.github.zafarkhaja.semver.Version;

public class GradleXmlDocumentVersionExtractorTest {
    final TestUtil testUtil = new TestUtil();
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder xmlDocumentBuilder = factory.newDocumentBuilder();

    public GradleXmlDocumentVersionExtractorTest() throws ParserConfigurationException {}

    @Test
    public void detectVersionFromXML() throws IOException, SAXException {
        final GradleXmlDocumentVersionExtractor gradleXmlDocumentVersionExtractor = new GradleXmlDocumentVersionExtractor();
        final Document xmlDocument = getMavenMetadataXML();

        final String latest = "*";
        final String lockedMajor = "0.*";
        final String lockedMinor = "0.2.*";
        final String lockedPatch = "0.5.0";
        final String invalidVersionRange = "2.*";

        final Version wildcardVersion = gradleXmlDocumentVersionExtractor.detectVersionFromXML(xmlDocument, latest).get();
        assert wildcardVersion.toString().equals("1.1.0");

        final Version lockedMajorVersion = gradleXmlDocumentVersionExtractor.detectVersionFromXML(xmlDocument, lockedMajor).get();
        assert lockedMajorVersion.toString().equals("0.7.0");

        final Version lockedMinorVersion = gradleXmlDocumentVersionExtractor.detectVersionFromXML(xmlDocument, lockedMinor).get();
        assert lockedMinorVersion.toString().equals("0.2.2");

        final Version lockedPatchVersion = gradleXmlDocumentVersionExtractor.detectVersionFromXML(xmlDocument, lockedPatch).get();
        assert lockedPatchVersion.toString().equals("0.5.0");

        final Optional<Version> invalidVersionRangeVersion = gradleXmlDocumentVersionExtractor.detectVersionFromXML(xmlDocument, invalidVersionRange);
        assert !invalidVersionRangeVersion.isPresent();
    }

    private Document getMavenMetadataXML() throws IOException, SAXException {
        final InputStream mavenMetadataString = testUtil.getResourceAsInputStream("/gradle/maven-metadata.xml");

        return xmlDocumentBuilder.parse(mavenMetadataString);
    }
}