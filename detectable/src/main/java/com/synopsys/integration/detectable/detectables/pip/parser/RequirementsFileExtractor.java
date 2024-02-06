package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
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

    public Extraction extract(File requirementsFileObject) throws IOException {
        List<RequirementsFileDependency> dependencies = requirementsFileTransformer.transform(requirementsFileObject);
        DependencyGraph dependencyGraph = requirementsFileDependencyTransformer.transform(dependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return Extraction.success(codeLocation);
    }
}
