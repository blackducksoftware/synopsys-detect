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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan


import static org.junit.Assert.*

import org.junit.Test

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode

class CpanListParserTest {
    private CpanListParser cpanListParser = new CpanListParser()

    @Test
    public void parseTest() {
        String cpanList = '''
Test::More\t1.2.3
Test::Less\t1.2.4
This is an invalid line
This\t1\t1also\t1invalid
Invalid
'''
        Map<String, NameVersionNode> nodeMap = cpanListParser.parse(cpanList.tokenize('\n'))
        assertEquals(2, nodeMap.size())
        assertNotNull(nodeMap['Test::More'])
        assertNotNull(nodeMap['Test::Less'])
        assertEquals('Test::More', nodeMap['Test::More'].name)
        assertEquals('1.2.3', nodeMap['Test::More'].version)
        assertEquals('Test::Less', nodeMap['Test::Less'].name)
        assertEquals('1.2.4', nodeMap['Test::Less'].version)
    }
}
