package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.Bdio;
import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.common.value.Product;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.model.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.model.ProjectInfo;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Writer;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class CreateAggregateBdio2FileOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bdio2Factory bdio2Factory;
    private final DetectInfo detectInfo;

    public CreateAggregateBdio2FileOperation(Bdio2Factory bdio2Factory, DetectInfo detectInfo) {
        this.bdio2Factory = bdio2Factory;
        this.detectInfo = detectInfo;
    }

    public void writeAggregateBdio2File(AggregateCodeLocation aggregateCodeLocation, Bdio.ScanType scanType) throws DetectUserFriendlyException {
        String detectVersion = detectInfo.getDetectVersion();
        SpdxCreator detectCreator = SpdxCreator.createToolSpdxCreator("Detect", detectVersion);

        ExternalId projectExternalId = aggregateCodeLocation.getAggregateDependencyGraph().getProjectDependency().getExternalId();
        String group = StringUtils.defaultIfBlank(projectExternalId.getGroup(), null);
        ProjectInfo projectInfo = ProjectInfo.nameVersionGroupGit(
            aggregateCodeLocation.getProjectNameVersion(),
            group,
            aggregateCodeLocation.getGitInfo()
        );
        BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(
            aggregateCodeLocation.getCodeLocationName(),
            projectInfo,
            ZonedDateTime.now(),
            new Product.Builder().name(detectCreator.getIdentifier()).build()
        );
        bdioMetadata.scanType(scanType);

        Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, aggregateCodeLocation.getAggregateDependencyGraph());
        writeDocument(aggregateCodeLocation.getAggregateFile(), bdio2Document);
    }

    private void writeDocument(File aggregateFile, Bdio2Document bdio2Document) throws DetectUserFriendlyException {
        Bdio2Writer bdio2Writer = new Bdio2Writer();
        try {
            OutputStream outputStream = new FileOutputStream(aggregateFile);
            bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
            logger.debug(String.format("BDIO Generated: %s", aggregateFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}