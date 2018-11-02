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
package com.blackducksoftware.integration.hub.detect.lifecycle.run;

public class RunOptions {
    private final boolean bomToolsEnabled;
    private final boolean sigScanEnabled;
    private final boolean binScanEnabled;
    private final boolean isOnline;
    private final boolean unmapCodeLocations;
    private final boolean swipEnabled;
    private final String aggregateName;
    private final String preferredTools;

    public RunOptions(final boolean bomToolsEnabled, final boolean sigScanEnabled, final boolean binScanEnabled, final boolean isOnline, final boolean unmapCodeLocations, final boolean swipEnabled, final String aggregateName,
        final String preferredTools) {
        this.bomToolsEnabled = bomToolsEnabled;
        this.sigScanEnabled = sigScanEnabled;
        this.binScanEnabled = binScanEnabled;
        this.isOnline = isOnline;
        this.unmapCodeLocations = unmapCodeLocations;
        this.swipEnabled = swipEnabled;
        this.aggregateName = aggregateName;
        this.preferredTools = preferredTools;
    }

    public boolean isBomToolsEnabled() {
        return bomToolsEnabled;
    }

    public boolean isSigScanEnabled() {
        return sigScanEnabled;
    }

    public boolean isBinScanEnabled() {
        return binScanEnabled;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isUnmapCodeLocations() {
        return unmapCodeLocations;
    }

    public boolean isSwipEnabled() {
        return swipEnabled;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public String getPreferredTools() {
        return preferredTools;
    }
}
