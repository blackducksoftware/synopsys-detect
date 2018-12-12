/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.help;

public class DetectArgumentStateParser {

    public DetectArgumentState parseArgs(final String[] args) {
        return parseArgs(new ArgumentParser(args));
    }

    public DetectArgumentState parseArgs(final ArgumentParser parser) {
        final boolean isHelp = parser.isArgumentPresent("-h", "--help");
        final boolean isHelpHtmlDocument = parser.isArgumentPresent("-hdoc", "--helpdocument");
        final boolean isHelpJsonDocument = parser.isArgumentPresent("-hjson", "--helpjson");
        final boolean isInteractive = parser.isArgumentPresent("-i", "--interactive");

        final boolean isVerboseHelp = parser.isArgumentPresent("-hv", "--helpVerbose");
        final boolean isDeprecatedHelp = parser.isArgumentPresent("-hd", "--helpDeprecated");

        final boolean isDiagnosticProvided = parser.isArgumentPresent("-d", "--diagnostic");
        final boolean isDiagnosticExtendedProvided = parser.isArgumentPresent("-de", "--diagnosticExtended");

        boolean isDiagnostic = false;
        boolean isDiagnosticProtected = true;

        if (isDiagnosticProvided || isDiagnosticExtendedProvided) {
            isDiagnostic = true;
        }
        if (isDiagnosticExtendedProvided) {
            isDiagnosticProtected = false;
        }

        String parsedValue = null;
        if (isHelp) {
            parsedValue = parser.findValueForCommand("-h", "--help");
        }

        return new DetectArgumentState(isHelp, isHelpHtmlDocument, isHelpJsonDocument, isInteractive, isVerboseHelp, isDeprecatedHelp, parsedValue, isDiagnostic, isDiagnosticProtected);
    }

}
