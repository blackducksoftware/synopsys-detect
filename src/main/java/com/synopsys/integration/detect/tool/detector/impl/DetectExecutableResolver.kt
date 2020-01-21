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

import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.detectable.exception.DetectableException
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver
import com.synopsys.integration.detectable.detectable.executable.resolver.*
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.file.Path
import java.util.function.Function
import java.util.function.Supplier

class DetectExecutableResolver(private val simpleExecutableResolver: SimpleExecutableResolver, private val detectExecutableOptions: DetectExecutableOptions) : JavaResolver, GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver, BazelResolver, DockerResolver, DotNetResolver, GitResolver, SwiftResolver, GoResolver {
    private val cachedExecutables: MutableMap<String, File> = mutableMapOf()

    @Throws(DetectableException::class)
    private fun resolveExecutable(cacheKey: String?, resolveExecutable: Supplier<File>, executableOverride: Path?): File {
        if (executableOverride != null) {
            val exe = executableOverride.toFile()
            return if (!exe.exists()) {
                throw DetectableException("Executable override must exist: $executableOverride")
            } else if (!exe.isFile) {
                throw DetectableException("Executable override must be a file: $executableOverride")
            } else if (!exe.canExecute()) {
                throw DetectableException("Executable override must be executable: $executableOverride")
            } else {
                exe
            }
        }
        val hasCacheKey = StringUtils.isNotBlank(cacheKey)
        if (hasCacheKey && cachedExecutables.containsKey(cacheKey)) {
            var cached = cachedExecutables.get(cacheKey);
            if (cached != null) return cached;
        }
        val resolved = resolveExecutable.get()
        if (hasCacheKey && cacheKey != null) {
            cachedExecutables[cacheKey] = resolved
        }
        return resolved
    }

    @Throws(DetectableException::class)
    private fun resolveExecutableLocally(resolveExecutable: Function<DetectableEnvironment, File>, environment: DetectableEnvironment, executableOverride: Path?): File {
        return resolveExecutable(null, Supplier { resolveExecutable.apply(environment) }, executableOverride)
    }

    @Throws(DetectableException::class)
    override fun resolveBash(): File {
        return resolveExecutable("bash", Supplier { simpleExecutableResolver.resolveBash() }, detectExecutableOptions.bashUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveBazel(): File {
        return resolveExecutable("bazel", Supplier { simpleExecutableResolver.resolveBazel() }, detectExecutableOptions.bazelUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveConda(): File {
        return resolveExecutable("conda", Supplier { simpleExecutableResolver.resolveConda() }, detectExecutableOptions.condaUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveCpan(): File {
        return resolveExecutable("cpan", Supplier { simpleExecutableResolver.resolveCpan() }, detectExecutableOptions.cpanUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveCpanm(): File {
        return resolveExecutable("cpanm", Supplier { simpleExecutableResolver.resolveCpanm() }, detectExecutableOptions.cpanmUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveGradle(environment: DetectableEnvironment): File {
        return resolveExecutableLocally(Function { simpleExecutableResolver.resolveGradle(it) }, environment, detectExecutableOptions.gradleUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveMaven(environment: DetectableEnvironment): File {
        return resolveExecutableLocally(Function { simpleExecutableResolver.resolveMaven(it) }, environment, detectExecutableOptions.mavenUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveNpm(environment: DetectableEnvironment): File {
        return resolveExecutableLocally(Function { simpleExecutableResolver.resolveNpm(it) }, environment, detectExecutableOptions.npmUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolvePear(): File {
        return resolveExecutable("pear", Supplier { simpleExecutableResolver.resolvePear() }, detectExecutableOptions.pearUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolvePip(): File {
        return resolveExecutable("pip", Supplier { simpleExecutableResolver.resolvePip() }, null)
    }

    @Throws(DetectableException::class)
    override fun resolvePipenv(): File {
        return resolveExecutable("pipenv", Supplier { simpleExecutableResolver.resolvePipenv() }, detectExecutableOptions.pipenvUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolvePython(): File {
        return resolveExecutable("python", Supplier { simpleExecutableResolver.resolvePython() }, detectExecutableOptions.pythonUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveRebar3(): File {
        return resolveExecutable("rebar3", Supplier { simpleExecutableResolver.resolveRebar3() }, detectExecutableOptions.rebarUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveJava(): File {
        return resolveExecutable("java", Supplier { simpleExecutableResolver.resolveJava() }, detectExecutableOptions.javaUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveDocker(): File {
        return resolveExecutable("docker", Supplier { simpleExecutableResolver.resolveDocker() }, detectExecutableOptions.dockerUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveDotNet(): File {
        return resolveExecutable("dotnet", Supplier { simpleExecutableResolver.resolveDotNet() }, detectExecutableOptions.dotnetUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveGit(): File {
        return resolveExecutable("git", Supplier { simpleExecutableResolver.resolveGit() }, detectExecutableOptions.gitUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveSwift(): File {
        return resolveExecutable("swift", Supplier { simpleExecutableResolver.resolveSwift() }, detectExecutableOptions.swiftUserPath)
    }

    @Throws(DetectableException::class)
    override fun resolveGo(): File {
        return resolveExecutable("go", Supplier { simpleExecutableResolver.resolveGo() }, detectExecutableOptions.goUserPath)
    }
}
