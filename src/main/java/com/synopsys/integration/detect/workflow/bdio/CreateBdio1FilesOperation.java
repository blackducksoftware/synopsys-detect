package com.synopsys.integration.detect.workflow.bdio;

import static com.synopsys.integration.detect.tool.detector.CodeLocationConverter.DETECT_FORGE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraphUtil;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.BdioBillOfMaterials;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.util.NameVersion;

public class CreateBdio1FilesOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectBdioWriter detectBdioWriter;
    private final SimpleBdioFactory simpleBdioFactory;

    public CreateBdio1FilesOperation(DetectBdioWriter detectBdioWriter, SimpleBdioFactory simpleBdioFactory) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public List<UploadTarget> createBdioFiles(BdioCodeLocationResult bdioCodeLocationResult, File outputDirectory, NameVersion projectNameVersion)
        throws DetectUserFriendlyException {
        logger.debug("Creating BDIO files from code locations.");
        List<UploadTarget> uploadTargets = new ArrayList<>();
        for (BdioCodeLocation bdioCodeLocation : bdioCodeLocationResult.getBdioCodeLocations()) {
            String codeLocationName = bdioCodeLocation.getCodeLocationName();
            ExternalId externalId = bdioCodeLocation.getDetectCodeLocation().getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.getDetectCodeLocation().getDependencyGraph();

            ProjectDependencyGraph projectDependencyGraph = new ProjectDependencyGraph(new ProjectDependency(externalId));
            DependencyGraphUtil.copyRootDependencies(projectDependencyGraph, dependencyGraph);

            File bdioOutputFile = new File(outputDirectory, bdioCodeLocation.getBdioName() + ".jsonld");

            ExternalId projectExternalId = externalId;
            if (externalId.getForge().equals(DETECT_FORGE)) {
                projectExternalId = ExternalId.FACTORY.createNameVersionExternalId(DETECT_FORGE, projectNameVersion.getName(), projectNameVersion.getVersion());
            }
            BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(new BdioPropertyHelper());
            BdioProject bdioProject = bdioNodeFactory.createProject(
                projectNameVersion.getName(),
                projectNameVersion.getVersion(),
                projectExternalId.createBdioId(),
                projectExternalId
            );

            BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, bdioProject.name, bdioProject.version);
            SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
            simpleBdioDocument.setBillOfMaterials(billOfMaterials);
            simpleBdioDocument.setProject(bdioProject);

            simpleBdioFactory.populateComponents(simpleBdioDocument, projectDependencyGraph);
            // TODO: In 8.0.0
            //  SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createPopulatedBdioDocument(codeLocationName, projectDependencyGraph);

            detectBdioWriter.writeBdioFile(bdioOutputFile, simpleBdioDocument);
            uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, bdioOutputFile));
        }

        return uploadTargets;
    }
}
