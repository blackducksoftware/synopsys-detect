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
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;

public class MavenMetadataServiceTest {
    final TestUtil testUtil = new TestUtil();
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder xmlDocumentBuilder = factory.newDocumentBuilder();

    public MavenMetadataServiceTest() throws ParserConfigurationException {}

    @Test
    public void parseVersionFromXML() throws IOException, SAXException {
        final MavenMetadataService mavenMetadataService = new MavenMetadataService(xmlDocumentBuilder, null);
        final Document xmlDocument = getMavenMetadataXML();

        final String latest = "*";
        final String lockedMajor = "0.*";
        final String lockedMinor = "0.2.*";
        final String lockedPatch = "0.5.0";
        final String invalidVersionRange = "2.*";

        final String wildcardVersion = mavenMetadataService.parseVersionFromXML(xmlDocument, latest).get();
        assert wildcardVersion.equals("1.1.0");

        final String lockedMajorVersion = mavenMetadataService.parseVersionFromXML(xmlDocument, lockedMajor).get();
        assert lockedMajorVersion.equals("0.7.0");

        final String lockedMinorVersion = mavenMetadataService.parseVersionFromXML(xmlDocument, lockedMinor).get();
        assert lockedMinorVersion.equals("0.2.2");

        final String lockedPatchVersion = mavenMetadataService.parseVersionFromXML(xmlDocument, lockedPatch).get();
        assert lockedPatchVersion.equals("0.5.0");

        final Optional<String> invalidVersionRangeVersion = mavenMetadataService.parseVersionFromXML(xmlDocument, invalidVersionRange);
        assert !invalidVersionRangeVersion.isPresent();
    }

    private Document getMavenMetadataXML() throws IOException, SAXException {
        final InputStream mavenMetadataString = testUtil.getResourceAsInputStream("/gradle/maven-metadata.xml");

        return xmlDocumentBuilder.parse(mavenMetadataString);
    }
}