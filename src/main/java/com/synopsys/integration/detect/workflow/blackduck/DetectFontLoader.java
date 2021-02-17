/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.blackduck;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectFontLoader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PDFont loadFont(final PDDocument document) {
        try {
            return PDType0Font.load(document, DetectFontLoader.class.getResourceAsStream("/NotoSansCJKtc-Regular.ttf"));
        } catch (final IOException e) {
            logger.warn("Failed to load CJK font, some glyphs may not encode correctly.", e);
            return PDType1Font.HELVETICA;
        }
    }

    public PDFont loadBoldFont(final PDDocument document) {
        try {
            return PDType0Font.load(document, DetectFontLoader.class.getResourceAsStream("/NotoSansCJKtc-Bold.ttf"));
        } catch (final IOException e) {
            logger.warn("Failed to load CJK Bold font, some glyphs may not encode correctly.", e);
            return PDType1Font.HELVETICA_BOLD;
        }
    }
}
