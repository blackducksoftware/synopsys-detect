/**
 * detectable
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
package com.synopsys.integration.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

public abstract class Detectable {
    protected DetectableEnvironment environment;
    protected List<File> relevantFiles = new ArrayList<>();

    public Detectable(final DetectableEnvironment environment) {
        this.environment = environment;
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
     * Perform project information discovery and try not to throw an exception. Instead return a discovery built with an exception.
     */
    public Discovery discover(final ExtractionEnvironment extractionEnvironment) {
        try {
            final Extraction extraction = extract(extractionEnvironment);
            if (extraction.isSuccess()) {
                return new Discovery.Builder().success(extraction).build();
            } else {
                return new Discovery.Builder().failure("The extraction was not a success.").build();
            }
        } catch (ExecutableFailedException e) {
            return new Discovery.Builder().failure("The extraction was not a success. An executable returned a non-zero exit code.").build();
        }

    }

    /*
     * Perform the extraction and try not to throw an exception. Instead return an extraction built with an exception.
     */
    public abstract Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException;

    public List<File> getFoundRelevantFiles() {
        return relevantFiles;
    }
}
