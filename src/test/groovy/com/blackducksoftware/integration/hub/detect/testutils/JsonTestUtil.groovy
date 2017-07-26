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
package com.blackducksoftware.integration.hub.detect.testutils

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.skyscreamer.jsonassert.JSONAssert

class JsonTestUtil {
    void testJsonResource(String expectedResourcePath, String actualJson) {
        final String expected = getResourceAsUTF8String(expectedResourcePath)
        testJson(expected, actualJson)
    }

    void testJson(String expectedJson, String actualJson) {
        JSONAssert.assertEquals(expectedJson, actualJson, false)
    }

    String getResourceAsUTF8String(String expectedResourcePath) {
        IOUtils.toString(this.getClass().getResourceAsStream(expectedResourcePath), StandardCharsets.UTF_8)
    }
}
