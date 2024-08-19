package com.synopsys.integration.detectable.detectables.opam.transform;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.opam.parse.OpamFileParser;
import com.synopsys.integration.detectable.detectables.opam.parse.OpamParsedResult;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

public class OpamGraphTransformer {

    private static final String version = "version";
    private static final String depends = "depends";

    private final File sourceDirectory;
    private final ExternalIdFactory externalIdFactory;
    private final DetectableExecutableRunner executableRunner;
    Map<Dependency, Set<Dependency>> visitedDependenciesGraph = new HashMap<>();
    public OpamGraphTransformer(File sourceDirectory, ExternalIdFactory externalIdFactory, DetectableExecutableRunner executableRunner) {
        this.sourceDirectory = sourceDirectory;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
    }

    public DependencyGraph transform(ExecutableTarget opamExe, OpamParsedResult opamParsedResult) throws ExecutableRunnerException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        if(opamExe != null) {
            for(String dependency: opamParsedResult.getParsedDirectDependencies()) {
                findTransitiveDependencies(dependency, opamExe, dependencyGraph, new OpamFileParser(), null);
            }
        } else {
            addDirectDependenciesToGraph(opamParsedResult, dependencyGraph); // no transitive dependencies will be detected
        }

        return dependencyGraph;
    }

    public String getProjectVersion(ExecutableTarget opamExe, String projectName) throws ExecutableRunnerException {
        List<String> arguments = new ArrayList<>();
        arguments.add("show");
        arguments.add(projectName);
        arguments.add("--field=version");

        String version = "";

        ExecutableOutput executableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(sourceDirectory, opamExe, arguments));

        if(executableOutput.getReturnCode() == 0) {
            List<String> output = executableOutput.getStandardOutputAsList();
            if(!output.isEmpty()) {
                return output.get(0);
            }
        } else {
            throw new ExecutableRunnerException(new Exception("There was an error running opam show on the "+ projectName +" package. Please run opam install . and try running the scan again."));
        }
        return version;
    }

    private void addDirectDependenciesToGraph(OpamParsedResult opamParsedResult, DependencyGraph dependencyGraph) {
        List<String> directDependencies = opamParsedResult.getParsedDirectDependencies();
        Map<String, String> opamLockDependencies = opamParsedResult.getLockFileDependencies();

        for(String dependency: opamLockDependencies.keySet()) {
            if(directDependencies.contains(dependency)) {
                String packageVersion = opamLockDependencies.get(dependency);
                //convert to OPAM dependency
                //add Dependency to root
            }
        }
    }

    private void findTransitiveDependencies(String dependency, ExecutableTarget opamExe, DependencyGraph dependencyGraph, OpamFileParser parser, Dependency parentDependency) throws ExecutableRunnerException {
        Optional<Dependency> dependencyOptional = visitedDependenciesGraph.keySet().stream().filter(dep -> dep.getName().equals(dependency)).findFirst();
        if(dependencyOptional.isPresent() && visitedDependenciesGraph.containsKey(dependencyOptional.get())) {
            Dependency dependency1 = dependencyOptional.get();
            dependencyGraph.addParentWithChildren(dependency1, visitedDependenciesGraph.get(dependency1));
            addChildDependencies(dependencyGraph, visitedDependenciesGraph.get(dependency1));
        } else {
            List<String> output = runOpamShow(opamExe, dependency);

            if (output.isEmpty()) {
                return;
            }

            Map<String, String> parsedOutput = parser.parseData(output);

            String versionOutput = parsedOutput.get(version);

            Dependency createdDependency = createExternalId(dependency, versionOutput);

            visitedDependenciesGraph.put(createdDependency, new HashSet<>());

            if (parentDependency == null) {
                dependencyGraph.addDirectDependency(createdDependency);
            } else {
                dependencyGraph.addChildWithParent(createdDependency, parentDependency);
                visitedDependenciesGraph.get(parentDependency).add(createdDependency);
            }

            if (parsedOutput.containsKey(depends)) {
                String[] dependentPackages = parsedOutput.get(depends).split(", ");
                for (String dependencyPackage : dependentPackages) {
                    findTransitiveDependencies(dependencyPackage, opamExe, dependencyGraph, parser, createdDependency);
                }
            }
        }
    }

    private List<String> runOpamShow(ExecutableTarget opamExe, String dependency) throws ExecutableRunnerException {
        List<String> arguments = new ArrayList<>();
        arguments.add("show");
        arguments.add(dependency);
        arguments.add("--raw"); // adding this will show the output as file.

        ExecutableOutput executableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(sourceDirectory, opamExe, arguments));

        if(executableOutput.getReturnCode() == 0) {
            return executableOutput.getStandardOutputAsList();
        } else {
            throw new ExecutableRunnerException(new Exception("There was an error running opam show on the "+ dependency +" package. Please run opam install . and try running the scan again."));
        }
    }

    private Dependency createExternalId (String name, String version) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
        return new Dependency(externalId);
    }

    private void addChildDependencies(DependencyGraph dependencyGraph, Set<Dependency> setOfChildren) {
        for(Dependency childDependency: setOfChildren) {
            if(visitedDependenciesGraph.containsKey(childDependency) && !visitedDependenciesGraph.get(childDependency).isEmpty()) {
                dependencyGraph.addParentWithChildren(childDependency, visitedDependenciesGraph.get(childDependency));
                addChildDependencies(dependencyGraph, visitedDependenciesGraph.get(childDependency));
            }
        }
    }
}
