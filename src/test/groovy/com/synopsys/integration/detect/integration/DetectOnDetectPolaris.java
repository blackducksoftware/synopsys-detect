package com.synopsys.integration.detect.integration;

import com.synopsys.integration.detect.Application;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Tag("integration")
public class DetectOnDetectPolaris {
    @Test
    @Disabled
    //ekerwin - disable this until we can test the output
    public void testPolarisInDetect() throws Exception {
        List<String> detectArgs = new ArrayList<>();
        detectArgs.add("--detect.tools.excluded=BAZEL,DETECTOR,DOCKER,SIGNATURE_SCAN,BINARY_SCAN");
        detectArgs.add("--polaris.server.url=https://qa02.dev.polaris.synopsys.com");
        detectArgs.add("--polaris.access.token=o2uhp95us937lb60qbra9jrl2p4rhun2mmn0fplpnoorhc2h4sj0");
        detectArgs.add("--detect.wait.for.results=true");

        Application.main(detectArgs.toArray(new String[detectArgs.size()]));
    }

}
