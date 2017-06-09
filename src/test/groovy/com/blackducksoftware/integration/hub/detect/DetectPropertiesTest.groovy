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
package com.blackducksoftware.integration.hub.detect

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import com.blackducksoftware.integration.hub.detect.help.ValueDescriptionAnnotationFinder

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [ValueDescriptionAnnotationFinder.class, DetectProperties.class])
class DetectPropertiesTest {
    @Autowired
    DetectProperties detectProperties

    @Test
    void testPropertiesFieldReference(){
        assertEquals(120, detectProperties.hubTimeout)
        assertEquals(120, detectProperties.getHubTimeout())
    }
}
