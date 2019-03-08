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
package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.file.FileUtils;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;

public class DependencyFileDetailGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FilePathGenerator filePathGenerator;

    public DependencyFileDetailGenerator(final FilePathGenerator filePathGenerator) {this.filePathGenerator = filePathGenerator;}

    public Set<DependencyFileDetails> fromCompileCommands(List<CompileCommand> compileCommands, File sourceDirectory, File outputDirectory, boolean cleanup) {

        final Set<DependencyFileDetails> dependencyFileDetails = compileCommands.parallelStream()
                                                                     .flatMap(command -> filePathGenerator.fromCompileCommand(outputDirectory, command, cleanup).stream())
                                                                     .filter(StringUtils::isNotBlank)
                                                                     .map(File::new)
                                                                     .filter(File::exists)
                                                                     .map(file -> new DependencyFileDetails(FileUtils.isFileChildOfDirectory(file, sourceDirectory), file))
                                                                     .collect(Collectors.toSet());

        logger.trace("Found : " + dependencyFileDetails.size() + " files to process.");

        return dependencyFileDetails;
    }
}
