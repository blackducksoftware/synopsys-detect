package com.synopsys.integration.detectable.detectables.ivy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.SAXParser;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.ivy.parse.IvyDependenciesSaxHandler;
import com.synopsys.integration.detectable.detectables.ivy.parse.IvyProjectNameParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class IvyParseExtractor {
    private final SAXParser saxParser;
    private final IvyProjectNameParser projectNameParser;

    public IvyParseExtractor(SAXParser saxParser, IvyProjectNameParser projectNameParser) {
        this.saxParser = saxParser;
        this.projectNameParser = projectNameParser;
    }

    public Extraction extract(File ivyXmlFile, @Nullable File buildXmlFile) throws IOException {
        try (InputStream ivyXmlInputStream = Files.newInputStream(ivyXmlFile.toPath())) {
            IvyDependenciesSaxHandler ivyDependenciesSaxHandler = new IvyDependenciesSaxHandler(); // TODO: Maybe wrap with Parser like other handler
            saxParser.parse(ivyXmlInputStream, ivyDependenciesSaxHandler);
            List<Dependency> dependencies = ivyDependenciesSaxHandler.getDependencies();

            DependencyGraph dependencyGraph = new BasicDependencyGraph();
            dependencyGraph.addChildrenToRoot(dependencies);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph); // TODO: Could pass in project external id
            // TODO: Create a ProjectDependencyGraph instead :)

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
