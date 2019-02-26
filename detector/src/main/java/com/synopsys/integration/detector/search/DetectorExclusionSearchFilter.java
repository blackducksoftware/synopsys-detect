/**
 * detector
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
package com.synopsys.integration.detector.search;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectorExclusionSearchFilter implements DetectorSearchFilter {
    private List<String> excludedDirectories;
    private WildcardFileFilter fileFilter;

    public DetectorExclusionSearchFilter(List<String> excludedDirectories, List<String> excludedDirectoryNamePatterns){
        this.excludedDirectories = excludedDirectories;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean shouldExclude(File file) {
        for (final String excludedDirectory : excludedDirectories) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }
}
