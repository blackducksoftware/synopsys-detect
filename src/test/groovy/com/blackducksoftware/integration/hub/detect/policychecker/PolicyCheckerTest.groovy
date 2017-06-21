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
package com.blackducksoftware.integration.hub.detect.policychecker

import static org.junit.Assert.assertTrue

import org.junit.Test

class PolicyCheckerTest {

    @Test
    public void testConversionToBdioModel() {
        BdioPolicy expected = new BdioPolicy()
        expected.name = 'node-js'
        expected.version = '0.2.0'
        def testIn = new File(getClass().getResource('/policychecker/BdioParserTest.jsonld').getFile())

        PolicyChecker policyChecker = new PolicyChecker(null, null)
        BdioPolicy actual = policyChecker.convertFromJsonToSimpleBdioDocument(testIn)

        assertTrue(expected.name.equals(actual.name) && expected.version.equals(actual.version))
    }
}
