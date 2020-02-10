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

import com.synopsys.integration.detectable.detectable.file.FileFinder
import org.apache.commons.io.filefilter.WildcardFileFilter
import java.io.File
import java.io.FileFilter
import java.util.*

class FunctionalFileFinder : FileFinder {
    var files: MutableMap<Int, MutableList<File>> = mutableMapOf()

    fun addFile(file: File, depth: Int) {
        if (!files.containsKey(depth)) {
            files[depth] = mutableListOf()
        }
        files[depth]!!.add(file)
    }

    override fun findFiles(directoryToSearch: File, filenamePatterns: List<String>, depth: Int, findInsideMatchingDirectories: Boolean): List<File> {
        val found: MutableList<File> = ArrayList()
        for (i in 0..depth) {
            if (files.containsKey(i)) {
                val possibles = files[i]!!
                for (pattern in filenamePatterns) {
                    val fileFilter: FileFilter = WildcardFileFilter(pattern)
                    for (possible in possibles) {
                        if (fileFilter.accept(possible)) {
                            found.add(possible)
                        }
                    }
                }
            }
        }
        return found
    }
}