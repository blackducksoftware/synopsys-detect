package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;

public class GoModCommandRunner {
    // java:S5852: Warning about potential DoS risk.
    @SuppressWarnings({ "java:S5852" })
    private static final Pattern GENERATE_GO_LIST_JSON_OUTPUT_PATTERN = Pattern.compile("\\d+\\.[\\d.]+"); // Example: "go version go1.17.5 darwin/amd64" -> ""
    private static final String VERSION_COMMAND = "version";
    private static final String MOD_COMMAND = "mod";
    private static final String MOD_WHY_SUBCOMMAND = "why";
    private static final String MOD_GRAPH_SUBCOMMAND = "graph";
    private static final String LIST_COMMAND = "list";
    private static final String JSON_OUTPUT_FLAG = "-json";
    private static final String MODULE_OUTPUT_FLAG = "-m";
    private static final String VENDOR_OUTPUT_FLAG = "-vendor";
    private static final String LIST_READONLY_FLAG = "-mod=readonly";
    private static final String MODULE_NAME = "all";

    private final DetectableExecutableRunner executableRunner;

    public GoModCommandRunner(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    // Excludes the "all" argument to return the root project modules
    public List<String> runGoList(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, LIST_COMMAND, MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG))
            .getStandardOutputAsList();
    }

    // TODO: Utilize the fields "Main": true, and "Indirect": true, fields from the JSON output to avoid running go list twice. Before switching to json output we needed to run twice. JM-01/2022
    public List<String> runGoListAll(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        boolean readOnlyFlagSupported;
        try {
            readOnlyFlagSupported = isReadOnlyFlagSupported(directory, goExe);
        } catch (ExecutableFailedException e) {
            // TODO: If we don't have a version for go, we don't do anything. This should probably result in a failure. JM-01/2022
            return new ArrayList<>();
        }

        List<String> goListCommand = new LinkedList<>();
        goListCommand.add(LIST_COMMAND);
        if (readOnlyFlagSupported) {
            // Providing a readonly flag prevents the command from modifying customer's source.
            goListCommand.add(LIST_READONLY_FLAG);
        }
        goListCommand.addAll(Arrays.asList(MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG, MODULE_NAME));

        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, goListCommand))
            .getStandardOutputAsList();
    }

    private boolean isReadOnlyFlagSupported(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        List<String> goVersionOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, VERSION_COMMAND))
            .getStandardOutputAsList();
        Matcher matcher = GENERATE_GO_LIST_JSON_OUTPUT_PATTERN.matcher(goVersionOutput.get(0));
        if (matcher.find()) {
            String version = matcher.group(); // 1.16.5
            String[] parts = version.split("\\.");
            boolean majorVersionBeyond1 = Integer.parseInt(parts[0]) > 1;
            boolean minorVersion14AndUp = Integer.parseInt(parts[1]) >= 14;
            return majorVersionBeyond1 || minorVersion14AndUp;
        }
        return false;
    }

    public List<String> runGoModGraph(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, MOD_COMMAND, MOD_GRAPH_SUBCOMMAND))
            .getStandardOutputAsList();
    }

    public List<String> runGoModWhy(File directory, ExecutableTarget goExe, boolean vendorResults) throws ExecutableFailedException {
        // executing this command helps produce more accurate results. Parse the output to create a module exclusion list.
        List<String> commands = new LinkedList<>(Arrays.asList(MOD_COMMAND, MOD_WHY_SUBCOMMAND, MODULE_OUTPUT_FLAG));
        if (vendorResults) {
            commands.add(VENDOR_OUTPUT_FLAG);
        }
        commands.add(MODULE_NAME);
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, commands))
            .getStandardOutputAsList();
    }

}
