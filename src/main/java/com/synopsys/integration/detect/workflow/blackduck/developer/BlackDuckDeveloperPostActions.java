/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class BlackDuckDeveloperPostActions {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final EventSystem eventSystem;

    public BlackDuckDeveloperPostActions(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void perform(List<DeveloperScanComponentResultView> results) {
        Set<String> violatedPolicyComponentNames = new LinkedHashSet<>();
        for (DeveloperScanComponentResultView resultView : results) {
            String componentName = resultView.getComponentName();
            String componentVersion = resultView.getVersionName();
            Set<String> policyNames = resultView.getViolatingPolicyNames();

            if (!policyNames.isEmpty()) {
                violatedPolicyComponentNames.add(componentName);
                for (String policyName : policyNames) {
                    logger.info("Policy rule \"{}\" was violated by component \"{}\" ({}).",
                        policyName,
                        componentName,
                        componentVersion
                    );
                }
            }
        }

        if (!violatedPolicyComponentNames.isEmpty()) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_POLICY_VIOLATION, createViolationMessage(violatedPolicyComponentNames)));
        }
    }

    private String createViolationMessage(Set<String> violatedPolicyNames) {
        StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append("Black Duck found:");
        stringBuilder.append(fixComponentPlural(" %d %s in violation", violatedPolicyNames.size()));
        return stringBuilder.toString();
    }

    private String fixComponentPlural(String formatString, int count) {
        String label = "components";
        if (count == 1)
            label = "component";
        return String.format(formatString, count, label);
    }
}
