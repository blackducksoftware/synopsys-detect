package com.synopsys.integration.detectable.detectables.xcode.parse;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.synopsys.integration.detectable.detectables.xcode.model.FileReferenceType;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeFileReference;
import com.synopsys.integration.detectable.detectables.xcode.model.XcodeWorkspace;
import com.synopsys.integration.detectable.util.XmlUtil;

public class XcodeWorkspaceParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public XcodeWorkspace parse(String workspaceDataXmlContents) throws IOException, SAXException, ParserConfigurationException, DOMException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource workspaceInputSource = new InputSource(new StringReader(workspaceDataXmlContents.trim()));

        Document workspaceDocument = builder.parse(workspaceInputSource);
        Node workspaceNode = XmlUtil.getNode("Workspace", workspaceDocument);
        Optional<String> formatVersion = XmlUtil.getAttributeSafetly("version", workspaceNode, logger);
        List<Node> fileRefs = XmlUtil.getNodeList("FileRef", workspaceNode);

        List<XcodeFileReference> xcodeFileReferences = fileRefs.stream()
            .map(fileRefsNode -> XmlUtil.getAttributeSafetly("location", fileRefsNode, logger))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::parseReference)
            .collect(Collectors.toList());

        return new XcodeWorkspace(formatVersion.orElse("UNKNOWN"), xcodeFileReferences);
    }

    private XcodeFileReference parseReference(String locationString) {
        String location = StringUtils.removeStart(locationString, "group:");
        FileReferenceType fileReferenceType = FileReferenceType.DIRECTORY;
        if (StringUtils.endsWith(location, ".xcodeproj")) {
            fileReferenceType = FileReferenceType.XCODE_PROJECT;
        }
        return new XcodeFileReference(Paths.get(location), fileReferenceType);
    }
}
