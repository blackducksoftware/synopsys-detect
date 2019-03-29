/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.help;

public class DetectArgumentState {
    private final boolean isHelp;
    private final boolean isHelpHtmlDocument;
    private final boolean isHelpJsonDocument;
    private final boolean isInteractive;

    private final boolean isVerboseHelp;
    private final boolean isDeprecatedHelp;
    private final String parsedValue;

    private final boolean isDiagnostic;
    private final boolean isDiagnosticExtended;

    public DetectArgumentState(final boolean isHelp, final boolean isHelpHtmlDocument, final boolean isHelpJsonDocument, final boolean isInteractive, final boolean isVerboseHelp, final boolean isDeprecatedHelp, final String parsedValue,
        final boolean isDiagnostic,
        final boolean isDiagnosticExtended) {
        this.isHelp = isHelp;
        this.isHelpHtmlDocument = isHelpHtmlDocument;
        this.isHelpJsonDocument = isHelpJsonDocument;
        this.isInteractive = isInteractive;
        this.isVerboseHelp = isVerboseHelp;
        this.isDeprecatedHelp = isDeprecatedHelp;
        this.parsedValue = parsedValue;
        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticExtended = isDiagnosticExtended;
    }

    public boolean isHelp() {
        return isHelp;
    }

    public boolean isHelpHtmlDocument() {
        return isHelpHtmlDocument;
    }

    public boolean isHelpJsonDocument() {
        return isHelpJsonDocument;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public boolean isVerboseHelp() {
        return isVerboseHelp;
    }

    public boolean isDeprecatedHelp() {
        return isDeprecatedHelp;
    }

    public boolean isDiagnostic() {
        return isDiagnostic;
    }

    public boolean isDiagnosticExtended() {
        return isDiagnosticExtended;
    }

    public String getParsedValue() {
        return parsedValue;
    }

}
