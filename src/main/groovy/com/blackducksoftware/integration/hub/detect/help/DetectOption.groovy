/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.help;

public class DetectOption {
    final String key;
    final String description;
    final Class valueType;
    final String defaultValue;

    public DetectOption(final String key, final String description,Class valueType, String defaultValue) {
        this.key = key;
        this.description = description;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }
}
