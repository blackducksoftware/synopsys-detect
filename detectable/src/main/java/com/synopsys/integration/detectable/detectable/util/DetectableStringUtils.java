/*
 * detectable
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
package com.synopsys.integration.detectable.detectable.util;

public class DetectableStringUtils {
    public static String removeEvery(final String line, final String[] targets) {
        int indexToCut = line.length();
        for (final String target : targets) {
            if (line.contains(target)) {
                indexToCut = line.indexOf(target);
            }
        }

        return line.substring(0, indexToCut);
    }

    public static int parseIndentationLevel(final String line, String indentation) {
        String consumableLine = line;
        int level = 0;

        while (consumableLine.startsWith(indentation)) {
            consumableLine = consumableLine.replaceFirst(indentation, "");
            level++;
        }

        return level;
    }
}
