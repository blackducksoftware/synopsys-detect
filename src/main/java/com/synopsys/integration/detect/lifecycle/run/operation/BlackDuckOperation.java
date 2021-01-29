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
package com.synopsys.integration.detect.lifecycle.run.operation;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public abstract class BlackDuckOperation extends ToolOperation<RunResult, RunResult> {
    private final DetectContext detectContext;
    private final DetectInfo detectInfo;
    private final ProductRunData productRunData;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final DetectToolFilter detectToolFilter;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final RunOptions runOptions;
    private final boolean priorStepsSucceeded;

    public BlackDuckOperation(DetectContext detectContext, DetectInfo detectInfo, ProductRunData productRunData, DirectoryManager directoryManager, EventSystem eventSystem,
        DetectConfigurationFactory detectConfigurationFactory, DetectToolFilter detectToolFilter, CodeLocationNameManager codeLocationNameManager,
        BdioCodeLocationCreator bdioCodeLocationCreator, RunOptions runOptions, boolean priorStepsSucceeded) {
        this.detectContext = detectContext;
        this.detectInfo = detectInfo;
        this.productRunData = productRunData;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
        this.detectConfigurationFactory = detectConfigurationFactory;
        this.detectToolFilter = detectToolFilter;
        this.codeLocationNameManager = codeLocationNameManager;
        this.bdioCodeLocationCreator = bdioCodeLocationCreator;
        this.runOptions = runOptions;
        this.priorStepsSucceeded = priorStepsSucceeded;
    }

    @Override
    public boolean shouldExecute() {
        return productRunData.shouldUseBlackDuckProduct();
    }

    protected AggregateOptions determineAggregationStrategy(RunOptions runOptions, boolean anythingFailed) {
        String aggregateName = runOptions.getAggregateName().orElse(null);
        AggregateMode aggregateMode = runOptions.getAggregateMode();
        if (StringUtils.isNotBlank(aggregateName)) {
            if (anythingFailed) {
                return AggregateOptions.aggregateButSkipEmpty(aggregateName, aggregateMode);
            } else {
                return AggregateOptions.aggregateAndAlwaysUpload(aggregateName, aggregateMode);
            }
        } else {
            return AggregateOptions.doNotAggregate();
        }
    }

    protected NameVersion getProjectInformation(RunResult runResult) {
        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        return projectNameVersionDecider.decideProjectNameVersion(runOptions.getPreferredTools(), runResult.getDetectToolProjectInfo());
    }

    protected BdioResult createBdioFiles(RunResult runResult, AggregateOptions aggregateOptions, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
        BdioManager bdioManager = new BdioManager(detectInfo, new SimpleBdioFactory(), new ExternalIdFactory(), new Bdio2Factory(), new IntegrationEscapeUtil(), codeLocationNameManager, bdioCodeLocationCreator, directoryManager);
        BdioResult bdioResult = bdioManager.createBdioFiles(bdioOptions, aggregateOptions, projectNameVersion, runResult.getDetectCodeLocations(), runOptions.shouldUseBdio2());
        eventSystem.publishEvent(Event.DetectCodeLocationNamesCalculated, bdioResult.getCodeLocationNamesResult());
        return bdioResult;
    }

    public boolean havePriorStepsSucceeded() {
        return priorStepsSucceeded;
    }

    public BlackDuckRunData getBlackDuckRunData() {
        return productRunData.getBlackDuckRunData();
    }

    public DetectContext getDetectContext() {
        return detectContext;
    }

    public DetectInfo getDetectInfo() {
        return detectInfo;
    }

    public ProductRunData getProductRunData() {
        return productRunData;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public DetectConfigurationFactory getDetectConfigurationFactory() {
        return detectConfigurationFactory;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public CodeLocationNameManager getCodeLocationNameManager() {
        return codeLocationNameManager;
    }

    public BdioCodeLocationCreator getBdioCodeLocationCreator() {
        return bdioCodeLocationCreator;
    }

    public RunOptions getRunOptions() {
        return runOptions;
    }
}
