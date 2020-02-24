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
package com.synopsys.integration.detect.tool.detector.impl

import java.nio.file.Path

class DetectExecutableOptions(
        val bashUserPath: Path?,
        val bazelUserPath: Path?,
        val condaUserPath: Path?,
        val cpanUserPath: Path?,
        val cpanmUserPath: Path?,
        val gradleUserPath: Path?,
        val mavenUserPath: Path?,
        val npmUserPath: Path?,
        val pearUserPath: Path?,
        val pipenvUserPath: Path?,
        val pythonUserPath: Path?,
        val rebarUserPath: Path?,
        val javaUserPath: Path?,
        val dockerUserPath: Path?,
        val dotnetUserPath: Path?,
        val gitUserPath: Path?,
        val goUserPath: Path?,
        val swiftUserPath: Path?
)