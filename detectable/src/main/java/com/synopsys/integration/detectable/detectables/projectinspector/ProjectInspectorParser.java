package com.synopsys.integration.detectable.detectables.projectinspector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorDependency;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorMavenCoordinate;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorModule;

// TODO: Should be split into a Parser/Transformer
public class ProjectInspectorParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private static final String MODULES_KEY = "Modules";

    public ProjectInspectorParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> parse(File outputFile) {
        List<CodeLocation> codeLocations = new ArrayList<>();

        if (outputFile == null || !outputFile.exists() || !outputFile.isFile()) {
            logger.info("inspection.json file doesn't exist.");
            return codeLocations;
        }

        //Utilized streaming parser to process the JSON file incrementally, without loading the entire file into memory.
        //Memory-efficient for processing a large(400-500MB) JSON file.
        try (JsonReader reader = new JsonReader(new FileReader(outputFile))) {
            reader.beginObject();
            while (reader.hasNext()) {
                String modulesObject = reader.nextName();
                if (modulesObject != null && modulesObject.equals(MODULES_KEY)) {
                    codeLocations = processModules(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            logger.error("An error occurred while reading inspection.json file", e);
        }
        return codeLocations;
    }

    public List<CodeLocation> processModules(JsonReader reader) throws IOException {
        List<CodeLocation> codeLocations = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String moduleId = reader.nextName();
            if (moduleId != null) {
                JsonObject module = JsonParser.parseReader(reader).getAsJsonObject();
                ProjectInspectorModule projectInspectorModule = gson.fromJson(module, ProjectInspectorModule.class);
                CodeLocation codeLocation = codeLocationFromModule(projectInspectorModule);
                codeLocations.add(codeLocation);
            }
        }
        reader.endObject();

        return codeLocations;
    }


    public CodeLocation codeLocationFromModule(ProjectInspectorModule module) {
        Map<String, Dependency> lookup = new HashMap<>();

        //build the map of all external ids
        module.dependencies.forEach(dependency -> lookup.computeIfAbsent(dependency.id, missingId -> convertProjectInspectorDependency(dependency)));

        //and add them to the graph
        DependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        module.dependencies.forEach(moduleDependency -> {
            Dependency dependency = lookup.get(moduleDependency.id);
            moduleDependency.includedBy.forEach(parent -> {
                if ("DIRECT".equals(parent)) {
                    mutableDependencyGraph.addDirectDependency(dependency);
                } else if (lookup.containsKey(parent)) {
                    mutableDependencyGraph.addChildWithParent(dependency, lookup.get(parent));
                } else { //Theoretically should not happen according to PI devs. -jp
                    throw new RuntimeException("An error occurred reading the project inspector output." +
                        " An unknown parent dependency was encountered '" + parent + "' while including dependency '" + moduleDependency.name + "'.");
                }
            });
        });
        return new CodeLocation(mutableDependencyGraph, new File(module.moduleFile));
    }

    public Dependency convertProjectInspectorDependency(ProjectInspectorDependency dependency) {
        if ("MAVEN".equals(dependency.dependencyType) && dependency.mavenCoordinate != null) {
            ProjectInspectorMavenCoordinate gav = dependency.mavenCoordinate;
            return new Dependency(gav.artifact, gav.version, externalIdFactory.createMavenExternalId(gav.group, gav.artifact, gav.version));
        } else if ("MAVEN".equals(dependency.dependencyType)) {
            logger.warn("Project Inspector Maven dependency did not have coordinates, using name and version only.");
            return new Dependency(dependency.name, dependency.version, externalIdFactory.createNameVersionExternalId(Forge.MAVEN, dependency.name, dependency.version));
        } else if ("NUGET".equals(dependency.dependencyType)) {
            return new Dependency(dependency.name, dependency.version, externalIdFactory.createNameVersionExternalId(Forge.NUGET, dependency.name, dependency.version));
        } else {
            throw new RuntimeException("Unknown Project Inspector dependency type: " + dependency.dependencyType);
        }
    }
}
