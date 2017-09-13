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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt.models

import com.blackducksoftware.integration.hub.bdio.simple.DependencyGraph

import groovy.transform.TypeChecked

@TypeChecked
class SbtDependencyModule {
    public String sourcePath
    public String name
    public String version
    public String org
    public DependencyGraph graph

    public String configuration = null //if this is from a specific configuration

    public SbtDependencyModule(){

    }
    public SbtDependencyModule(String name, String version, String org, DependencyGraph graph, String sourcePath) {
        this.sourcePath = sourcePath
        this.graph = graph

        this.org = org
        this.name = name
        this.version = version
    }
}
