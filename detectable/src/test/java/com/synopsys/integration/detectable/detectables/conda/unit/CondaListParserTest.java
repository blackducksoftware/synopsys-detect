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
package com.synopsys.integration.detectable.detectables.conda.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;

@UnitTest
public class CondaListParserTest {
    private CondaListParser condaListParser;

    @Before
    public void init() {
        condaListParser = new CondaListParser(new GsonBuilder().setPrettyPrinting().create(), new ExternalIdFactory());
    }

    @Test
    public void condaListElementToDependencyNodeTransformerTest() {
        final String platform = "linux";
        final CondaListElement element = new CondaListElement();
        element.name = "sampleName";
        element.version = "sampleVersion";
        element.buildString = "py36_0";
        final Dependency dependency = condaListParser.condaListElementToDependency(platform, element);

        assertEquals("sampleName", dependency.name);
        assertEquals("sampleVersion-py36_0-linux", dependency.version);
        assertEquals("sampleName=sampleVersion-py36_0-linux", dependency.externalId.createExternalId());
    }
}
