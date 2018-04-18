package com.blackducksoftware.integration.hub.detect.bomtool.docker;


import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class DockerApplicableResult extends BomToolApplicableResult {
    private final String dockerExe;
    private final String bashExe;

    public DockerApplicableResult(final File searchedDirectory, final String dockerExe, final String bashExe) {
        super(searchedDirectory, BomToolType.DOCKER);
        this.dockerExe = dockerExe;
        this.bashExe = bashExe;
    }

    public String getDockerExe() {
        return dockerExe;
    }

    public String getBashExe() {
        return bashExe;
    }

}
