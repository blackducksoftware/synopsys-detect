/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

import groovy.transform.TypeChecked;

@TypeChecked
public abstract class BomTool<T extends BomToolApplicableResult> {
    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Autowired
    protected ExecutableManager executableManager;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Autowired
    protected DetectFileManager detectFileManager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    protected BomToolExtractionResultsFactory bomToolExtractionResultsFactory;

    public abstract BomToolType getBomToolType();

    public abstract T isBomToolApplicable(File directory);

    public abstract BomToolExtractionResult extractDetectCodeLocations(T applicable);

    public BomToolSearchOptions getSearchOptions() {
        return BomToolSearchOptions.defaultOptions();
    }

}
