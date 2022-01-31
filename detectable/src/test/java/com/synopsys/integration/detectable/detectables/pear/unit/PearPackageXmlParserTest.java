package com.synopsys.integration.detectable.detectables.pear.unit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageXmlParser;
import com.synopsys.integration.util.NameVersion;

@UnitTest
class PearPackageXmlParserTest {
    private static PearPackageXmlParser pearPackageXmlParser;

    @BeforeEach
    void setUp() {
        pearPackageXmlParser = new PearPackageXmlParser();
    }

    void parse(boolean includeDoctype) throws IOException, SAXException, ParserConfigurationException {
        String samplePackageXml = "<package>\n"
            + " <name>test-name</name>\n"
            + " <version>\n"
            + "  <release>1.1.1</release>\n"
            + "  <api>1.0.0</api>\n"
            + " </version>\n"
            + "</package>";

        if (includeDoctype) {
            samplePackageXml = "<!DOCTYPE package SYSTEM \"http://pear.php.net/dtd/package-1.0\">\n" + samplePackageXml;
        }

        InputStream inputStream = IOUtils.toInputStream(samplePackageXml, StandardCharsets.UTF_8);
        NameVersion nameVersion = pearPackageXmlParser.parse(inputStream);

        Assertions.assertEquals("test-name", nameVersion.getName());
        Assertions.assertEquals("1.1.1", nameVersion.getVersion());
    }

    @Test
    void parseWithoutDoctype() throws ParserConfigurationException, SAXException, IOException {
        parse(false);
    }

    @Test
    void parseWithDoctype() throws ParserConfigurationException, SAXException, IOException {
        parse(true);
    }
}