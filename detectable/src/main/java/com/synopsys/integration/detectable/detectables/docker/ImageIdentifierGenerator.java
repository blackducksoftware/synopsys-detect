package com.synopsys.integration.detectable.detectables.docker;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectables.docker.model.DockerInspectorResults;

public class ImageIdentifierGenerator {

    public String generate(ImageIdentifierType imageIdentifierType, String suppliedImagePiece, @Nullable DockerInspectorResults dockerResults) {
        if (imageIdentifierType.equals(ImageIdentifierType.IMAGE_ID)
            && (dockerResults != null)
            && StringUtils.isNotBlank(dockerResults.getImageRepo())
            && StringUtils.isNotBlank(dockerResults.getImageTag())
        ) {
            return dockerResults.getImageRepo() + ":" + dockerResults.getImageTag();
        }
        return suppliedImagePiece;
    }
}
