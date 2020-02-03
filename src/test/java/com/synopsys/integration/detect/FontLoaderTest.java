/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.workflow.blackduck.DetectFontLoader;
import com.synopsys.integration.detector.base.DetectorType;

public class FontLoaderTest {
    @Test
    public void loadsCJKFont() {
        DetectFontLoader fontLoader = new DetectFontLoader();
        PDFont font = fontLoader.loadFont(new PDDocument());
        Assertions.assertTrue(font.getName().contains("CJK"));
    }

    @Test
    public void loadsCJKBoldFont() {
        DetectFontLoader fontLoader = new DetectFontLoader();
        PDFont font = fontLoader.loadBoldFont(new PDDocument());
        Assertions.assertTrue(font.getName().contains("CJK"));
    }
}
