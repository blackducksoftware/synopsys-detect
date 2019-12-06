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
