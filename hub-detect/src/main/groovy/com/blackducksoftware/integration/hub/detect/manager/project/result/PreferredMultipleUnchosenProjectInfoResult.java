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
package com.blackducksoftware.integration.hub.detect.manager.project.result;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;

public class PreferredMultipleUnchosenProjectInfoResult extends ProjectInfoResult {
    private final BomToolGroupType bomToolType;

    public PreferredMultipleUnchosenProjectInfoResult(final BomToolGroupType bomToolType) {
        this.bomToolType = bomToolType;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("More than one preferred bom tool of type " + bomToolType.toString() + " was found. Project info could not be found in a bom tool.");
    }

}
