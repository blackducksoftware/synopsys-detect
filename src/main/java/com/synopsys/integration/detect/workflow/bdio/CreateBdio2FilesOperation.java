package com.synopsys.integration.detect.workflow.bdio;

import static com.synopsys.integration.detect.tool.detector.CodeLocationConverter.DETECT_FORGE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.Bdio;
import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.common.value.Product;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraphUtil;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.model.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.model.ProjectInfo;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Writer;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.util.NameVersion;

public class CreateBdio2FilesOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bdio2Factory bdio2Factory;
    private final DetectInfo detectInfo;

    public CreateBdio2FilesOperation(Bdio2Factory bdio2Factory, DetectInfo detectInfo) {
        this.bdio2Factory = bdio2Factory;
        this.detectInfo = detectInfo;
    }

    public List<UploadTarget> createBdioFiles(BdioCodeLocationResult bdioCodeLocationResult, File outputDirectory, NameVersion projectNameVersion)
        throws DetectUserFriendlyException {
        List<UploadTarget> uploadTargets = new ArrayList<>();
        for (BdioCodeLocation bdioCodeLocation : bdioCodeLocationResult.getBdioCodeLocations()) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            // Bdio 2
            String detectVersion = detectInfo.getDetectVersion();
            SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);
            String group = StringUtils.defaultIfBlank(bdioCodeLocation.getDetectCodeLocation().getExternalId().getGroup(), null);
            ProjectInfo projectInfo = ProjectInfo.nameVersionGroup(projectNameVersion, group);
            BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(
                codeLocationName,
                projectInfo,
                ZonedDateTime.now(),
                new Product.Builder().name(detectCreator.getIdentifier()).build()
            );
            bdioMetadata.scanType(Bdio.ScanType.PACKAGE_MANAGER);

            ProjectDependencyGraph projectDependencyGraph;
            if (dependencyGraph instanceof ProjectDependencyGraph) {
                // TODO: In 8.0.0 all CodeLocations should have a ProjectDependencyGraph instead of DependencyGraph and ExternalId JM-04/2022
                projectDependencyGraph = (ProjectDependencyGraph) dependencyGraph;
            } else {
                // Attempt to build a ProjectDependencyGraph with good project info
                ExternalId projectExternalId = externalId;
                if (externalId.getForge().equals(DETECT_FORGE)) {
                    projectExternalId = ExternalId.FACTORY.createNameVersionExternalId(DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion());
                }
                projectDependencyGraph = new ProjectDependencyGraph(new ProjectDependency(projectExternalId));
                DependencyGraphUtil.copyRootDependencies(projectDependencyGraph, dependencyGraph);
            }
            Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, projectDependencyGraph);

            Bdio2Writer bdio2Writer = new Bdio2Writer();
            File bdio2OutputFile = new File(outputDirectory, bdioCodeLocation.getBdioName() + ".bdio");

            try {
                OutputStream outputStream = new FileOutputStream(bdio2OutputFile);
                bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
                logger.debug(String.format("BDIO Generated: %s", bdio2OutputFile.getAbsolutePath()));

                uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdio2OutputFile));
            } catch (IOException e) {
                throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }

        return uploadTargets;
    }
}
