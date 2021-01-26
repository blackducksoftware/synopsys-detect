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

import com.synopsys.integration.detectable.extraction.Extraction;

public class Discovery {
    private final DiscoveryResultType result;
    private final Exception error;
    private final String description;

    private final String projectVersion;
    private final String projectName;
    private final Extraction extraction;

    private Discovery(final Builder builder) {
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
        this.extraction = builder.extraction;

        if (result == null) {
            throw new IllegalArgumentException("A discovery requires a result type.");
        }
    }

    public boolean isSuccess() {
        return this.result == DiscoveryResultType.SUCCESS;
    }

    public Exception getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public DiscoveryResultType getResult() {
        return result;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public static class Builder {
        private DiscoveryResultType result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;
        private Extraction extraction;

        public Builder projectName(final String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder projectVersion(final String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder success() {
            this.result = DiscoveryResultType.SUCCESS;
            return this;
        }

        public Builder success(final String projectName, final String projectVersion) {
            return success().projectName(projectName).projectVersion(projectVersion);
        }

        public Builder skipped() {
            return success();
        }

        public Builder success(final Extraction extraction) {
            this.extraction = extraction;
            this.projectName = extraction.getProjectName();
            this.projectVersion = extraction.getProjectVersion();
            return success();
        }

        public Builder failure(final String description) {
            this.result = DiscoveryResultType.FAILURE;
            this.description = description;
            return this;
        }

        public Builder exception(final Exception error) {
            this.result = DiscoveryResultType.EXCEPTION;
            this.error = error;
            return this;
        }

        public Discovery build() {
            return new Discovery(this);
        }

    }

    public enum DiscoveryResultType {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }
}
