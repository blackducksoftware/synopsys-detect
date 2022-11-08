package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetContainer;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetContainerType;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetInspection;

public class NugetInspectorParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NugetInspectorParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NugetParseResult createCodeLocation(String dependencyFileText) {
        NugetInspection nugetInspection = gson.fromJson(dependencyFileText, NugetInspection.class);

        List<CodeLocation> codeLocations = new ArrayList<>();
        String projectName = "";
        String projectVersion = "";
        for (NugetContainer it : nugetInspection.containers) {
            Optional<NugetParseResult> possibleParseResult = createDetectCodeLocationFromNugetContainer(it);
            if (possibleParseResult.isPresent()) {
                NugetParseResult result = possibleParseResult.get();
                if (StringUtils.isNotBlank(result.getProjectName())) {
                    projectName = result.getProjectName();
                    projectVersion = result.getProjectVersion();
                }
                codeLocations.addAll(result.getCodeLocations());
            }
        }

        return new NugetParseResult(projectName, projectVersion, codeLocations);
    }

    private Optional<NugetParseResult> createDetectCodeLocationFromNugetContainer(NugetContainer nugetContainer) {
        NugetParseResult parseResult;
        String projectName = "";
        String projectVersionName = "";

        if (nugetContainer == null) {
            return Optional.empty();
        }

        if (NugetContainerType.SOLUTION == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            List<CodeLocation> codeLocations = new ArrayList<>();
            for (NugetContainer container : nugetContainer.children) {
                if (container == null)
                    continue;

                NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder();
                builder.addPackageSets(container.packages);
                DependencyGraph children = builder.createDependencyGraph(container.dependencies);
                if (StringUtils.isBlank(projectVersionName)) {
                    projectVersionName = container.version;
                }

                CodeLocation codeLocation = new CodeLocation(
                    children,
                    externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName),
                    convertSourcePath(container.sourcePath)
                );
                codeLocations.add(codeLocation);
            }
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocations);
        } else if (NugetContainerType.PROJECT == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder();
            builder.addPackageSets(nugetContainer.packages);
            DependencyGraph children = builder.createDependencyGraph(nugetContainer.dependencies);

            CodeLocation codeLocation = new CodeLocation(
                children,
                externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName),
                convertSourcePath(nugetContainer.sourcePath)
            );
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocation);
        } else {
            parseResult = null;
        }

        return Optional.ofNullable(parseResult);
    }

    private File convertSourcePath(String sourcePath) {//TODO: Seem to be getting a relative path for nuget... not sure where to look, something like "folder/./project/"
        File fileSourcePath = null;
        if (StringUtils.isNotBlank(sourcePath)) {
            fileSourcePath = new File(sourcePath);
        }

        return fileSourcePath;
    }
}
