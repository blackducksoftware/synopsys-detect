/**
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
package com.blackducksoftware.integration.hub.detect.extraction.model;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class Extraction {

    public List<DetectCodeLocation> codeLocations;
    public ExtractionResultType result;
    public Exception error;
    public String description;

    public String projectVersion;
    public String projectName;

    private Extraction(final Builder builder) {
        this.codeLocations = builder.codeLocations;
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
    }

    public static class Builder {
        private final List<DetectCodeLocation> codeLocations = new ArrayList<>();
        private ExtractionResultType result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;

        public Builder projectName(final String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder projectVersion(final String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder codeLocations(final DetectCodeLocation codeLocation) {
            codeLocations.add(codeLocation);
            return this;
        }
        public Builder codeLocations(final List<DetectCodeLocation> codeLocation) {
            codeLocations.addAll(codeLocation);
            return this;
        }
        public Builder success(final DetectCodeLocation codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }
        public Builder success(final List<DetectCodeLocation> codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }
        public Builder success() {
            this.result = ExtractionResultType.Success;
            return this;
        }
        public Builder failure(final String description) {
            this.result = ExtractionResultType.Failure;
            this.description = description;
            return this;
        }
        public Builder exception(final Exception error) {
            this.result = ExtractionResultType.Exception;
            this.error = error;
            return this;
        }
        public Extraction build() {
            return new Extraction(this);
        }

    }

    public enum ExtractionResultType {
        Success,
        Failure,
        Exception
    }

}
