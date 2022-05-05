package com.synopsys.integration.detect.workflow.bdio;

import static com.synopsys.integration.detect.tool.detector.CodeLocationConverter.DETECT_FORGE;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraphUtil;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class CreateAggregateCodeLocationOperation {
    private final ExternalIdFactory externalIdFactory;
    private final CodeLocationNameManager codeLocationNameManager;

    public CreateAggregateCodeLocationOperation(ExternalIdFactory externalIdFactory, CodeLocationNameManager codeLocationNameManager) {
        this.externalIdFactory = externalIdFactory;
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public AggregateCodeLocation createAggregateCodeLocation(
        File bdioOutputDirectory,
        DependencyGraph aggregateDependencyGraph,
        NameVersion projectNameVersion,
        String bdioFileName
    ) {
        ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion());
        String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);

        String defaultFileName = new IntegrationEscapeUtil().replaceWithUnderscore(projectNameVersion.getName() + "_" + projectNameVersion.getVersion());
        String fileName = StringUtils.defaultIfBlank(bdioFileName, defaultFileName);
        fileName = fileName + ".bdio";
        File aggregateBdioFile = new File(bdioOutputDirectory, fileName);

        // TODO: Stop-gap measure to avoid changes propagating. Shouldn't be a problem in 8.0.0 JM-04/2022
        ProjectDependencyGraph projectDependencyGraph = new ProjectDependencyGraph(new ProjectDependency(projectExternalId));
        DependencyGraphUtil.copyRootDependencies(projectDependencyGraph, aggregateDependencyGraph);

        return new AggregateCodeLocation(aggregateBdioFile, codeLocationName, projectNameVersion, projectExternalId, projectDependencyGraph);
    }
}
