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

import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction.ExtractionResultType;

public class BomToolEvaluation {
    public static final String NO_MESSAGE = "Unknown";

    private final BomTool bomTool;
    private final BomToolEnvironment environment;

    private BomToolResult searchable;
    private BomToolResult applicable;
    private BomToolResult extractable;

    public Extraction extraction;

    public BomToolEvaluation(final BomTool bomTool, final BomToolEnvironment environment) {
        this.bomTool = bomTool;
        this.environment = environment;
    }

    public boolean wasExtractionSuccessful() {
        return isExtractable() && this.extraction != null && this.extraction.result == ExtractionResultType.Success;
    }

    public BomTool getBomTool() {
        return bomTool;
    }

    public BomToolEnvironment getEnvironment() {
        return environment;
    }

    public void setSearchable(final BomToolResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getBomToolResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(final BomToolResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public String getApplicabilityMessage() {
        return getBomToolResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(final BomToolResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public String getExtractabilityMessage() {
        return getBomToolResultDescription(extractable).orElse(NO_MESSAGE);
    }

    private Optional<String> getBomToolResultDescription(final BomToolResult bomToolResult) {
        String description = null;

        if (bomToolResult != null) {
            description = bomToolResult.toDescription();
        }

        return Optional.ofNullable(description);
    }

}
