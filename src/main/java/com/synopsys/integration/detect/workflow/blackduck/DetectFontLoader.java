/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
