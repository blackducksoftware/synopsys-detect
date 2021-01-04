/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.git.parsing.model;

import org.jetbrains.annotations.NotNull;

public class GitConfigRemote {
    @NotNull
    private final String name;
    @NotNull
    private final String url;
    @NotNull
    private final String fetch;

    public GitConfigRemote(@NotNull final String name, @NotNull final String url, @NotNull final String fetch) {
        this.name = name;
        this.url = url;
        this.fetch = fetch;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getFetch() {
        return fetch;
    }
}
