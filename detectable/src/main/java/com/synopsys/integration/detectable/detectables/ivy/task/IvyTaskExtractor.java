package com.synopsys.integration.detectable.detectables.ivy.task;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.ivy.IvyProjectNameParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class IvyTaskExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final IvyDependencyTreeParser dependencyTreeParser;
    private final IvyProjectNameParser projectNameParser;

    public IvyTaskExtractor(DetectableExecutableRunner executableRunner, IvyDependencyTreeParser dependencyTreeParser, IvyProjectNameParser projectNameParser) {
        this.executableRunner = executableRunner;
        this.dependencyTreeParser = dependencyTreeParser;
        this.projectNameParser = projectNameParser;
    }

    public Extraction extract(File directory, ExecutableTarget antExe, File buildXmlFile) throws ExecutableFailedException {
        try {
            List<String> antDependencyTreeCommand = Arrays.asList(
                "ant",
                "dependencytree"
            );
            ExecutableOutput dependencytreeResult = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, antExe, antDependencyTreeCommand));

            List<String> dependencytreeOutput = dependencytreeResult.getStandardOutputAsList();

            DependencyGraph dependencyGraph = dependencyTreeParser.parse(dependencytreeOutput);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            Optional<NameVersion> prrojectName = projectNameParser.parseProjectName(buildXmlFile);

            return new Extraction.Builder()
                .success(codeLocation)
                .nameVersionIfPresent(prrojectName)
                .build();
        } catch (
            Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
