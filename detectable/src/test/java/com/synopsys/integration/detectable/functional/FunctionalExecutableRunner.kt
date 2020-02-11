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
package com.synopsys.integration.detectable.functional

import com.synopsys.integration.detectable.detectable.executable.Executable
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner
import java.io.File

class FunctionalExecutableRunner : ExecutableRunner {
    private val executableOutputMap: MutableMap<Executable, ExecutableOutput> = mutableMapOf()

    fun addExecutableOutput(executableOutput: ExecutableOutput, executable: Executable) {
        executableOutputMap[executable] = executableOutput
    }

    override fun execute(workingDirectory: File, exeCmd: String, vararg args: String): ExecutableOutput? {
        return execute(workingDirectory, exeCmd, args.toMutableList())
    }

    override fun execute(workingDirectory: File, exeCmd: String, args: MutableList<String>): ExecutableOutput? {
        return execute(workingDirectory, File(exeCmd), args.toMutableList())
    }

    override fun execute(workingDirectory: File, exeFile: File, vararg args: String): ExecutableOutput? {
        return execute(workingDirectory, exeFile, args.toMutableList())
    }

    override fun execute(workingDirectory: File, exeFile: File, args: MutableList<String>): ExecutableOutput? {
        return execute(Executable(workingDirectory, emptyMap(), exeFile.toString(), args))
    }

    override fun execute(executable: Executable): ExecutableOutput? {
        return executableOutputMap[executable]
    }
}
