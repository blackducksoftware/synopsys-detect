package com.blackduck.integration.detect.airgap;

import com.blackduck.integration.detect.configuration.help.DetectArgumentState;
import com.blackduck.integration.detect.configuration.help.DetectArgumentStateParser;
import com.blackduck.integration.detect.workflow.airgap.AirGapType;
import com.blackduck.integration.detect.workflow.airgap.AirGapTypeDecider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AirGapParsedValueTests {

    DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void fullAirGap() {
        String[] args = new String[] { "-z", "FULL" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.FULL, type);
    }

    @Test
    public void noDocker() {
        String[] args = new String[] { "-z", "NO_DOCKER" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.NO_DOCKER, type);
    }

    @Test
    public void defaultIsFull() {
        String[] args = new String[] { "-z" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.FULL, type);
    }
}
