package com.blackduck.integration.detectable.detectables.opam.buildexe.parse;


import com.blackduck.integration.detectable.detectables.opam.parse.OpamParsedResult;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class OpamTreeParser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<OpamParsedResult> codeLocations = new ArrayList<>();
    private DependencyGraph currentGraph;
    private final File sourceDirectory;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    public OpamTreeParser(Gson gson, File sourceDirectory, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.sourceDirectory = sourceDirectory;
        this.externalIdFactory = externalIdFactory;
    }

    public List<OpamParsedResult> parseJsonTreeFile(File outputFile) throws IOException {
        if(outputFile == null || !outputFile.exists() || !outputFile.isFile()) {
            logger.warn("The opamTreeOutput.json file does not exist, the file was deleted, or an issue occurred while running the opam tree command.");
            return codeLocations;
        }

        try (JsonReader jsonReader = new JsonReader(new FileReader(outputFile))) {
            OpamJsonFileModule opamTreeModule = gson.fromJson(jsonReader, OpamJsonFileModule.class);
            for(OpamTreeProjectModule opamTreeProjectModule: opamTreeModule.opamProjects) {
                if(opamTreeProjectModule != null) {
                    processTreeJsonObject(opamTreeProjectModule);
                }
            }
        } catch (IOException e) {
            throw new IOException("There was an error while parsing the JSON file.",e);
        }

        return codeLocations;
    }

    public void processTreeJsonObject(OpamTreeProjectModule opamTreeProjectModule){
        if(opamTreeProjectModule != null) {
            String projectName = opamTreeProjectModule.name;
            String projectVersion = opamTreeProjectModule.version;

            initializeProject(projectName, projectVersion);

            if(!opamTreeProjectModule.dependencies.isEmpty()) {
                collectCodeLocations(opamTreeProjectModule.dependencies, null);
            }
        }
    }

    private void collectCodeLocations(List<OpamTreeDependencyModule> dependencyModules, Dependency parentDependency) {
        for(OpamTreeDependencyModule dependencyModule: dependencyModules) {
            if(dependencyModule != null) {
                String packageName = dependencyModule.name;
                String packageVersion = dependencyModule.version;

                Dependency dependency = createDependencyExternalId(packageName, packageVersion);

                if(parentDependency == null) {
                    currentGraph.addDirectDependency(dependency);
                } else {
                    currentGraph.addChildWithParent(dependency, parentDependency);
                }

                if(!dependencyModule.dependencies.isEmpty()) {
                    collectCodeLocations(dependencyModule.dependencies, dependency);
                }
            }
        }
    }

    private void initializeProject(String projectName, String projectVersion) {
        Dependency projectDependency = createDependencyExternalId(projectName, projectVersion); // create a dependency for the project
        currentGraph = new BasicDependencyGraph();
        String codeLocationPath = sourceDirectory.getPath();
        if(!codeLocationPath.endsWith(projectDependency.getName())) { // get the source code for the project using specific opam file
            codeLocationPath = "/" + projectDependency.getName();
        }
        CodeLocation codeLocation = new CodeLocation(currentGraph, projectDependency.getExternalId(), new File(codeLocationPath));
        codeLocations.add(new OpamParsedResult(projectName, projectName, codeLocation)); // create a new opam parsed result for the project being initialized
    }



    private Dependency createDependencyExternalId(String name, String version) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.OPAM, name, version);
        return new Dependency(externalId);
    }
}
