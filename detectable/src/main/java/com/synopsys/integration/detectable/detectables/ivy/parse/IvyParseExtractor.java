package com.synopsys.integration.detectable.detectables.ivy.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.ivy.IvyProjectNameParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class IvyParseExtractor {
    private final ExternalIdFactory externalIdFactory;
    private final SAXParser saxParser;
    private final IvyProjectNameParser projectNameParser;

    public IvyParseExtractor(ExternalIdFactory externalIdFactory, SAXParser saxParser, IvyProjectNameParser projectNameParser) {
        this.externalIdFactory = externalIdFactory;
        this.saxParser = saxParser;
        this.projectNameParser = projectNameParser;
    }

    public Extraction extract(File ivyXmlFile, File buildXmlFile) throws IOException {
        try (InputStream ivyXmlInputStream = new FileInputStream(ivyXmlFile)) {
            IvyDependenciesHandler ivyDependenciesHandler = new IvyDependenciesHandler(externalIdFactory);
            saxParser.parse(ivyXmlInputStream, ivyDependenciesHandler);
            List<Dependency> dependencies = ivyDependenciesHandler.getDependencies();

            MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
            dependencyGraph.addChildrenToRoot(dependencies);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            Optional<NameVersion> projectName = projectNameParser.parseProjectName(buildXmlFile);

            return new Extraction.Builder()
                .success(codeLocation)
                .nameVersionIfPresent(projectName)
                .build();
        } catch (SAXException e) {
            return new Extraction.Builder().failure(String.format("There was an error parsing file %s: %s", ivyXmlFile.getAbsolutePath(), e.getMessage())).build();
        }
    }
}
