package com.synopsys.integration.detectable.detectables.pip.setuptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class SetupToolsExtractor {
    
    private ExternalIdFactory externalIdFactory;

    public SetupToolsExtractor(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File projectToml) {
        // TODO eventually have to account, perhaps in a new set of files entirely, for a pip-less approach
        // Get dependencies by running pip list
        try {
            Map<String, String> pipDependencies = listDependencies();
        
            // TODO get direct dependencies from Toml file. Eventually we'll need to get these from cfg and py files
            // instead if those exist or have dependencies. Also, the npm detector uses a more complex packager
            // and transformer approach but this seems pretty straightforward so not sure we need to follow that model
            Set<String> tomlDirectDependencies = parseDirectDependencies(projectToml);
            
            List<Dependency> directDependencies = createDirectDependencies(pipDependencies, tomlDirectDependencies);
            
            // Build graph from dependencies
            DependencyGraph dependencyGraph = new BasicDependencyGraph();
            dependencyGraph.addChildrenToRoot(directDependencies);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

//            String projectName = StringUtils.stripToNull(combinedPackageJson.getName());
//            String projectVersion = StringUtils.stripToNull(combinedPackageJson.getVersion());

            return new Extraction.Builder()
                .success(codeLocation)
//                .projectName(projectName)
//                .projectVersion(projectVersion)
                .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<Dependency> createDirectDependencies(Map<String, String> pipDependencies, Set<String> tomlDirectDependencies) {
        List<Dependency> directDependencies = new ArrayList<>();
        
        for (String dependency : tomlDirectDependencies) {
            String normalizedDirectDependency = normalize(dependency);
            
            for (Map.Entry<String, String> pipDependency : pipDependencies.entrySet()) {
                String normalizedPipDependency = normalize(pipDependency.getKey());
                if (normalizedDirectDependency.equals(normalizedPipDependency)) {
                    directDependencies.add(entryToDependency(pipDependency.getKey(), pipDependency.getValue()));
                    break;
                }
            }
        }
        
        return directDependencies;
    }
    
    /**
     * Normalize Python package names as a.b, a-b, and a_b all refer to the same package
     * @param name Python package name
     * @return 
     */
    private String normalize(String name) {
        return name.replace('-', '_').replace('.', '_');
    }

    public Set<String> parseDirectDependencies(File projectToml) throws IOException {
        Set<String> results = new HashSet<>();
        
        TomlParseResult tomlParseResult = TomlFileUtils.parseFile(projectToml);
        
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

    private Map<String, String> listDependencies() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Arrays.asList("pip", "list"));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        Map<String, String> resultMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                resultMap.put(parts[0], parts[1]);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Error running pip list, exit code: " + exitCode);
        }

        return resultMap;
    }
}
