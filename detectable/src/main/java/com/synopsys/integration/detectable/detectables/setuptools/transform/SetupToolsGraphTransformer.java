package com.synopsys.integration.detectable.detectables.setuptools.transform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class SetupToolsGraphTransformer {

    private static final String UNEXPECTED_PIP_OUTPUT = "Unexpected empty pip show results. Please run pip install . on the project and try running Detect again.";
    
    private File sourceDirectory;
    private ExternalIdFactory externalIdFactory;
    private DetectableExecutableRunner executableRunner;

    public SetupToolsGraphTransformer(File sourceDirectory, ExternalIdFactory externalIdFactory, DetectableExecutableRunner executableRunner) {
        this.sourceDirectory = sourceDirectory;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
    }

    public DependencyGraph transform(ExecutableTarget pipExe, SetupToolsParsedResult parsedResult) throws ExecutableRunnerException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        
        if (pipExe != null) {
            // Get dependencies by running pip show on each direct dependency
            for (PythonDependency directDependency : parsedResult.getDirectDependencies()) {
                handleShowDependency(pipExe, sourceDirectory, dependencyGraph, directDependency.getName(), null);
            }
        } else {
            // Unable to determine transitive dependencies, add parsed dependencies directly
            // to the root of the graph.
            handleParsedDependencies(parsedResult, dependencyGraph);
        }
        
        return dependencyGraph;
    }

    public void handleParsedDependencies(SetupToolsParsedResult parsedResult, DependencyGraph dependencyGraph) {
        List<PythonDependency> directDependencies = parsedResult.getDirectDependencies();
        
        for (PythonDependency directDependency : directDependencies) {
            String name = directDependency.getName();
            String version = directDependency.getVersion();
            
            Dependency currentDependency;
            if (StringUtils.isEmpty(version)) {
                currentDependency = entryToDependency(name);
            } else {
                currentDependency = entryToDependency(name, version);
            }
            dependencyGraph.addChildrenToRoot(currentDependency);
        }
    }
    
    private void handleShowDependency(ExecutableTarget pipExe, File sourceDirectory, DependencyGraph dependencyGraph, String dependencyToSearch, Dependency parentDependency) throws ExecutableRunnerException {
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
                handleShowDependency(pipExe, sourceDirectory, dependencyGraph, requiredPackage, currentDependency);
            }
        }
    }
    
    public List<String> runPipShow(File sourceDirectory, ExecutableTarget pipExe, String dependencyToSearch) throws ExecutableRunnerException {      
        List<String> pipArguments = new ArrayList<>();
        pipArguments.add("show");
        pipArguments.add(dependencyToSearch);
        
        ExecutableOutput executableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(sourceDirectory, pipExe, pipArguments));
        
        if (executableOutput.getReturnCode() == 0) {
            return executableOutput.getStandardOutputAsList();
        } else {
            throw new ExecutableRunnerException(new Exception(UNEXPECTED_PIP_OUTPUT));
        }
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
 
    private Dependency entryToDependency(String key) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, key);
        return new Dependency(externalId); 
    }
    
    private Dependency entryToDependency(String key, String value) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, key, value);
        return new Dependency(externalId);
    }
}