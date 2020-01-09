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
package com.synopsys.integration.detectable.detectables.pip.parser

import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze
import com.synopsys.integration.detectable.detectables.pip.model.PipFreezeEntry

class PipenvFreezeParser {
    private val versionSeparator = "==".toRegex()

    fun parse(pipFreezeOutput: List<String>): PipFreeze {
        val entries = pipFreezeOutput
                .map { line -> line.split(versionSeparator) }
                .filter { pieces -> pieces.size == 2 }
                .map { pieces -> PipFreezeEntry(pieces[0], pieces[1]) }

        return PipFreeze(entries)
    }
}
