package com.synopsys.integration.detectable.detectables.ivy.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import javax.xml.parsers.SAXParser;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.synopsys.integration.util.NameVersion;

public class IvyProjectNameParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SAXParser saxParser;

    public IvyProjectNameParser(SAXParser saxParser) {
        this.saxParser = saxParser;
    }

    public Optional<NameVersion> parseProjectName(@Nullable File buildXmlFile) throws IOException {
        if (buildXmlFile == null) {
            return Optional.empty();
        }
        try (InputStream ivyXmlInputStream = Files.newInputStream(buildXmlFile.toPath())) {
            IvyProjectNameSaxHandler handler = new IvyProjectNameSaxHandler();
            saxParser.parse(ivyXmlInputStream, handler);
            return Optional.of(new NameVersion(handler.getProjectName()));
        } catch (SAXException e) {
            logger.debug("Failed to parse build.xml for project info.", e);
            return Optional.empty();
        }
    }
}