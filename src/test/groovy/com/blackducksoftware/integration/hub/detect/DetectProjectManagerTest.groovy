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

import static org.junit.Assert.assertEquals

import org.junit.Test

class DetectProjectManagerTest {
    @Test
    public void extractFinalPieceFromPath() {
        def detectProjectManager = new DetectProjectManager()
        assertEquals('a', detectProjectManager.extractFinalPieceFromSourcePath('/a'))
        assertEquals('a', detectProjectManager.extractFinalPieceFromSourcePath('/a/'))
        assertEquals('c', detectProjectManager.extractFinalPieceFromSourcePath('/a/b/c'))
        assertEquals('c', detectProjectManager.extractFinalPieceFromSourcePath('/a/b/c/'))
    }
}
