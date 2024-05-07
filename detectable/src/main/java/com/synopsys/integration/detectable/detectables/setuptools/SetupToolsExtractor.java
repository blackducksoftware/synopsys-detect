package com.synopsys.integration.detectable.detectables.setuptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.TomlFileUtils;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.Extraction.Builder;

public class SetupToolsExtractor {
    
    private ExternalIdFactory externalIdFactory;

    public SetupToolsExtractor(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File projectToml) {
        // TODO eventually have to account, perhaps in a new set of files entirely, for a pip-less approach
        // Get dependencies by running pip show on each direct dependency
        try {
            TomlParseResult tomlParseResult = TomlFileUtils.parseFile(projectToml);
            
            // TODO get direct dependencies from Toml file. Eventually we'll need to get these from cfg and py files
            // instead if those exist or have dependencies. Also, the npm detector uses a more complex packager
            // and transformer approach but this seems pretty straightforward so not sure we need to follow that model
            Set<String> tomlDirectDependencies = parseDirectDependencies(tomlParseResult);

            DependencyGraph dependencyGraph = new BasicDependencyGraph();
            
            for (String directDependency : tomlDirectDependencies) {
                parseShowDependency(dependencyGraph, directDependency, null);
            }

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            String projectName = tomlParseResult.getString("project.name");
            String projectVersion = tomlParseResult.getString("project.version");

            Builder builder = new Extraction.Builder();
            builder.success(codeLocation);
            
            if (!StringUtils.isEmpty(projectName)) {
                builder.projectName(projectName);
            }
            
            if (!StringUtils.isEmpty(projectVersion)) {
                builder.projectVersion(projectVersion);
            }
            
            return builder.build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    // TODO poor name as this both runs and parses a dependency
    private void parseShowDependency(DependencyGraph dependencyGraph, String dependencyToSearch, Dependency parentDependency) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Arrays.asList("pip", "show", dependencyToSearch));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        Map<String, String> showOutput = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(": ");
            if (parts.length >= 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (key.equals("Version") || key.equals("Requires")) {
                    showOutput.put(key, value);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Error running pip show, exit code: " + exitCode);
        }
        
        Dependency currentDependency = entryToDependency(dependencyToSearch, showOutput.get("Version"));
        
        if (parentDependency == null) {
            // No parent, add to root of graph
            dependencyGraph.addChildrenToRoot(currentDependency);
        } else {
            dependencyGraph.addChildWithParent(currentDependency, parentDependency);
        }
        
        // See if we need to continue exploring this chain of dependencies
        if (showOutput.containsKey("Requires")) {
            String[] requiredPackages = showOutput.get("Requires").split(", ");
            for (String requiredPackage : requiredPackages) {
                parseShowDependency(dependencyGraph, requiredPackage, currentDependency);
            }
        }
    }

    public Set<String> parseDirectDependencies(TomlParseResult tomlParseResult) throws IOException {
        Set<String> results = new HashSet<>();
        
        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");
        
        // TODO I doubt this will handle strings that have versions or version specifiers
        for (int i = 0; i < dependencies.size(); i++) {
            results.add(dependencies.getString(i));
        }
        
        return results;
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
    private Dependency entryToDependency(String key, String value) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, key, value);
        return new Dependency(externalId);
    }
}
