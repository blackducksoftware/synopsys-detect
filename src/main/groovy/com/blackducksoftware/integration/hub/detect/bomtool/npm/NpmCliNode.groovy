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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.google.gson.annotations.SerializedName

class NpmCliNode implements NameVersionNode {
    String name
    String version

    @SerializedName("dependencies")
    Map<String, NpmCliNode> children

    @Override
    public List<NpmCliNode> getChildren() {
        List<NpmCliNode> npmChildNodes = []

        children?.each { key, value ->
            value.name = key
            npmChildNodes.add(value)
        }

        npmChildNodes
    }

    @Override
    public void setChildren(List<? extends NameVersionNode> children) {
        this.@children.clear()
        children.each {
            this.@children.put(it.name, it)
        }
    }
}