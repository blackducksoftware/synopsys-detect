package com.synopsys.integration.detectable.detectables.pip.setuptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detectable.extraction.Extraction;

public class SetupToolsExtractor {

    public Extraction extract(File projectToml, File projectDirectory) {
        String pathToScan = projectDirectory.toString();
        
        // Get dependencies
        try {
            // Create a virtual environment using venv
            executeCommand(Arrays.asList("python3", "-m", "venv", "SetupToolsDetectorTemp"), false);

            // Change directory to the virtual environment, run pip install and pip list
            // TODO this alters the project
            Path path = Paths.get("piptmpbuild");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            executeCommand(Arrays.asList("SetupToolsDetectorTemp/bin/pip", "install", pathToScan, "-t", "piptmpbuild"), false);
            // "-b", "piptmpbuild" // would have worked but was removed in pip 20.3

            executeCommand(Arrays.asList("SetupToolsDetectorTemp/bin/pip", "list"), false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error executing python command", e);
        } finally {
            try {
                // Remove the virtual environment
                executeCommand(Arrays.asList("rm", "-rf", "SetupToolsDetectorTemp"), false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // Build graph from dependencies
//        DependencyGraph dependencyGraph = new BasicDependencyGraph();
//        dependencyGraph.addChildrenToRoot(dependencies);
//
//        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
//
//        String projectName = StringUtils.stripToNull(combinedPackageJson.getName());
//        String projectVersion = StringUtils.stripToNull(combinedPackageJson.getVersion());
//
//        return new Extraction.Builder()
//            .success(codeLocation)
//            .projectName(projectName)
//            .projectVersion(projectVersion)
//            .build();
        
        return null;
    }
    
//    private List<Dependency> transformDependencies(MultiValuedMap<String, String> dependencies) {
//        if (dependencies == null || dependencies.size() == 0) {
//            return new ArrayList<>();
//        }
//        return dependencies.entries().stream()
//            .map(entry -> entryToDependency(entry.getKey(), entry.getValue()))
//            .collect(Collectors.toList());
//    }
//
//    private Dependency entryToDependency(String key, String value) {
//        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, key, value);
//        return new Dependency(externalId);
//    }

    private void executeCommand(List<String> command, boolean isShellCommand) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (isShellCommand) {
            List<String> shellCommand = new ArrayList<>();
            shellCommand.add("/bin/bash");
            shellCommand.add("-c");
            shellCommand.add(String.join(" ", command));
            processBuilder.command(shellCommand);
        } else {
            processBuilder.command(command);
        }
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (command.contains("list")) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Error executing command: " + command + ", exit code: " + exitCode);
        }
    }
}
