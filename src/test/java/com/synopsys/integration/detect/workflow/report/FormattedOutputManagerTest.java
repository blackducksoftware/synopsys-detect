package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.output.FormattedCodeLocationOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutputManager;
import com.synopsys.integration.detect.workflow.status.FormattedCodeLocation;

public class FormattedOutputManagerTest {
    @Test
    public void formattedCodeLocationTest() {
        // Setup the reporting infrastructure
        EventSystem eventSystem = new EventSystem();
        FormattedOutputManager formattedOutputManager = new FormattedOutputManager(eventSystem);
        
        // Mock a completed scan to report on
        List<FormattedCodeLocation> codeLocationExpected = new ArrayList<>();
        codeLocationExpected.add(new FormattedCodeLocation(null, UUID.randomUUID(), DetectTool.DETECTOR.name()));
        
        // Publish the event
        eventSystem.publishEvent(Event.CodeLocationsCompleted, codeLocationExpected);
        
        DetectInfo detectInfo = new DetectInfo("", null, "");
        FormattedOutput formattedOutput = formattedOutputManager.createFormattedOutput(detectInfo, ExitCodeType.SUCCESS);
        
        List<FormattedCodeLocationOutput> codeLocationActual = formattedOutput.codeLocations;
        Assertions.assertEquals(1, codeLocationActual.size());
        Assertions.assertEquals(codeLocationExpected.get(0).getScanId(), codeLocationActual.get(0).scanId);
        Assertions.assertEquals(codeLocationExpected.get(0).getScanType(), codeLocationActual.get(0).scanType);
    }
}

/*
// TODO Jordan deleted this test. It would require a re-write
package com.synopsys.integration.detect.workflow.report;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.output.FormattedDetectorOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutputManager;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorStatusCode;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;

public class FormattedOutputManagerTest {

    @Test
    public <T extends Detectable> void detectorOutputStatusDataTest() throws IllegalAccessException {
        EventSystem eventSystem = new EventSystem();
        FormattedOutputManager formattedOutputManager = new FormattedOutputManager(eventSystem);

        DetectorRule rule = Mockito.mock(DetectorRule.class);
        Mockito.when(rule.getDescriptiveName()).thenReturn("");
        Mockito.when(rule.getName()).thenReturn("");
        Mockito.when(rule.getDetectorType()).thenReturn(DetectorType.GO_MOD);

        DetectorEvaluation detectorEvaluation = new DetectorEvaluation(rule);
        ExecutableNotFoundDetectableResult result = new ExecutableNotFoundDetectableResult("go");
        DetectorResult extractableResult = new DetectorResult(result.getPassed(), result.toDescription(), result.getClass(), Collections.emptyList(), Collections.emptyList());
        detectorEvaluation.setExtractable(extractableResult);
        detectorEvaluation.setApplicable(new DetectorResult(true, "", Collections.emptyList(), Collections.emptyList()));
        detectorEvaluation.setSearchable(new DetectorResult(true, "", Collections.emptyList(), Collections.emptyList()));
        detectorEvaluation.setDetectableEnvironment(new DetectableEnvironment(new File("")));

        DetectorToolResult detectorToolResult = new DetectorToolResult(
            null,
            GitInfo.none(),
            null,
            null,
            new HashSet<>(),
            new DetectorEvaluationTree(null, 0, null, Collections.singletonList(detectorEvaluation), new HashSet<>()),
            null
        );
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);

        DetectInfo detectInfo = new DetectInfo("", null, "");
        FormattedOutput formattedOutput = formattedOutputManager.createFormattedOutput(detectInfo);
        FormattedDetectorOutput detectorOutput = formattedOutput.detectors.get(0);

        Assertions.assertEquals("FAILURE", detectorOutput.status);
        Assertions.assertEquals(DetectorStatusCode.EXECUTABLE_NOT_FOUND, detectorOutput.statusCode);
        Assertions.assertEquals("No go executable was found.", detectorOutput.statusReason);
    }
}
*/