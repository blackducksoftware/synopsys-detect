package com.blackduck.integration.detectable.detectables.xcode.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import com.blackduck.integration.detectable.detectables.xcode.model.FileReferenceType;
import com.blackduck.integration.detectable.detectables.xcode.model.XcodeFileReference;
import com.blackduck.integration.detectable.detectables.xcode.model.XcodeWorkspace;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class XcodeWorkspaceParserTest {

    @Test
    void parse() throws IOException, ParserConfigurationException, SAXException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        String workspaceContents = StringUtils.joinWith(
            System.lineSeparator(),
            " <?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<Workspace",
            "   version = \"1.0\">",
            "   <FileRef",
            "      location = \"group:ENA/ENASecurity\">",
            "   </FileRef>",
            "   <FileRef",
            "      location = \"group:ENA/HealthCertificateToolkit\">",
            "   </FileRef>",
            "   <FileRef",
            "      location = \"group:ENA/ENA.xcodeproj\">",
            "   </FileRef>",
            "</Workspace>"
        );

        XcodeWorkspaceParser parser = new XcodeWorkspaceParser();
        XcodeWorkspace xcodeWorkspace = parser.parse(workspaceContents);

        XcodeFileReference enaSecurity = assertReferenceExists(xcodeWorkspace, "ENA/ENASecurity");
        Assertions.assertEquals(FileReferenceType.DIRECTORY, enaSecurity.getFileReferenceType());

        XcodeFileReference healthToolkit = assertReferenceExists(xcodeWorkspace, "ENA/HealthCertificateToolkit");
        assertEquals(FileReferenceType.DIRECTORY, healthToolkit.getFileReferenceType());

        XcodeFileReference xcodeProject = assertReferenceExists(xcodeWorkspace, "ENA/ENA.xcodeproj");
        assertEquals(FileReferenceType.XCODE_PROJECT, xcodeProject.getFileReferenceType());

        assertEquals(3, xcodeWorkspace.getFileReferences().size(), "There are more file references than are being asserted against");
    }

    private XcodeFileReference assertReferenceExists(XcodeWorkspace xcodeWorkspace, String relativePath) {
        Optional<XcodeFileReference> enaSecurity = xcodeWorkspace.getFileReferences().stream()
            .filter(reference -> reference.getRelativeLocation().toString().contains(relativePath))
            .findFirst();

        assertTrue(enaSecurity.isPresent(), String.format("Expected to find '%s' in the Xcode Workspace", relativePath));
        assertEquals(Paths.get(relativePath), enaSecurity.get().getRelativeLocation(), String.format("Should have parsed to the same path '%s'", relativePath));
        return enaSecurity.get();
    }
}