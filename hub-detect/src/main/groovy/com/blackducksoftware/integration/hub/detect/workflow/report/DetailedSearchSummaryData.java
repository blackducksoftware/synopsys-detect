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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

public class DetailedSearchSummaryData {
    private final String directory;
    private final List<DetailedSearchSummaryBomToolData> applicable;
    private final List<DetailedSearchSummaryBomToolData> notApplicable;
    private final List<DetailedSearchSummaryBomToolData> notSearchable;

    public DetailedSearchSummaryData(final String directory, final List<DetailedSearchSummaryBomToolData> applicable, final List<DetailedSearchSummaryBomToolData> notApplicable, final List<DetailedSearchSummaryBomToolData> notSearchable) {
        this.directory = directory;
        this.applicable = applicable;
        this.notApplicable = notApplicable;
        this.notSearchable = notSearchable;
    }

    public List<DetailedSearchSummaryBomToolData> getApplicable() {
        return applicable;
    }

    public List<DetailedSearchSummaryBomToolData> getNotApplicable() {
        return notApplicable;
    }

    public List<DetailedSearchSummaryBomToolData> getNotSearchable() {
        return notSearchable;
    }

    public String getDirectory() {
        return directory;
    }
}
