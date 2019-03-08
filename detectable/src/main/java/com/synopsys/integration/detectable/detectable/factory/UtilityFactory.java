/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class UtilityFactory {
    public FileFinder simpleFileFinder() {
        return new SimpleFileFinder();
    }

    public SimpleExecutableFinder simpleExecutableFinder() {
        return SimpleExecutableFinder.forCurrentOperatingSystem(simpleFileFinder());
    }

    public SimpleLocalExecutableFinder simpleLocalExecutableFinder() {
        return new SimpleLocalExecutableFinder(simpleExecutableFinder());
    }

    public SimpleSystemExecutableFinder simpleSystemExecutableFinder() {
        return new SimpleSystemExecutableFinder(simpleExecutableFinder());
    }

    public SimpleExecutableResolver executableResolver() {
        CachedExecutableResolverOptions options = new CachedExecutableResolverOptions(python3);
        options.python3 = false;
        return new SimpleExecutableResolver(options, simpleLocalExecutableFinder(), simpleSystemExecutableFinder());
    }
}
