package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.util.Collections;

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
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        BomToolEnvironment bomToolEnvironment = new BomToolEnvironment(directoryManager.getSourceDirectory(), Collections.emptySet(), 0, null, false);
        DockerBomTool dockerBomTool = detectContext.getBean(DockerBomTool.class, bomToolEnvironment);

        logger.info("Will run the docker tool.");

        BomToolResult applicableResult = dockerBomTool.applicable();
        BomToolResult extractableResult = dockerBomTool.extractable();
        ExtractionId extractionId = new ExtractionId(BomToolGroupType.DOCKER, "docker");
        Extraction extractResult = dockerBomTool.extract(extractionId);
        DockerToolResult dockerToolResult = new DockerToolResult();
        dockerToolResult.dockerCodeLocations = extractResult.codeLocations;
        return dockerToolResult;
        //TODO get docker file!
    }
}
