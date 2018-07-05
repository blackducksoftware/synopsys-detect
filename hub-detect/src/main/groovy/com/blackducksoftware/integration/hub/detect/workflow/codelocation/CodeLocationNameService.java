/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.codelocation;

import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public abstract class CodeLocationNameService {
    private final DetectFileFinder detectFileFinder;

    public CodeLocationNameService(final DetectFileFinder detectFileFinder) {
        this.detectFileFinder = detectFileFinder;
    }

    public DetectFileFinder getDetectFileFinder() {
        return detectFileFinder;
    }

    protected String shortenPiece(final String piece) {
        if (piece.length() <= 40) {
            return piece;
        } else {
            return piece.substring(0, 19) + "..." + piece.substring(piece.length() - 18);
        }
    }
}
