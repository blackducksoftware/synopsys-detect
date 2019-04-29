/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.blackduck.BlackduckPostActions;
import com.synopsys.integration.detect.workflow.blackduck.BlackduckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DetectPostActions {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ProjectVersionWrapper projectVersionWrapper;
    private BlackDuckRunData blackDuckRunData;
    private BlackduckPostActions blackduckPostActions;
    private BlackduckPostOptions blackduckPostOptions;
    private CodeLocationWaitData codeLocationWaitData;
    private long timeoutInSeconds;
    private boolean hasAtLeastOneBdio;
    private boolean shouldHaveScanned;

    public void runPostActions() throws DetectUserFriendlyException {
        runPostBlackduckActions();
    }

    private void runPostBlackduckActions() throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (null == projectVersionWrapper || null == blackDuckRunData || null == blackduckPostActions || null == blackduckPostOptions || null == codeLocationWaitData || 0 >= timeoutInSeconds) {
            logger.debug("Will not perform Black Duck post actions: Detect is not online.");
            return;
        }

        if (blackDuckRunData.isOnline() && blackDuckRunData.getBlackDuckServicesFactory().isPresent()) {
            logger.info("Will perform Black Duck post actions.");
            blackduckPostActions.perform(blackduckPostOptions, codeLocationWaitData, projectVersionWrapper, timeoutInSeconds);

            if (hasAtLeastOneBdio || shouldHaveScanned) {
                final Optional<String> componentsLink = projectVersionWrapper.getProjectVersionView().getFirstLink(ProjectVersionView.COMPONENTS_LINK);
                if (componentsLink.isPresent()) {
                    logger.info(String.format("To see your results, follow the URL: %s", componentsLink.get()));
                }
            }
            logger.info("Black Duck actions have finished.");
        }
    }

    public ProjectVersionWrapper getProjectVersionWrapper() {
        return projectVersionWrapper;
    }

    public void setProjectVersionWrapper(ProjectVersionWrapper projectVersionWrapper) {
        this.projectVersionWrapper = projectVersionWrapper;
    }

    public BlackDuckRunData getBlackDuckRunData() {
        return blackDuckRunData;
    }

    public void setBlackDuckRunData(BlackDuckRunData blackDuckRunData) {
        this.blackDuckRunData = blackDuckRunData;
    }

    public BlackduckPostActions getBlackduckPostActions() {
        return blackduckPostActions;
    }

    public void setBlackduckPostActions(BlackduckPostActions blackduckPostActions) {
        this.blackduckPostActions = blackduckPostActions;
    }

    public BlackduckPostOptions getBlackduckPostOptions() {
        return blackduckPostOptions;
    }

    public void setBlackduckPostOptions(BlackduckPostOptions blackduckPostOptions) {
        this.blackduckPostOptions = blackduckPostOptions;
    }

    public CodeLocationWaitData getCodeLocationWaitData() {
        return codeLocationWaitData;
    }

    public void setCodeLocationWaitData(CodeLocationWaitData codeLocationWaitData) {
        this.codeLocationWaitData = codeLocationWaitData;
    }

    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public boolean isHasAtLeastOneBdio() {
        return hasAtLeastOneBdio;
    }

    public void setHasAtLeastOneBdio(boolean hasAtLeastOneBdio) {
        this.hasAtLeastOneBdio = hasAtLeastOneBdio;
    }

    public boolean isShouldHaveScanned() {
        return shouldHaveScanned;
    }

    public void setShouldHaveScanned(boolean shouldHaveScanned) {
        this.shouldHaveScanned = shouldHaveScanned;
    }

}
