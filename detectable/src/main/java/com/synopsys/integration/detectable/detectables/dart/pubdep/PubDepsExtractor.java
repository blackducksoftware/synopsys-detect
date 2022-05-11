package com.synopsys.integration.detectable.detectables.dart.pubdep;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.util.NameVersion;

public class PubDepsExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    private final PubDepsParser pubDepsParser;
    private final PubSpecYamlNameVersionParser nameVersionParser;
    private final ToolVersionLogger toolVersionLogger;

    public PubDepsExtractor(
        DetectableExecutableRunner executableRunner,
        PubDepsParser pubDepsParser,
        PubSpecYamlNameVersionParser nameVersionParser,
        ToolVersionLogger toolVersionLogger
    ) {
        this.executableRunner = executableRunner;
        this.pubDepsParser = pubDepsParser;
        this.nameVersionParser = nameVersionParser;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(
        File directory,
        @Nullable ExecutableTarget dartExe,
        @Nullable ExecutableTarget flutterExe,
        DartPubDepsDetectableOptions dartPubDepsDetectableOptions,
        File pubSpecYamlFile
    ) {
        try {
            toolVersionLogger.log(directory, dartExe);
            toolVersionLogger.log(directory, flutterExe);
            List<String> pubDepsCommand = new ArrayList<>();
            pubDepsCommand.add("pub");
            pubDepsCommand.add("deps");

            if (dartPubDepsDetectableOptions.getDependencyTypeFilter().shouldExclude(DartPubDependencyType.DEV)) {
                pubDepsCommand.add("--no-dev");
            }

            ExecutableOutput pubDepsOutput = null;

            if (dartExe != null) {
                pubDepsOutput = runPubDepsCommand(directory, dartExe, pubDepsCommand);
            }

            if (pubDepsOutput == null || pubDepsOutput.getReturnCode() != 0) {
                if (flutterExe == null && dartExe != null) {
                    return new Extraction.Builder().failure(String.format("An error occurred trying to run %s %s", dartExe.toCommand(), String.join(" ", pubDepsCommand))).build();
                } else {
                    // If command does not work with Dart, it could be because at least one of the packages requires Flutter
                    logger.debug("Running dart pub deps was not successful.  Going to try running flutter pub deps.");
                    pubDepsCommand.add(0, "--no-version-check");
                    pubDepsOutput = runPubDepsCommand(directory, flutterExe, pubDepsCommand);
                }
            }

            Optional<NameVersion> nameVersion = Optional.empty();
            if (pubSpecYamlFile != null) {
                List<String> pubSpecYamlLines = Files.readAllLines(pubSpecYamlFile.toPath(), StandardCharsets.UTF_8);
                nameVersion = nameVersionParser.parseNameVersion(pubSpecYamlLines);
            }

            DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsOutput.getStandardOutputAsList());

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(codeLocation).nameVersionIfPresent(nameVersion).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private ExecutableOutput runPubDepsCommand(File directory, ExecutableTarget exe, List<String> commandArgs) throws ExecutableRunnerException {
        return executableRunner.execute(ExecutableUtils.createFromTarget(directory, exe, commandArgs));
    }
}
