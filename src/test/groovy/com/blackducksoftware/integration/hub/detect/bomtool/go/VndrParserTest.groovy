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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.go.vndr.VndrParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class VndrParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void vndrParserTest() throws IOException {
        final VndrParser vndrParser = new VndrParser();
        final String vendorConfContents = IOUtils.toString(getClass().getResourceAsStream("/go/vendor.conf"), StandardCharsets.UTF_8);
        final List<DependencyNode> dependencies = vndrParser.parseVendorConf(vendorConfContents);
        Assert.assertNotNull(dependencies)

        final String actual = gson.toJson(dependencies)
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/go/Go_VndrExpected.json"), StandardCharsets.UTF_8)
        JSONAssert.assertEquals(expected, actual, false);
    }
}
