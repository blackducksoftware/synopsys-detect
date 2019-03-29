/**
 * detectable
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
package com.synopsys.integration.detectable;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public abstract class Detectable {
    protected DetectableEnvironment environment;
    private final String name;
    private final String group;

    public Detectable(final DetectableEnvironment environment, final String name, final String group) {
        this.environment = environment;
        this.name = name;
        this.group = group;
    }

    /*
     * Applicable should be light-weight and should never throw an exception. Look for files, check properties, short and sweet.
     */
    public abstract DetectableResult applicable();

    /*
     * Extractable may be as heavy as needed, and may (and sometimes should) fail. Make web requests, install inspectors or run executables.
     */
    public abstract DetectableResult extractable() throws DetectableException;

    /*
     * Perform the extraction and try not to throw an exception. Instead return an extraction built with an exception.
     */
    public abstract Extraction extract(ExtractionEnvironment extractionEnvironment);

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return group;
    }

    public String getDescriptiveName() {
        return String.format("%s - %s", getGroupName().toString(), getName());
    }

    public boolean isSame(final Detectable detector) {
        return this.getClass().equals(detector.getClass());
    }

}
