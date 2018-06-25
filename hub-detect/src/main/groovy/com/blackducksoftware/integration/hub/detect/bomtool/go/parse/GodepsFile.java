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
package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GodepsFile {
    @SerializedName("ImportPath")
    private String importPath;

    @SerializedName("GoVersion")
    private String goVersion;

    @SerializedName("GodepVersion")
    private String godepVersion;

    @SerializedName("Packages")
    private List<String> packages;

    @SerializedName("Deps")
    private List<GodepDependency> deps;

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(final String importPath) {
        this.importPath = importPath;
    }

    public String getGoVersion() {
        return goVersion;
    }

    public void setGoVersion(final String goVersion) {
        this.goVersion = goVersion;
    }

    public String getGodepVersion() {
        return godepVersion;
    }

    public void setGodepVersion(final String godepVersion) {
        this.godepVersion = godepVersion;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(final List<String> packages) {
        this.packages = packages;
    }

    public List<GodepDependency> getDeps() {
        return deps;
    }

    public void setDeps(final List<GodepDependency> deps) {
        this.deps = deps;
    }

}
