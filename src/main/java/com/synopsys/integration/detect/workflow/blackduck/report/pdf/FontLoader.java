package com.synopsys.integration.detect.workflow.blackduck.report.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;

@FunctionalInterface
public interface FontLoader {
    PDFont loadFont(PDDocument document);

}
