package com.synopsys.integration.detectable.detectables.projectinspector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCodeLocationPackager;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorComponent;
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
    private final Map<String,Set<String>> shadedDependencies = new HashMap<>();
    private boolean versionMismatch = false;

    public ProjectInspectorParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> parse(File outputFile, boolean includeShadedDependencies) throws Exception {
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
                    codeLocations = processModules(reader, includeShadedDependencies);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return codeLocations;
    }

    public List<CodeLocation> processModules(JsonReader reader, boolean includeShadedDependencies) throws IOException {
        List<CodeLocation> codeLocations = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String moduleId = reader.nextName();
            if (moduleId != null) {
                JsonObject module = JsonParser.parseReader(reader).getAsJsonObject();
                ProjectInspectorModule projectInspectorModule = gson.fromJson(module, ProjectInspectorModule.class);
                if(projectInspectorModule.components != null) {
                    if (includeShadedDependencies) {
                        processShadedDependencies(projectInspectorModule);
                    } else {
                        CodeLocation codeLocation = codeLocationFromModule(projectInspectorModule);
                        codeLocations.add(codeLocation);
                    }
                } else {
                    versionMismatch = true;
                }
            }
        }
        reader.endObject();

        if(versionMismatch) {
            throw new RuntimeException("Detect and Project Inspector version mismatch, confirm that compatible versions of Detect and Project Inspector are in use.");
        }

        return codeLocations;
    }


    public CodeLocation codeLocationFromModule(ProjectInspectorModule module) {
        Map<String, Dependency> lookup = new HashMap<>();
        Map<String, Dependency> modules = new HashMap<>();

            //build the map of all external ids
        module.components.forEach((dependencyId, component) -> {
            if(!component.dependencySource.equals("MODULE")) {
                lookup.computeIfAbsent(dependencyId, missingId -> convertProjectInspectorDependency(component));
            } else {
                modules.computeIfAbsent(dependencyId, missingId -> convertProjectInspectorDependency(component));
            }
        });

        //and add them to the graph
        DependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        module.components.forEach((moduleDependencyId, moduleDependency) -> {
            Dependency dependency = lookup.getOrDefault(moduleDependencyId, null);
            if (dependency != null && moduleDependency.inclusionType.equals("DIRECT")) {
                mutableDependencyGraph.addDirectDependency(dependency);
            } else if (dependency != null && moduleDependency.inclusionType.equals("TRANSITIVE")) {
                moduleDependency.includedBy.forEach(includedBy -> {
                    if (lookup.containsKey(includedBy.id)) {
                        mutableDependencyGraph.addChildWithParent(dependency, lookup.get(includedBy.id));
                    } else if (!modules.containsKey(includedBy.id)) { //Theoretically should not happen according to PI devs. -jp
                        throw new RuntimeException("An error occurred reading the project inspector output." +
                                " An unknown parent dependency was encountered '" + includedBy.id + "' while including dependency '" + moduleDependency.name + "'.");
                    }
                });
            }
        });
        return new CodeLocation(mutableDependencyGraph, new File(module.moduleFile));
    }

    public void processShadedDependencies(ProjectInspectorModule module) {
        module.components.forEach((dependencyId, component) -> {
            if (component.shadedBy != null) {
                String[] gavParts = component.shadedBy.description.split(":");
                String group = gavParts[0];
                String artifact = gavParts[1];
                String version;
                if (gavParts.length > 4) {
                    version = gavParts[gavParts.length - 2];
                } else {
                    version = gavParts[gavParts.length - 1];
                }
                String gav = String.join(":", group, artifact, version);
                if (shadedDependencies.containsKey(gav)) {
                    shadedDependencies.get(gav).add(component.name);
                } else {
                    shadedDependencies.put(gav, new HashSet<>(Arrays.asList(component.name)));
                }
            }
        });
    }

    public Dependency convertProjectInspectorDependency(ProjectInspectorComponent component) {
        if ("MAVEN".equals(component.dependencyType) && component.mavenCoordinate != null) {
            ProjectInspectorMavenCoordinate gav = component.mavenCoordinate;
            return new Dependency(gav.artifact, gav.version, externalIdFactory.createMavenExternalId(gav.group, gav.artifact, gav.version));
        } else if ("MAVEN".equals(component.dependencyType)) {
            logger.warn("Project Inspector Maven dependency did not have coordinates, using name and version only.");
            return new Dependency(component.name, component.version, externalIdFactory.createNameVersionExternalId(Forge.MAVEN, component.name, component.version));
        } else if ("NUGET".equals(component.dependencyType)) {
            return new Dependency(component.name, component.version, externalIdFactory.createNameVersionExternalId(Forge.NUGET, component.name, component.version));
        } else {
            throw new RuntimeException("Unknown Project Inspector dependency type: " + component.dependencyType);
        }
    }

    public Map<String, Set<String>> getShadedDependencies() {
        return shadedDependencies;
    }
}
