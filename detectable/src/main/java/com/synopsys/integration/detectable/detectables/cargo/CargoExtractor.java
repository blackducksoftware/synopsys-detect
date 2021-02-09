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
package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.util.TomlFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockParser;
import com.synopsys.integration.util.NameVersion;

public class CargoExtractor {

    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";
    private static final String PACKAGE_KEY = "package";

    private final CargoLockParser cargoLockParser;

    public CargoExtractor(CargoLockParser cargoLockParser) {
        this.cargoLockParser = cargoLockParser;
    }

    public Extraction extract(File cargoLock, @Nullable File cargoToml) {
        try {
            String cargoLockAsString = getFileAsString(cargoLock, StandardCharsets.UTF_8);
            DependencyGraph graph = cargoLockParser.parseLockFile(cargoLockAsString);
            CodeLocation codeLocation = new CodeLocation(graph);

            Optional<NameVersion> cargoNameVersion = extractNameVersionFromCargoToml(cargoToml);
            if (cargoNameVersion.isPresent()) {
                return new Extraction.Builder()
                           .success(codeLocation)
                           .projectName(cargoNameVersion.get().getName())
                           .projectVersion(cargoNameVersion.get().getVersion())
                           .build();
            }
            return new Extraction.Builder().success(codeLocation).build();
        } catch (IOException | DetectableException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
    }

    private Optional<NameVersion> extractNameVersionFromCargoToml(@Nullable File cargoToml) throws IOException {
        if (cargoToml != null) {
            TomlParseResult cargoTomlObject = TomlFileParser.parse(cargoToml);
            if (cargoTomlObject.get(PACKAGE_KEY) != null) {
                TomlTable cargoTomlPackageInfo = cargoTomlObject.getTable(PACKAGE_KEY);
                if (cargoTomlPackageInfo.get(NAME_KEY) != null && cargoTomlPackageInfo.get(VERSION_KEY) != null) {
                    return Optional.of(new NameVersion(cargoTomlPackageInfo.getString(NAME_KEY), cargoTomlPackageInfo.getString(VERSION_KEY)));
                }
            }
        }
        return Optional.empty();
    }
}
