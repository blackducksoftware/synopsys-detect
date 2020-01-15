/**
 * detect-configuration
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
package com.synopsys.integration.detect.help;

import java.util.List;

import com.synopsys.integration.detect.configuration.DetectProperty;

public abstract class DetectOption {
    private final DetectProperty detectProperty;
    private final List<String> validValues;
    private final boolean strictValidation;
    private final boolean caseSensitiveValidation;
    private final DetectOptionHelp detectOptionHelp;

    public DetectOption(final DetectProperty detectProperty, final boolean strictValidation, final boolean caseSensitiveValidation, final List<String> validValues, final DetectOptionHelp detectOptionHelp) {
        this.detectProperty = detectProperty;
        this.strictValidation = strictValidation;
        this.caseSensitiveValidation = caseSensitiveValidation;
        this.validValues = validValues;
        this.detectOptionHelp = detectOptionHelp;
    }

    public boolean isCommaSeperatedList() {
        return false;
    }

    public DetectProperty getDetectProperty() {
        return detectProperty;
    }

    public DetectOptionHelp getDetectOptionHelp() {
        return detectOptionHelp;
    }

    public boolean hasStrictValidation() {
        return strictValidation;
    }

    public boolean hasCaseSensitiveValidation() {
        return caseSensitiveValidation;
    }

    public List<String> getValidValues() {
        return validValues;
    }
}
