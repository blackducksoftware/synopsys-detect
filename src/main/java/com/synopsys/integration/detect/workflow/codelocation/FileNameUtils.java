/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNameUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileNameUtils.class);

    public static String relativize(final String from, final String to) {
        String relative = to;
        try {
            final Path toPath = new File(to).toPath();
            final Path fromPath = new File(from).toPath();
            final Path relativePath = fromPath.relativize(toPath);
            final List<String> relativePieces = new ArrayList<>();
            for (int i = 0; i < relativePath.getNameCount(); i++) {
                relativePieces.add(relativePath.getName(i).toFile().getName());
            }
            relative = StringUtils.join(relativePieces, "/");
        } catch (final Exception e) {
            logger.info(String.format("Unable to relativize path, full source path will be used: %s", to));
            logger.debug("The reason relativize failed: ", e);
        }

        return relative;
    }

    public static String relativizeParent(final String from, final String to) {
        String relative = to;
        try {
            final Path toPath = new File(to).toPath();
            final Path fromPath = new File(from).toPath();
            final Path relativePath = fromPath.getParent().relativize(toPath);
            final List<String> relativePieces = new ArrayList<>();
            for (int i = 0; i < relativePath.getNameCount(); i++) {
                relativePieces.add(relativePath.getName(i).toFile().getName());
            }
            relative = StringUtils.join(relativePieces, "/");
        } catch (final Exception e) {
            logger.info(String.format("Unable to relativize path, full source path will be used: %s", to));
            logger.debug("The reason relativize failed: ", e);
        }

        return relative;
    }

}
