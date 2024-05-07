package com.synopsys.integration.detectable.detectables.setuptools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.util.TomlFileUtils;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.Extraction.Builder;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class SetupToolsExtractor {
    
    private final DetectableExecutableRunner executableRunner;
    
    private ExternalIdFactory externalIdFactory;

    public SetupToolsExtractor(DetectableExecutableRunner executableRunner, ExternalIdFactory externalIdFactory) {
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File sourceDirectory, File projectToml, ExecutableTarget pipExe) {
        try {
            TomlParseResult tomlParseResult = TomlFileUtils.parseFile(projectToml);
            
            // TODO get direct dependencies from Toml file. Eventually we'll need to get these from cfg and py files
            // instead if those exist or have dependencies. 
            //TODO The npm detector uses a more complex packager
            // and transformer approach but this seems pretty straightforward so not sure we need to follow that model
            Set<String> tomlDirectDependencies = parseDirectDependencies(tomlParseResult);

            DependencyGraph dependencyGraph = new BasicDependencyGraph();
            
            if (pipExe != null) {
                // Get dependencies by running pip show on each direct dependency
                for (String directDependency : tomlDirectDependencies) {
                    parseShowDependency(pipExe, sourceDirectory, dependencyGraph, directDependency, null);
                }
            } else {
                // Unable to determine transitive dependencies, add parsed dependencies directly
                // to the root of the graph.
                for (String directDependency : tomlDirectDependencies) {
                    Dependency currentDependency = entryToDependency(directDependency);
                    dependencyGraph.addChildrenToRoot(currentDependency);
                }
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

    private void parseShowDependency(ExecutableTarget pipExe, File sourceDirectory, DependencyGraph dependencyGraph, String dependencyToSearch, Dependency parentDependency) throws IOException, InterruptedException, ExecutableRunnerException {
        List<String> rawShowOutput = runPipShow(sourceDirectory, pipExe, dependencyToSearch);
        
        Map<String, String> showOutput = parsePipShow(rawShowOutput);
        
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
                parseShowDependency(pipExe, sourceDirectory, dependencyGraph, requiredPackage, currentDependency);
            }
        }
    }

    public List<String> runPipShow(File sourceDirectory, ExecutableTarget pipExe, String dependencyToSearch) throws ExecutableRunnerException {
        // TODO somewhere need checks to blow up if no dependencies are found.
        
        List<String> pipArguments = new ArrayList<>();
        pipArguments.add("show");
        pipArguments.add(dependencyToSearch);
        
        return executableRunner.execute(ExecutableUtils.createFromTarget(sourceDirectory, pipExe, pipArguments)).getStandardOutputAsList();
    }
    
    public Map<String, String> parsePipShow(List<String> rawShowOutput) {
        Map<String, String> showOutput = new HashMap<>();
        
        for (String line : rawShowOutput) {
            String[] parts = line.split(": ");
            if (parts.length >= 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (key.equals("Version") || key.equals("Requires")) {
                    showOutput.put(key, value);
                }
            }
        }

        return showOutput;
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
    
    private Dependency entryToDependency(String key) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, key);
        return new Dependency(externalId); 
    }
    
    private Dependency entryToDependency(String key, String value) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, key, value);
        return new Dependency(externalId);
    }
}
