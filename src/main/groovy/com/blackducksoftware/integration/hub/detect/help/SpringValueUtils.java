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

public class SpringValueUtils {

    public static String springKeyFromValueAnnotation(String value) {
        if (value.contains("${")) {
            value = value.substring(2);
        }
        if (value.endsWith("}")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.contains(":")) {
            value = value.split(":")[0];
        }
        return value;
    }
}
