package com.synopsys.integration.detect.integration;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.VersionBomComponentView;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.exception.IntegrationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class DetectOnDetectPolaris {
    @Test
    @Ignore
    //ekerwin - dignoring this until we can test the output
    public void testPolarisInDetect() throws Exception {
        List<String> detectArgs = new ArrayList<>();
        detectArgs.add("--detect.tools.excluded=BAZEL,DETECTOR,DOCKER,SIGNATURE_SCAN,BINARY_SCAN");
        detectArgs.add("--polaris.server.url=https://qa02.dev.polaris.synopsys.com");
        detectArgs.add("--polaris.access.token=o2uhp95us937lb60qbra9jrl2p4rhun2mmn0fplpnoorhc2h4sj0");
        detectArgs.add("--detect.wait.for.results=true");

        Application.main(detectArgs.toArray(new String[detectArgs.size()]));
    }

}
