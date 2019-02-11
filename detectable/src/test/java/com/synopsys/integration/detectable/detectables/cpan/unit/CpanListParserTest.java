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
package com.synopsys.integration.detectable.detectables.cpan.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;

@UnitTest
public class CpanListParserTest {
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory());

    @Test
    public void parseTest() {
        final List<String> tokens = new ArrayList<>();
        tokens.add("Test::More\t1.2.3");
        tokens.add("Test::Less\t1.2.4");
        tokens.add("This is an invalid line");
        tokens.add("This\t1\t1also\t1invalid");
        tokens.add("Invalid");

        final Map<String, String> nodeMap = cpanListParser.createNameVersionMap(tokens);
        assertEquals(2, nodeMap.size());
        assertNotNull(nodeMap.get("Test::More"));
        assertNotNull(nodeMap.get("Test::Less"));
        assertEquals("1.2.3", nodeMap.get("Test::More"));
        assertEquals("1.2.4", nodeMap.get("Test::Less"));
    }
}
