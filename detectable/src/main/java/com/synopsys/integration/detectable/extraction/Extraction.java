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
package com.synopsys.integration.detectable.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.util.NameVersion;

public class Extraction {
    private final List<CodeLocation> codeLocations;
    private final List<File> relevantFiles;
    private final List<File> unrecognizedPaths;
    private final ExtractionResultType result;

    //if your an error you might have one of these filled.
    private final Exception error;
    //end

    private final String description;
    private final String projectVersion;
    private final String projectName;
    private final Map<ExtractionMetadata, Object> metaData;

    private Extraction(final Builder builder) {
        this.codeLocations = builder.codeLocations;
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
        this.metaData = builder.metaData;
        this.relevantFiles = builder.relevantFiles;
        this.unrecognizedPaths = builder.unrecognizedPaths;

        if (result == null) {
            throw new IllegalArgumentException("An extraction requires a result type.");
        }
    }

    public static Extraction fromFailedExecutable(ExecutableFailedException executableRunnerException) {
        return new Extraction.Builder().exception(executableRunnerException).build();
    }

    public <T> Optional<T> getMetaData(final ExtractionMetadata<T> extractionMetadata) {
        if (metaData.containsKey(extractionMetadata)) {
            final Class<T> clazz = extractionMetadata.getMetadataClass();
            final Object value = metaData.get(extractionMetadata);
            if (value != null && clazz.isAssignableFrom(value.getClass())) {
                return Optional.of(clazz.cast(value));
            }
        }
        return Optional.empty();
    }

    public boolean isSuccess() {
        return this.result == ExtractionResultType.SUCCESS;
    }

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
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

    public ExtractionResultType getResult() {
        return result;
    }

    public List<File> getRelevantFiles() {
        return relevantFiles;
    }

    public List<File> getUnrecognizedPaths() {
        return unrecognizedPaths;
    }

    public static class Builder {
        private final List<CodeLocation> codeLocations = new ArrayList<>();
        private final List<File> relevantFiles = new ArrayList<>();
        private final List<File> unrecognizedPaths = new ArrayList<>();
        private ExtractionResultType result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;
        private final Map<ExtractionMetadata, Object> metaData = new HashMap<>();

        public Builder projectName(final String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder projectVersion(final String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder nameVersionIfPresent(final Optional<NameVersion> nameVersion) {
            if (nameVersion.isPresent()) {
                this.projectName(nameVersion.get().getName());
                this.projectVersion(nameVersion.get().getVersion());
            }
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

        public <T> Builder metaData(final ExtractionMetadata<T> key, final T value) {
            this.metaData.put(key, value);
            return this;
        }

        public Builder relevantFiles(final File... files) {
            this.relevantFiles.addAll(Arrays.asList(files));
            return this;
        }

        public Builder unrecognizedPaths(final File... files) {
            this.unrecognizedPaths.addAll(Arrays.asList(files));
            return this;
        }

        public Builder unrecognizedPaths(final Collection<File> files) {
            this.unrecognizedPaths.addAll(files);
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
