package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;

public class CodeLocationConverter {
    public static final Forge DETECT_FORGE = new Forge("/", "Detect");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public CodeLocationConverter(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Map<CodeLocation, DetectCodeLocation> toDetectCodeLocation(File detectSourcePath, Extraction extraction, File overridePath, String overrideName) {
        Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();

        for (CodeLocation codeLocation : extraction.getCodeLocations()) {
            File sourcePath = codeLocation.getSourcePath().orElse(overridePath);
            ExternalId externalId;
            if (!codeLocation.getExternalId().isPresent()) {
                logger.debug("The detector was unable to determine an external id for this code location, so an external id will be created using the file path.");
                String relativePath = FileNameUtils.relativize(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath());
                if (StringUtils.isNotBlank(relativePath)) {
                    externalId = externalIdFactory.createPathExternalId(DETECT_FORGE, relativePath);
                } else {// Relativize from the parent.
                    externalId = externalIdFactory.createPathExternalId(
                        DETECT_FORGE,
                        FileNameUtils.relativizeParent(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath())
                    );
                }

                logger.debug("The external id that was created is: {}", Arrays.asList(externalId.getExternalIdPieces()));
            } else {
                externalId = codeLocation.getExternalId().get();
            }
            Optional<String> dockerImageName = extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA);

            DetectCodeLocation detectCodeLocation = dockerImageName.map(s -> DetectCodeLocation.forDocker(codeLocation.getDependencyGraph(), sourcePath, externalId, s))
                .orElseGet(() -> DetectCodeLocation.forCreator(codeLocation.getDependencyGraph(), sourcePath, externalId, overrideName));

            detectCodeLocations.put(codeLocation, detectCodeLocation);
        }

        return detectCodeLocations;
    }
}
