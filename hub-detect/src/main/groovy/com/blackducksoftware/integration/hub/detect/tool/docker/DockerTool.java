package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;

public class DockerTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public DockerTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DockerToolResult run() throws BomToolException {
        logger.info("Preparing to run Docker.");
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        BomToolEnvironment bomToolEnvironment = new BomToolEnvironment(directoryManager.getSourceDirectory(), Collections.emptySet(), 0, null, false);
        DockerBomTool dockerBomTool = detectContext.getBean(DockerBomTool.class, bomToolEnvironment);

        logger.info("Checking it applies.");
        BomToolResult applicableResult = dockerBomTool.applicable();
        if (applicableResult.getPassed()) {
            logger.info("Checking it is extractable.");
            BomToolResult extractableResult = dockerBomTool.extractable();
            if (extractableResult.getPassed()) {
                logger.info("Performing the extraction.");
                ExtractionId extractionId = new ExtractionId(BomToolGroupType.DOCKER, "docker");
                Extraction extractResult = dockerBomTool.extract(extractionId);

                DockerToolResult dockerToolResult = new DockerToolResult();
                dockerToolResult.dockerCodeLocations = extractResult.codeLocations;

                Optional<Object> dockerTar = extractResult.getMetaDataValue(DockerExtractor.DOCKER_TAR_META_DATA_KEY);
                if (dockerTar.isPresent()) {
                    dockerToolResult.dockerTar = Optional.of((File) dockerTar.get());
                }

                return dockerToolResult;
            } else {
                logger.error("Docker was not extractable even though the tool attempted to run.");
                logger.error(applicableResult.toDescription());
            }
        } else {
            logger.error("Docker was not applicable even though the tool attempted to run.");
            logger.error(applicableResult.toDescription());
        }

        return new DockerToolResult();
    }
}
