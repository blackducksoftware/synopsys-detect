package com.synopsys.integration.detectable.detectables.ivy.task;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
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

    public Extraction extract(File directory, ExecutableTarget antExe, File buildXmlFile, IvyTaskDetectableOptions ivyTaskDetectableOptions) {
        try {
            ExecutableOutput dependencytreeResult = executableRunner.execute(ExecutableUtils.createFromTarget(directory, antExe, ivyTaskDetectableOptions.getIvyDependencytreeTarget())); //TODO- should this be execute successfully?

            List<String> dependencytreeOutput = dependencytreeResult.getStandardOutputAsList();

            DependencyGraph dependencyGraph = dependencyTreeParser.parse(dependencytreeOutput);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            Optional<NameVersion> projectName = projectNameParser.parseProjectName(buildXmlFile);

            return new Extraction.Builder()
                .success(codeLocation)
                .nameVersionIfPresent(projectName)
                .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
