package com.synopsys.integration.detect.detector.bitbake;

import java.util.Optional;

import org.junit.Test;

import com.synopsys.integration.detect.testutils.TestUtil;

public class BitbakeListTasksParserTest {
    @Test
    public void parseTargetArchitectureTest() {
        final TestUtil testUtil = new TestUtil();
        final String listtaskOutput = testUtil.getResourceAsUTF8String("/bitbake/listtasks_output.txt");
        final BitbakeListTasksParser bitbakeListTasksParser = new BitbakeListTasksParser();
        final Optional<String> architecture = bitbakeListTasksParser.parseTargetArchitecture(listtaskOutput);

        assert architecture.isPresent();
        System.out.println(architecture.get());
        assert architecture.get().equals("i586-poky-linux");
    }
}
