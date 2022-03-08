package com.synopsys.integration.detectable.detectables.conda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;

public class CondaCliExtractor {
    private final CondaListParser condaListParser;
    private final DetectableExecutableRunner executableRunner;
    private final ToolVersionLogger toolVersionLogger;

    public CondaCliExtractor(CondaListParser condaListParser, DetectableExecutableRunner executableRunner, ToolVersionLogger toolVersionLogger) {
        this.condaListParser = condaListParser;
        this.executableRunner = executableRunner;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(File directory, ExecutableTarget condaExe, File workingDirectory, String condaEnvironmentName) {
        try {
            toolVersionLogger.log(workingDirectory, condaExe);
            List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            if (StringUtils.isNotBlank(condaEnvironmentName)) {
                condaListOptions.add("-n");
                condaListOptions.add(condaEnvironmentName);
            }
            condaListOptions.add("--json");
            ExecutableOutput condaListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(directory, condaExe, condaListOptions));

            String listJsonText = condaListOutput.getStandardOutput();

            ExecutableOutput condaInfoOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, condaExe, "info", "--json"));
            String infoJsonText = condaInfoOutput.getStandardOutput();

            DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
