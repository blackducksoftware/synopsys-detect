package com.synopsys.integration.detectable.detectables.ivy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class IvyParseExtractor {
    private final ExternalIdFactory externalIdFactory;
    private final SAXParser saxParser;

    public IvyParseExtractor(ExternalIdFactory externalIdFactory, SAXParser saxParser) {
        this.externalIdFactory = externalIdFactory;
        this.saxParser = saxParser;
    }

    public Extraction extract(File ivyXmlFile) {
        try (InputStream ivyXmlInputStream = new FileInputStream(ivyXmlFile)) {
            IvyDependenciesHandler ivyDependenciesHandler = new IvyDependenciesHandler(externalIdFactory);
            saxParser.parse(ivyXmlInputStream, ivyDependenciesHandler);
            List<Dependency> dependencies = ivyDependenciesHandler.getDependencies();

            MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
            dependencyGraph.addChildrenToRoot(dependencies);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
