/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

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

public class AggregateModeTransitiveOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleBdioFactory simpleBdioFactory;

    public AggregateModeTransitiveOperation(final SimpleBdioFactory simpleBdioFactory) {
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public DependencyGraph aggregateCodeLocations(final File sourcePath, final List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            final Dependency codeLocationDependency = createAggregateDependency(sourcePath, detectCodeLocation);
            aggregateDependencyGraph.addChildrenToRoot(codeLocationDependency);
            aggregateDependencyGraph.addGraphAsChildrenToParent(codeLocationDependency, detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateDependency(final File sourcePath, final DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().getName();
            version = codeLocation.getExternalId().getVersion();
        } catch (final Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }
        final ExternalId original = codeLocation.getExternalId();
        final String codeLocationSourcePath = codeLocation.getSourcePath().toString(); //TODO: what happens when docker is present or no source path or no external id!
        final File codeLocationSourceDir = new File(codeLocationSourcePath);
        final String relativePath = FileNameUtils.relativize(sourcePath.getAbsolutePath(), codeLocationSourceDir.getAbsolutePath());

        final String bomToolType;
        if (codeLocation.getDockerImageName().isPresent()) {
            bomToolType = "docker"; // TODO: Should docker image name be considered here?
        } else {
            bomToolType = codeLocation.getCreatorName().orElse("unknown").toLowerCase();
        }

        final List<String> externalIdPieces = new ArrayList<>(Arrays.asList(original.getExternalIdPieces()));
        if (StringUtils.isNotBlank(relativePath)) {
            externalIdPieces.add(relativePath);
        }
        externalIdPieces.add(bomToolType);
        final String[] pieces = externalIdPieces.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return new Dependency(name, version, new ExternalIdFactory().createModuleNamesExternalId(original.getForge(), pieces));
    }
}
