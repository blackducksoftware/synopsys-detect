package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class CpanCliExtractor {
    private final CpanListParser cpanListParser;
    private final DetectableExecutableRunner executableRunner;
    private final ToolVersionLogger toolVersionLogger;

    public CpanCliExtractor(CpanListParser cpanListParser, DetectableExecutableRunner executableRunner, ToolVersionLogger toolVersionLogger) {
        this.cpanListParser = cpanListParser;
        this.executableRunner = executableRunner;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(ExecutableTarget cpanExe, ExecutableTarget cpanmExe, File workingDirectory) throws ExecutableRunnerException {
        toolVersionLogger.log(workingDirectory, cpanExe);
        //TODO: Consider command runner to reduce cognitive load.
        ExecutableOutput cpanListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, cpanExe, "-l"));
        List<String> listText = cpanListOutput.getStandardOutputAsList();

        ExecutableOutput showdepsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, cpanmExe, "--showdeps", "."));
        List<String> showdeps = showdepsOutput.getStandardOutputAsList();

        DependencyGraph dependencyGraph = cpanListParser.parse(listText, showdeps);
        CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);
        return new Extraction.Builder().success(detectCodeLocation).build();
    }
}
