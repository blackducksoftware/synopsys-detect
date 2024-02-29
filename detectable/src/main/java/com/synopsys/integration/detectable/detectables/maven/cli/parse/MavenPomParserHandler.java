package com.synopsys.integration.detectable.detectables.maven.cli.parse;

import java.util.List;
import java.util.ArrayList;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class MavenPomParserHandler extends DefaultHandler {
    private static final String DEPENDENCY = "dependency";
    private static final String PLUGIN = "plugin";
    private static final String INCLUDE_SHADED_DEPENDENCIES = "include";
    private static final String VERSION = "version";
    private static final String GROUP = "groupId";
    private static final String ARTIFACT = "artifactId";
    private static final String MAVEN_SHADE_PLUGIN = "maven-shade-plugin";
    private static boolean parsingMavenShadePlugin = false;
    private static boolean parsingInclude = false;
    private static boolean parsingGroup = false;
    private static boolean parsingPlugin = false;
    private static boolean parsingDependency = false;
    private static boolean parsingArtifact = false;
    private static boolean parsingVersion = false;
    private static boolean isMavenShadePluginUsed = false;
    private final List<String> shadedDependencies = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();
    private final ExternalIdFactory externalIdFactory;
    private String shadedDependencyVersion = null;
    private String group;
    private String artifact;
    private String version;


    public MavenPomParserHandler(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(INCLUDE_SHADED_DEPENDENCIES.equals(qName) && parsingMavenShadePlugin && parsingPlugin){
            parsingInclude = true;
        }else if(PLUGIN.equals(qName)){
            parsingPlugin = true;
        } else if(DEPENDENCY.equals(qName)){
            parsingDependency = true;
        } else if(VERSION.equals(qName)){
            parsingVersion();
        } else if(parsingDependency && GROUP.equals(qName)){
            parsingGroup();
        } else if(parsingDependency && ARTIFACT.equals(qName)){
            parsingArtifact();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (DEPENDENCY.equals(qName)) {
            parsingDependency = false;

            ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
            dependencies.add(new Dependency(artifact, version, externalId));
        } else if (INCLUDE_SHADED_DEPENDENCIES.equals(qName)){
            parsingInclude = false;
        } else if (PLUGIN.equals(qName)){
            parsingPlugin = false;
            parsingMavenShadePlugin = false;
        } else {
            parsingNothingImportant();
        }
    }

    @Override
    public void characters(char[] ch,int start, int length) throws SAXException {
        super.characters(ch,start,length);
        String value = new String(ch,start,length);
        if(value.equals(MAVEN_SHADE_PLUGIN)){
            parsingMavenShadePlugin = true;
            isMavenShadePluginUsed = true;
        } else if(parsingInclude){
            shadedDependencies.add(new String(ch,start,length));
        } else if(parsingVersion && parsingDependency){
            version = new String(ch,start,length);
        } else if(parsingVersion && shadedDependencyVersion == null){
            shadedDependencyVersion = new String(ch,start,length);
        } else if (parsingArtifact) {
            artifact = new String(ch,start,length);
        } else if (parsingGroup) {
            group = new String(ch,start,length);
        }
    }

    private void parsingNothingImportant() {
        parsingGroup = false;
        parsingArtifact = false;
        parsingVersion = false;
    }

    private void parsingGroup() {
        parsingNothingImportant();
        parsingGroup = true;
    }

    private void parsingArtifact() {
        parsingNothingImportant();
        parsingArtifact = true;
    }

    private void parsingVersion() {
        parsingNothingImportant();
        parsingVersion = true;
    }

    public List<String> getShadedDependencies() {
        return shadedDependencies;
    }

    public String getShadedDependencyVersion() {
        return shadedDependencyVersion;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean getMavenShadePluginUse() {
        return isMavenShadePluginUsed;
    }
}
