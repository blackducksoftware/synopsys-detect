package com.synopsys.integration.detect.workflow.report;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    public <T extends Detectable> void detectorOutputStatusDataTest() {
        EventSystem eventSystem = new EventSystem();
        FormattedOutputManager formattedOutputManager = new FormattedOutputManager(eventSystem);

        DetectorRule rule = Mockito.mock(DetectorRule.class);
        Mockito.when(rule.getDescriptiveName()).thenReturn("");
        Mockito.when(rule.getName()).thenReturn("");
        Mockito.when(rule.getDetectorType()).thenReturn(DetectorType.GO_MOD);

        DetectorEvaluation detectorEvaluation = new DetectorEvaluation(rule);
        ExecutableNotFoundDetectableResult result = new ExecutableNotFoundDetectableResult("go");
        final DetectorResult extractableResult = new DetectorResult(result.getPassed(), result.toDescription(), result.getClass(), explanations);
        detectorEvaluation.setExtractable(extractableResult);
        detectorEvaluation.setApplicable(new DetectorResult(true, "", null));
        detectorEvaluation.setSearchable(new DetectorResult(true, "", null));
        detectorEvaluation.setDetectableEnvironment(new DetectableEnvironment(new File("")));

        final DetectorToolResult detectorToolResult = new DetectorToolResult(
            null,
            null,
            null,
            new HashSet<>(),
            new DetectorEvaluationTree(null, 0, null, Collections.singletonList(detectorEvaluation), new HashSet<>()),
            null
        );
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);

        DetectInfo detectInfo = new DetectInfo("", 0, null);
        FormattedOutput formattedOutput = formattedOutputManager.createFormattedOutput(detectInfo);
        FormattedDetectorOutput detectorOutput = formattedOutput.detectors.get(0);

        Assertions.assertEquals("FAILURE", detectorOutput.status);
        Assertions.assertEquals(DetectorStatusCode.EXECUTABLE_NOT_FOUND, detectorOutput.statusCode);
        Assertions.assertEquals("No go executable was found.", detectorOutput.statusReason);
    }
}
