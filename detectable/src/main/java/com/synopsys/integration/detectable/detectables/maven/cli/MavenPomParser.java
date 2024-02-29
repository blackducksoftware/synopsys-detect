package com.synopsys.integration.detectable.detectables.maven.cli;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.cli.parse.MavenPomParserHandler;
import com.synopsys.integration.detectable.factory.MavenPomParserHandlerFactory;
import org.xml.sax.SAXException;

public class MavenPomParser {

    private final SAXParser saxParser;
    private List<String> shadedDependencies = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    private boolean isMavenShadePluginUsed = false;
    private final MavenPomParserHandlerFactory mavenPomParserHandlerFactory;


    public MavenPomParser(ExternalIdFactory externalIdFactory, SAXParser saxParser){
        this.saxParser = saxParser;
        this.mavenPomParserHandlerFactory = new MavenPomParserHandlerFactory(externalIdFactory);
    }

    public void parsePOMFile(File pomXmlFile) throws IOException, SAXException {
        try (final InputStream pomXmlInputStream = Files.newInputStream(pomXmlFile.toPath())) {
            shadedDependencies.clear();
            dependencies.clear();
            isMavenShadePluginUsed = false;
            MavenPomParserHandler mavenPomParserHandler = mavenPomParserHandlerFactory.getMavenPomParserHandler();
            saxParser.parse(pomXmlInputStream, mavenPomParserHandler);
            shadedDependencies = mavenPomParserHandler.getShadedDependencies();
            dependencies = mavenPomParserHandler.getDependencies();
            isMavenShadePluginUsed = mavenPomParserHandler.getMavenShadePluginUse();
        }
    }

    public List<String> getShadedDependencies() {
        return shadedDependencies;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean getMavenShadePluginUseInformation() {
        return isMavenShadePluginUsed;
    }
}
