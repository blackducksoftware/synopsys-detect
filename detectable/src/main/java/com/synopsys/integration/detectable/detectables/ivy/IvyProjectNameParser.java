package com.synopsys.integration.detectable.detectables.ivy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import javax.xml.parsers.SAXParser;

import com.synopsys.integration.util.NameVersion;

public class IvyProjectNameParser {
    private final SAXParser saxParser;

    public IvyProjectNameParser(SAXParser saxParser) {
        this.saxParser = saxParser;
    }

    public Optional<NameVersion> parseProjectName(File buildXmlFile) {
        try (InputStream ivyXmlInputStream = new FileInputStream(buildXmlFile)) {
            IvyProjectNameHandler handler = new IvyProjectNameHandler();
            saxParser.parse(ivyXmlInputStream, handler);
            return Optional.of(new NameVersion(handler.getProjectName()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}