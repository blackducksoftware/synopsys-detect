/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.impl.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.event.EventType;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorToolTest {

    @Test
    public void testFailWhenMisConfigured() throws DetectUserFriendlyException {

        final ExtractionEnvironmentProvider extractionEnvironmentProvider = Mockito.mock(ExtractionEnvironmentProvider.class);
        final DetectorFinder detectorFinder = Mockito.mock(DetectorFinder.class);
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);
        final CodeLocationConverter codeLocationConverter = Mockito.mock(CodeLocationConverter.class);

        final DetectorTool tool = new DetectorTool(detectorFinder, extractionEnvironmentProvider, eventSystem, codeLocationConverter);

        final File directory = new File(".");
        final DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        final DetectorFinderOptions detectorFinderOptions = Mockito.mock(DetectorFinderOptions.class);
        final DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        final String projectBomTool = "testBomTool";

        tool.performDetectors(directory, detectorRuleSet, detectorFinderOptions, evaluationOptions, projectBomTool, new ArrayList<>());

        Mockito.verify(eventSystem).publishEvent(Mockito.any(EventType.class), Mockito.argThat(new FailureExitCodeRequestMatcher()));
    }

    private class FailureExitCodeRequestMatcher implements ArgumentMatcher<ExitCodeRequest> {

        @Override
        public boolean matches(ExitCodeRequest actualExitCodeRequest) {
            System.out.printf("custom matcher called: %d: %s\n", actualExitCodeRequest.getExitCodeType().getExitCode(), actualExitCodeRequest.getReason());
            if ((actualExitCodeRequest.getExitCodeType() == ExitCodeType.FAILURE_CONFIGURATION) &&
                    (StringUtils.isNotBlank(actualExitCodeRequest.getReason()))) {
                return true;
            }
            return false;
        }
    }
}
