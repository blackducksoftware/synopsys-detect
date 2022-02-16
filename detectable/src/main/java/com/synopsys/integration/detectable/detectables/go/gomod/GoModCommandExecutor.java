package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;

public class GoModCommandExecutor {
    // java:S5852: Warning about potential DoS risk.
    @SuppressWarnings({ "java:S5852" })
    private static final Pattern GENERATE_GO_LIST_JSON_OUTPUT_PATTERN = Pattern.compile("\\d+\\.[\\d.]+"); // TODO: Provide example. This looks like it's used for version matching contrary to the name. JM-01/2022
    private static final String JSON_OUTPUT_FLAG = "-json";
    private static final String MODULE_OUTPUT_FLAG = "-m";

    private final DetectableExecutableRunner executableRunner;

    public GoModCommandExecutor(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    // Excludes the "all" argument to return the root project modules
    List<String> generateGoListOutput(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, "list", MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG))
            .getStandardOutputAsList();
    }

    // TODO: Utilize the fields "Main": true, and "Indirect": true, fields from the JSON output to avoid running go list twice. Before switching to json output we needed to run twice. JM-01/2022
    List<String> generateGoListJsonOutput(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        // TODO: Move the Go version checking to it's own method. JM-01/2022
        List<String> goVersionOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, "version"))
            .getStandardOutputAsList();
        Matcher matcher = GENERATE_GO_LIST_JSON_OUTPUT_PATTERN.matcher(goVersionOutput.get(0));
        if (matcher.find()) {
            String version = matcher.group();
            String[] parts = version.split("\\.");
            if (Integer.parseInt(parts[0]) > 1 || Integer.parseInt(parts[1]) >= 14) {
                return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, "list", "-mod=readonly", MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG, "all"))
                    .getStandardOutputAsList();
            } else {
                return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, "list", MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG, "all"))
                    .getStandardOutputAsList();
            }
        } // TODO: If we don't have a version for go, we don't do anything. This should probably result in a failure. JM-01/2022
        return new ArrayList<>();
    }

    List<String> generateGoModGraphOutput(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, "mod", "graph"))
            .getStandardOutputAsList();
    }

    List<String> generateGoModWhyOutput(File directory, ExecutableTarget goExe, boolean vendorResults) throws ExecutableFailedException {
        // executing this command helps produce more accurate results. Parse the output to create a module exclusion list.
        List<String> commands = Arrays.asList("mod", "why", "-m");
        if (vendorResults) {
            commands.add("-vendor");
        }
        commands.add("all");
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, commands))
            .getStandardOutputAsList();
    }

}
