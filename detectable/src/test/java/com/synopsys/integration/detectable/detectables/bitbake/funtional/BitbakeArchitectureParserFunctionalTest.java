package com.synopsys.integration.detectable.detectables.bitbake.funtional;

import java.util.Optional;

import org.junit.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class BitbakeArchitectureParserFunctionalTest {
    @Test
    public void foundArchitectureInFullOuput() {
        final String listTaskOutput = FunctionalTestFiles.asString("/bitbake/Bitbake_ListTasks_Full.txt");

        final BitbakeArchitectureParser bitbakeArchitectureParser = new BitbakeArchitectureParser();
        final Optional<String> architecture = bitbakeArchitectureParser.architectureFromOutput(listTaskOutput);

        assert architecture.get().equals("i586-poky-linux");
    }
}
