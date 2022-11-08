package com.synopsys.integration.detect.workflow.bdio.aggregation;

import static com.synopsys.integration.detect.tool.detector.CodeLocationConverter.DETECT_FORGE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraphUtil;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.util.NameVersion;

public class FullAggregateGraphCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProjectDependencyGraph aggregateCodeLocations(File sourcePath, NameVersion projectNameVersion, List<DetectCodeLocation> codeLocations) {
        ExternalId projectExternalId = ExternalId.FACTORY.createNameVersionExternalId(DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion());
        ProjectDependency projectDependency = new ProjectDependency(projectExternalId);
        ProjectDependencyGraph aggregateDependencyGraph = new ProjectDependencyGraph(projectDependency);

        for (DetectCodeLocation detectCodeLocation : codeLocations) {
            Dependency codeLocationDependency = createAggregateNode(sourcePath, detectCodeLocation);
            aggregateDependencyGraph.addDirectDependency(codeLocationDependency);
            DependencyGraphUtil.copyDirectDependenciesToParent(aggregateDependencyGraph, codeLocationDependency, detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateNode(File sourcePath, DetectCodeLocation codeLocation) {
        ExternalId original = codeLocation.getExternalId();
        List<String> externalIdPieces = new ArrayList<>(Arrays.asList(original.getExternalIdPieces()));

        NameVersion projectNameVersion = createProjectNameVersion(codeLocation);
        createRelativePath(sourcePath, projectNameVersion, codeLocation)
            .ifPresent(externalIdPieces::add);

        String bomToolType = createBomToolType(codeLocation);
        externalIdPieces.add(bomToolType);

        String[] pieces = externalIdPieces.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return new ProjectDependency(
            projectNameVersion.getName(),
            projectNameVersion.getVersion(),
            new ExternalIdFactory().createModuleNamesExternalId(original.getForge(), pieces)
        );
    }

    private NameVersion createProjectNameVersion(DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().getName();
            version = codeLocation.getExternalId().getVersion();
        } catch (Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }

        return new NameVersion(name, version);
    }

    private Optional<String> createRelativePath(File sourcePath, NameVersion projectNameVersion, DetectCodeLocation codeLocation) {
        String codeLocationSourcePath = codeLocation.getSourcePath().toString(); //TODO: what happens when docker is present or no source path or no external id!
        File codeLocationSourceDir = new File(codeLocationSourcePath);
        String relativePath = FileNameUtils.relativize(sourcePath.getAbsolutePath(), codeLocationSourceDir.getAbsolutePath());

        if (StringUtils.isNotBlank(relativePath) && !relativePath.equals(projectNameVersion.getName())) {
            // Adds the relative path only if the project name was not derived from it to avoid duplicate information
            return Optional.of(relativePath);
        }
        return Optional.empty();
    }

    private String createBomToolType(DetectCodeLocation codeLocation) {
        String bomToolTypeWithPrefix = "-";
        if (codeLocation.getDockerImageName().isPresent()) {
            bomToolTypeWithPrefix += "docker"; // TODO: Should docker image name be considered here?
        } else {
            bomToolTypeWithPrefix += codeLocation.getCreatorName()
                .orElse("unknown").toLowerCase();
        }
        return bomToolTypeWithPrefix;
    }
}
