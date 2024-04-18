package com.synopsys.integration.detectable.detectables.pip.setuptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.synopsys.integration.detectable.extraction.Extraction;

public class SetupToolsExtractor {

    public Extraction extract(File projectToml) {
        // Get dependencies
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            //processBuilder.command(Arrays.asList("pip", "list", "-v"));
            processBuilder.command(Arrays.asList("pwd"));
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error executing pip command, exit code: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing pip command", e);
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
}
