package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class RequirementsFileExtractor {
    private final RequirementsFileTransformer requirementsFileTransformer;
    private final RequirementsFileDependencyTransformer requirementsFileDependencyTransformer;

    public RequirementsFileExtractor(
        RequirementsFileTransformer requirementsFileTransformer,
        RequirementsFileDependencyTransformer requirementsFileDependencyTransformer
    ) {
        this.requirementsFileTransformer = requirementsFileTransformer;
        this.requirementsFileDependencyTransformer = requirementsFileDependencyTransformer;
    }

    public Extraction extract(List<File> requirementsFiles) throws IOException {
        List<CodeLocation> codeLocations = new ArrayList<>();
        for (File requirementsFile : requirementsFiles) {
            List<RequirementsFileDependency> dependencies = requirementsFileTransformer.transform(requirementsFile);
            DependencyGraph dependencyGraph = requirementsFileDependencyTransformer.transform(dependencies);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            codeLocations.add(codeLocation);
        }
        return Extraction.success(codeLocations);
    }
}
