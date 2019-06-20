package com.synopsys.integration.detect.airgap;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.help.DetectArgumentState;
import com.synopsys.integration.detect.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectors;

public class AirGapParsedValueTests {

    DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void allIncluded() {
        final String[] args = new String[] { "-z", "ALL" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.DOCKER.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.GRADLE.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }

    @Test
    public void dockerNotIncluded() {
        final String[] args = new String[] { "-z", "GRADLE,NUGET" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.DOCKER.name()));

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.GRADLE.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }

    @Test
    public void onlyNugetIncluded() {
        final String[] args = new String[] { "-z", "NUGET" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.DOCKER.name()));
        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.GRADLE.name()));

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }
}
