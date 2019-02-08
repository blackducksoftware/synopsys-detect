package com.synopsys.integration.detectable.detectables.pear.unit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.PearPackageXmlParser;
import com.synopsys.integration.util.NameVersion;

@UnitTest
class PearPackageXmlParserTest {
    private static PearPackageXmlParser pearPackageXmlParser;

    @BeforeEach
    void setUp() {
        pearPackageXmlParser = new PearPackageXmlParser();
    }

    @Test
    void parse() throws IOException, SAXException, ParserConfigurationException {
        final String samplePackageXml = "<package>\n"
                                            + " <name>test-name</name>\n"
                                            + " <version>\n"
                                            + "  <release>1.1.1</release>\n"
                                            + "  <api>1.0.0</api>\n"
                                            + " </version>\n"
                                            + "</package>";

        final InputStream inputStream = IOUtils.toInputStream(samplePackageXml, StandardCharsets.UTF_8);
        final NameVersion nameVersion = pearPackageXmlParser.parse(inputStream);

        Assert.assertEquals("test-name", nameVersion.getName());
        Assert.assertEquals("1.1.1", nameVersion.getVersion());
    }
}