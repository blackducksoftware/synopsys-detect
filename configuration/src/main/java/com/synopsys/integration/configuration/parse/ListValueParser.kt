/**
 * configuration
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
package com.synopsys.integration.configuration.parse

/**
 * Splits the configuration provided value to a list of T around occurrences of the specified [delimiters].
 *
 * @param valueParser The ValueParser to be applied to each entry in the list.
 * @param delimiters One or more strings to be used as delimiters. Defaults to comma-separated.
 *
 * To avoid ambiguous results when strings in [delimiters] have characters in common, this method proceeds from
 * the beginning to the end of this string, and matches at each position the first element in [delimiters]
 * that is equal to a delimiter in this instance at that position.
 */
open class ListValueParser<T>(private val valueParser: ValueParser<T>, private vararg val delimiters: String = arrayOf(",")) : ValueParser<List<T>>() {
    override fun parse(value: String): List<T> {
        return value.split(*delimiters)
                .map { valueParser.parse(it) }
                .toList()
    }
}