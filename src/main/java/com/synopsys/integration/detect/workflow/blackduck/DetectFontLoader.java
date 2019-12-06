package com.synopsys.integration.detect.workflow.blackduck;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectFontLoader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PDFont loadFont(PDDocument document) {
        try {
            return PDType0Font.load(document, DetectFontLoader.class.getResourceAsStream("/NotoSansCJKtc-Regular.ttf"));
        } catch (IOException e) {
            logger.warn("Failed to load CJK font, some glyphs may not encode correctly.", e);
            return PDType1Font.HELVETICA;
        }
    }

    public PDFont loadBoldFont(PDDocument document) {
        try {
            return PDType0Font.load(document, DetectFontLoader.class.getResourceAsStream("/NotoSansCJKtc-Bold.ttf"));
        } catch (IOException e) {
            logger.warn("Failed to load CJK Bold font, some glyphs may not encode correctly.", e);
            return PDType1Font.HELVETICA_BOLD;
        }
    }
}
