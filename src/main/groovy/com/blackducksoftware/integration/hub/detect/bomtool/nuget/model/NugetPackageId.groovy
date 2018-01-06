/*
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.model

import org.apache.commons.lang3.builder.RecursiveToStringStyle
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import com.google.gson.annotations.SerializedName

import groovy.transform.TypeChecked

@TypeChecked
class NugetPackageId {
    @SerializedName('Name')
    String name

    @SerializedName('Version')
    String version

    @Override
    public int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode())
        result = prime * result + ((version == null) ? 0 : version.toLowerCase().hashCode())
        return result
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false
        }
        if (!(obj instanceof NugetPackageId)) {
            return false
        }
        NugetPackageId other = (NugetPackageId) obj
        if (name == null) {
            if (other.name != null) {
                return false
            }
        } else if (!name.equalsIgnoreCase(other.name)) {
            return false
        }
        if (version == null) {
            if (other.version != null) {
                return false
            }
        } else if (!version.equalsIgnoreCase(other.version)) {
            return false
        }
        return true
    }


    @Override
    String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE)
    }
}
