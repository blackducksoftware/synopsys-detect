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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

public class SbtAggregate {
    public String name;
    public String org;
    public String version;

    public SbtAggregate(final String name, final String org, final String version) {
        this.name = name;
        this.org = org;
        this.version = version;
    }

    @Override
    public boolean equals(final Object object) {
        if (object != null && object instanceof SbtAggregate) {
            final SbtAggregate thing = (SbtAggregate) object;
            return thing.name == this.name && thing.org == this.org && thing.version == this.version;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((org == null) ? 0 : org.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }
}
