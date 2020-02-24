/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;

public class ResourceTypingExecutableCreator extends TypingExecutableCreator {
    private final List<String> toType;

    protected ResourceTypingExecutableCreator(final List<String> toType) {
        this.toType = toType;
    }

    @Override
    public List<String> getFilePaths(final File mockDirectory, final AtomicInteger commandCount) throws IOException {
        final List<String> filePaths = new ArrayList<>();
        for (final String resource : toType) {
            final InputStream commandText = BatteryFiles.asInputStream(resource);
            Assertions.assertNotNull(commandText, "Unable to find resource: " + resource);
            final File commandTextFile = new File(mockDirectory, "cmd-" + commandCount.getAndIncrement() + ".txt");
            Files.copy(commandText, commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            filePaths.add(commandTextFile.getCanonicalPath());
        }
        return filePaths;
    }
}
