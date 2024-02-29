package com.synopsys.integration.detectable.factory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MavenPomParserFactory {

    private final ExternalIdFactory externalIdFactory;

    private MavenPomParser mavenPomParser;

    public MavenPomParserFactory(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }
    public MavenPomParser getMavenPomParser(){
        if(mavenPomParser == null) {
            mavenPomParser = new MavenPomParser(externalIdFactory, saxParser());
        }
        return mavenPomParser;
    }

    private SAXParser saxParser() {
        try {
            return SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Unable to create SAX Parser.", e);
        }
    }
}
