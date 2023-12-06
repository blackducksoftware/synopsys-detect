package com.synopsys.integration.detectable.detectables.cpan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> listText = generateCpanListOutput(workingDirectory, cpanExe);

        ExecutableOutput showdepsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, cpanmExe, "--showdeps", "."));
        List<String> showdeps = showdepsOutput.getStandardOutputAsList();

        DependencyGraph dependencyGraph = cpanListParser.parse(listText, showdeps);
        CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph);
        return new Extraction.Builder().success(detectCodeLocation).build();
    }

    List<String> generateCpanListOutput(File workingDirectory, ExecutableTarget cpanExe) throws ExecutableRunnerException {
        Map<String, String> environmentVariables = new HashMap<>();

        // When cpan is run on a system for the first time, the user is presented with a number of prompts; this can cause Detect to wait indefinitely.
        // The following line causes the execution to accept defaults and does not block on any prompts so that Detect can complete its execution:
        environmentVariables.put("PERL_MM_USE_DEFAULT", "true");

        toolVersionLogger.log(workingDirectory, cpanExe, "-v", environmentVariables);

        List<String> args = new ArrayList<String>();
        args.add("-l");

        ExecutableOutput cpanListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, environmentVariables, cpanExe, args));
        return cpanListOutput.getStandardOutputAsList();
    }
}
