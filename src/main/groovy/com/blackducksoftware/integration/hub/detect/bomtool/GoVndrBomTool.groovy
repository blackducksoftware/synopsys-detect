/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.go.vndr.VndrParser
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class GoVndrBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoVndrBomTool.class)

    public static final String VNDR_CONF_FILENAME= 'vendor.conf'

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_VNDR
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean foundVndrConf = detectFileManager.containsAllFiles(sourcePath, VNDR_CONF_FILENAME)
        logger.debug("Found a $VNDR_CONF_FILENAME : $foundVndrConf")
        foundVndrConf
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File sourceDirectory = detectConfiguration.sourceDirectory

        VndrParser vndrParser = new VndrParser()
        def vendorConf = new File(sourcePath, VNDR_CONF_FILENAME)
        List<DependencyNode> dependencies = vndrParser.parseVendorConf(vendorConf.text)
        Set<DependencyNode> dependenciesSet = new HashSet<>(dependencies)
        ExternalId externalId = new PathExternalId(GoDepBomTool.GOLANG, sourcePath)

        def codeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, '', '', '', externalId, dependenciesSet)
        [codeLocation]
    }
}
