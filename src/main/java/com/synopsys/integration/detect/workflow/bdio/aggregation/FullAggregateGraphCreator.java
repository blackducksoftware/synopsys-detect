package com.synopsys.integration.detect.workflow.bdio.aggregation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;

public class FullAggregateGraphCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SimpleBdioFactory simpleBdioFactory;

    public FullAggregateGraphCreator(SimpleBdioFactory simpleBdioFactory) {
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public DependencyGraph aggregateCodeLocations(ProjectNodeCreator projectDependencyCreator, File sourcePath, List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (DetectCodeLocation detectCodeLocation : codeLocations) {
            Dependency codeLocationDependency = createAggregateNode(projectDependencyCreator, sourcePath, detectCodeLocation);
            aggregateDependencyGraph.addChildrenToRoot(codeLocationDependency);
            aggregateDependencyGraph.addGraphAsChildrenToParent(codeLocationDependency, detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateNode(ProjectNodeCreator projectDependencyCreator, File sourcePath, DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().getName();
            version = codeLocation.getExternalId().getVersion();
        } catch (Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }
        ExternalId original = codeLocation.getExternalId();
        String codeLocationSourcePath = codeLocation.getSourcePath().toString(); //TODO: what happens when docker is present or no source path or no external id!
        File codeLocationSourceDir = new File(codeLocationSourcePath);
        String relativePath = FileNameUtils.relativize(sourcePath.getAbsolutePath(), codeLocationSourceDir.getAbsolutePath());

        String bomToolType;
        if (codeLocation.getDockerImageName().isPresent()) {
            bomToolType = "docker"; // TODO: Should docker image name be considered here?
        } else {
            bomToolType = codeLocation.getCreatorName().orElse("unknown").toLowerCase();
        }

        List<String> externalIdPieces = new ArrayList<>(Arrays.asList(original.getExternalIdPieces()));
        if (StringUtils.isNotBlank(relativePath)) {
            externalIdPieces.add(relativePath);
        }
        externalIdPieces.add(bomToolType);
        String[] pieces = externalIdPieces.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return projectDependencyCreator.create(name, version, new ExternalIdFactory().createModuleNamesExternalId(original.getForge(), pieces));
    }

}
