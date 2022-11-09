package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class GoModCommandRunner {
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
    private static final String FORMAT_FLAG = "-f";
    
    private static final String FORMAT_DIRECTS = "{{if not (or .Indirect .Main)}}{{.Path}}@{{.Version}}{{end}}";
    private static final String FORMAT_FOR_MAIN = "{{if (.Main)}}{{.Path}}{{end}}";

    private final DetectableExecutableRunner executableRunner;

    public GoModCommandRunner(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    // Excludes the "all" argument to return the root project modules
    public List<String> runGoList(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, LIST_COMMAND, MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG))
            .getStandardOutputAsList();
    }

    private void getGoListPreamble(List<String> commandList, GoVersion goVersion) {
        // Providing a readonly flag prevents the command from modifying customer's source.
        // this flag not supported prior to go 1.14.
        commandList.add(LIST_COMMAND);
        if (goVersion.getMajorVersion() > 1 || goVersion.getMinorVersion() >= 14) {
            // Providing a readonly flag prevents the command from modifying customer's source.
            commandList.add(LIST_READONLY_FLAG);
        }
    }

    // TODO: Utilize the fields "Main": true, and "Indirect": true, fields from the JSON output to avoid running go list twice. Before switching to json output we needed to run twice. JM-01/2022
    public List<String> runGoListAll(File directory, ExecutableTarget goExe, GoVersion goVersion) throws ExecutableFailedException {
        List<String> goListCommand = new LinkedList<>();
        getGoListPreamble(goListCommand, goVersion); // modify go list for version > 1.14

        goListCommand.addAll(Arrays.asList(MODULE_OUTPUT_FLAG, JSON_OUTPUT_FLAG, MODULE_NAME));

        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, goListCommand))
            .getStandardOutputAsList();
    }

    public String runGoVersion(File directory, ExecutableTarget goExe) throws ExecutableFailedException {
        List<String> goVersionOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, VERSION_COMMAND))
            .getStandardOutputAsList();
        return goVersionOutput.get(0);
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

    public List<String> runGoModDirectDeps(File directory, ExecutableTarget goExe, GoVersion goVersion) throws ExecutableFailedException {
        //This'll give all direct dependencies for the main module.
        List<String> goListCommand = new LinkedList<>();
        getGoListPreamble(goListCommand, goVersion); // modify go list for version > 1.14

        
        goListCommand.addAll(Arrays.asList(MODULE_OUTPUT_FLAG, FORMAT_FLAG, FORMAT_DIRECTS, MODULE_NAME));
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, goListCommand))
                .getStandardOutputAsList();
    }

    public String runGoModGetMainModule(File directory, ExecutableTarget goExe, GoVersion goVersion) throws ExecutableFailedException {
        //This gives the value of the main module name.
        List<String> goListCommand = new LinkedList<>();
        getGoListPreamble(goListCommand, goVersion); // modify go list for version > 1.14

        goListCommand.addAll(Arrays.asList(MODULE_OUTPUT_FLAG, FORMAT_FLAG, FORMAT_FOR_MAIN, MODULE_NAME));
        return executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directory, goExe, goListCommand))
                .getStandardOutputAsList().get(0);
    }

}
