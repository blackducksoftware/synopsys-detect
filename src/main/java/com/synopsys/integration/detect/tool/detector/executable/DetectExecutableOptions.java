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
package com.synopsys.integration.detect.tool.detector.executable;

import java.nio.file.Path;

public class DetectExecutableOptions {
    private final boolean isPython3;
    private final Path bashUserPath;
    private final Path bazelUserPath;
    private final Path conanUserPath;
    private final Path condaUserPath;
    private final Path cpanUserPath;
    private final Path cpanmUserPath;
    private final Path gradleUserPath;
    private final Path mavenUserPath;
    private final Path npmUserPath;
    private final Path pearUserPath;
    private final Path pipUserPath;
    private final Path pipenvUserPath;
    private final Path pythonUserPath;
    private final Path rebarUserPath;
    private final Path javaUserPath;
    private final Path dockerUserPath;
    private final Path dotnetUserPath;
    private final Path gitUserPath;
    private final Path goUserPath;
    private final Path swiftUserPath;
    private final Path lernaUserPath;

    public DetectExecutableOptions(boolean isPython3, Path bashUserPath, Path bazelUserPath, Path conanUserPath, Path condaUserPath, Path cpanUserPath, Path cpanmUserPath, Path gradleUserPath, Path mavenUserPath,
        Path npmUserPath, Path pearUserPath, Path pipUserPath, Path pipenvUserPath, Path pythonUserPath, Path rebarUserPath, Path javaUserPath, Path dockerUserPath,
        Path dotnetUserPath, Path gitUserPath, Path goUserPath, Path swiftUserPath, Path lernaUserPath) {
        this.isPython3 = isPython3;
        this.bashUserPath = bashUserPath;
        this.bazelUserPath = bazelUserPath;
        this.conanUserPath = conanUserPath;
        this.condaUserPath = condaUserPath;
        this.cpanUserPath = cpanUserPath;
        this.cpanmUserPath = cpanmUserPath;
        this.gradleUserPath = gradleUserPath;
        this.mavenUserPath = mavenUserPath;
        this.npmUserPath = npmUserPath;
        this.pearUserPath = pearUserPath;
        this.pipUserPath = pipUserPath;
        this.pipenvUserPath = pipenvUserPath;
        this.pythonUserPath = pythonUserPath;
        this.rebarUserPath = rebarUserPath;
        this.javaUserPath = javaUserPath;
        this.dockerUserPath = dockerUserPath;
        this.dotnetUserPath = dotnetUserPath;
        this.gitUserPath = gitUserPath;
        this.goUserPath = goUserPath;
        this.swiftUserPath = swiftUserPath;
        this.lernaUserPath = lernaUserPath;
    }

    public Path getBashUserPath() {
        return bashUserPath;
    }

    public Path getBazelUserPath() {
        return bazelUserPath;
    }

    public Path getConanUserPath() {
        return conanUserPath;
    }

    public Path getCondaUserPath() {
        return condaUserPath;
    }

    public Path getCpanUserPath() {
        return cpanUserPath;
    }

    public Path getCpanmUserPath() {
        return cpanmUserPath;
    }

    public Path getGradleUserPath() {
        return gradleUserPath;
    }

    public Path getMavenUserPath() {
        return mavenUserPath;
    }

    public Path getNpmUserPath() {
        return npmUserPath;
    }

    public Path getPearUserPath() {
        return pearUserPath;
    }

    public Path getPipenvUserPath() {
        return pipenvUserPath;
    }

    public Path getPythonUserPath() {
        return pythonUserPath;
    }

    public Path getRebarUserPath() {
        return rebarUserPath;
    }

    public Path getJavaUserPath() {
        return javaUserPath;
    }

    public Path getDockerUserPath() {
        return dockerUserPath;
    }

    public Path getDotnetUserPath() {
        return dotnetUserPath;
    }

    public Path getGitUserPath() {
        return gitUserPath;
    }

    public Path getGoUserPath() {
        return goUserPath;
    }

    public Path getSwiftUserPath() {
        return swiftUserPath;
    }

    public Path getLernaUserPath() {
        return lernaUserPath;
    }

    public boolean isPython3() {
        return isPython3;
    }

    public Path getPipUserPath() {
        return pipUserPath;
    }
}