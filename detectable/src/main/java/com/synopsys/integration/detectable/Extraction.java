/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class Extraction {
    public List<CodeLocation> codeLocations;
    public ExtractionResultType result;
    public Exception error;
    public String description;

    public String projectVersion;
    public String projectName;
    public Map<String, Object> metaData; //TODO: Typesafe way to provide meta data?

    private Extraction(final Builder builder) {
        this.codeLocations = builder.codeLocations;
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
        this.metaData = builder.metaData;
    }

    public Optional<Object> getMetaDataValue(String key) {
        if (metaData.containsKey(key)) {
            return Optional.ofNullable(metaData.get(key));
        } else {
            return Optional.empty();
        }
    }

    public boolean isSuccess() {
        return this.result == ExtractionResultType.SUCCESS;
    }

    public static class Builder {
        private final List<CodeLocation> codeLocations = new ArrayList<>();
        private ExtractionResultType result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;
        private Map<String, Object> metaData = new HashMap<>();

        public Builder projectName(final String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder projectVersion(final String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder codeLocations(final CodeLocation codeLocation) {
            codeLocations.add(codeLocation);
            return this;
        }

        public Builder codeLocations(final List<CodeLocation> codeLocation) {
            codeLocations.addAll(codeLocation);
            return this;
        }

        public Builder success(final CodeLocation codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }

        public Builder success(final List<CodeLocation> codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }

        public Builder success() {
            this.result = ExtractionResultType.SUCCESS;
            return this;
        }

        public Builder failure(final String description) {
            this.result = ExtractionResultType.FAILURE;
            this.description = description;
            return this;
        }

        public Builder exception(final Exception error) {
            this.result = ExtractionResultType.EXCEPTION;
            this.error = error;
            return this;
        }

        public Builder metaData(String key, Object value) {
            this.metaData.put(key, value);
            return this;
        }

        public Extraction build() {
            return new Extraction(this);
        }

    }

    public enum ExtractionResultType {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }
}
