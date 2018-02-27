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
package com.blackducksoftware.integration.hub.detect.hub;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.Stringable;

public class ScanPathSource extends Stringable {
    public static final ScanPathSource DOCKER_SOURCE = new ScanPathSource(BomToolType.DOCKER.toString());
    public static final ScanPathSource NUGET_SOURCE = new ScanPathSource(BomToolType.NUGET.toString());
    public static final ScanPathSource GRADLE_SOURCE = new ScanPathSource(BomToolType.GRADLE.toString());
    public static final ScanPathSource MAVEN_SOURCE = new ScanPathSource(BomToolType.MAVEN.toString());
    public static final ScanPathSource NPM_SOURCE = new ScanPathSource(BomToolType.NPM.toString());
    public static final ScanPathSource SBT_SOURCE = new ScanPathSource(BomToolType.SBT.toString());
    public static final ScanPathSource DETECT_SOURCE = new ScanPathSource("DETECT");
    public static final ScanPathSource SNIPPET_SOURCE = new ScanPathSource("SNIPPET");

    private final String source;

    public ScanPathSource(final String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }

}
