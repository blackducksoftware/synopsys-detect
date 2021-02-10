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
package com.synopsys.integration.detectable.detectable.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;


public class TomlFileUtils {
    public static TomlParseResult parseFile(File tomlFile) throws IOException {
        return Toml.parse(getFileAsString(tomlFile, Charset.defaultCharset()));
    }

    private static String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
    }
}
