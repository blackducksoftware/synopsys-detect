/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.model.Package;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockParser;
import com.synopsys.integration.util.NameVersion;

public class CargoExtractor {

    private CargoLockParser cargoLockParser;

    public CargoExtractor(CargoLockParser cargoLockParser) {
        this.cargoLockParser = cargoLockParser;
    }

    public Extraction extract(File cargoLock, Optional<File> cargoToml) {
        try {
            String cargoLockAsString = getCargoLockAsString(cargoLock, StandardCharsets.UTF_8);
            DependencyGraph graph = cargoLockParser.parseLockFile(cargoLockAsString);
            CodeLocation codeLocation = new CodeLocation(graph);

            if (cargoToml.isPresent()) {
                CargoToml cargoTomlObject = new Toml().read(cargoToml.get()).to(CargoToml.class);
                if (cargoTomlObject.getPackage().isPresent()) {
                    Package cargoTomlPackageInfo = cargoTomlObject.getPackage().get();
                    if (cargoTomlPackageInfo.getName().isPresent() && cargoTomlPackageInfo.getVersion().isPresent()) {
                        return new Extraction.Builder()
                                   .success(codeLocation)
                                   .projectName(cargoTomlPackageInfo.getName().get())
                                   .projectVersion(cargoTomlPackageInfo.getVersion().get())
                                   .build();
                    }
                }
            }
            return new Extraction.Builder().success(codeLocation).build();
        } catch (IOException | DetectableException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private String getCargoLockAsString(File cargoLock, Charset encoding) throws IOException {
       List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
       return String.join(System.lineSeparator(), goLockAsList);
    }
}
