/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.report.pdf;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

public class PDFBoxManager implements Closeable {
    public static final String BLUE_GRAY = "#46759E";

    public final File outputFile;
    public final PDDocument document;
    public PDPage currentPage;
    private PDPageContentStream contentStream;

    public PDFBoxManager(final File outputFile, final PDDocument document) throws IOException {
        this.outputFile = outputFile;
        this.document = document;
        this.currentPage = new PDPage();
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, false);
    }

    public PDRectangle drawRectangleCentered(final float x, final float cellUpperY, final float width, final float height, final float cellHeight, final Color color) throws IOException {
        return drawRectangle(x - (width / 2), cellUpperY - (cellHeight / 2) - (height / 2), width, height, color);
    }

    public PDRectangle drawRectangle(final float x, final float y, final float width, final float height, final Color color) throws IOException {
        final float startingY = checkYAndSwitchPage(y, height);
        contentStream.setNonStrokingColor(color);
        contentStream.addRect(x, startingY, width, height);
        contentStream.fill();
        return new PDRectangle(x, startingY, width, height);
    }

    public PDRectangle drawImageCentered(final float x, final float cellUpperY, final float width, final float height, final float cellWidth, final float cellHeight, final String resourceImageName) throws IOException {
        return drawImage(x - (cellWidth / 2), cellUpperY - (cellHeight / 2) - (height / 2), width, height, resourceImageName);
    }

    public PDRectangle drawImage(final float x, final float y, final float width, final float height, final String resourceImageName) throws IOException {
        final float startingY = checkYAndSwitchPage(y, height);
        final BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream(resourceImageName));
        final PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
        contentStream.drawImage(pdImage, x, startingY, width, height);
        return new PDRectangle(x, startingY, width, height);
    }

    public PDRectangle writeTextCentered(final float x, final float y, final String text, final PDFont font, final float fontSize, final Color textColor) throws IOException {
        final float textLength = StringManager.getStringWidth(font, fontSize, text);
        return writeText(x - (textLength / 2), y, text, font, fontSize, textColor);
    }

    public PDRectangle writeTextCentered(final float x, final float cellUpperY, final float height, final String text, final PDFont font, final float fontSize, final Color textColor) throws IOException {
        final float textLength = StringManager.getStringWidth(font, fontSize, text);
        return writeText(x - (textLength / 2), cellUpperY - (height / 2) - (fontSize / 2), text, font, fontSize, textColor);
    }

    public PDRectangle writeText(final float x, final float y, final String text, final PDFont font, final float fontSize, final Color textColor) throws IOException {
        final float startingY = checkYAndSwitchPage(y, fontSize);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(textColor);
        contentStream.newLineAtOffset(x, startingY);
        final String fixedText = StringManager.replaceUnsupportedCharacters(text, font);
        contentStream.showText(fixedText);
        contentStream.endText();
        return new PDRectangle(x, startingY, StringManager.getStringWidth(font, fontSize, fixedText), fontSize);
    }

    public PDRectangle writeWrappedCenteredText(final float x, final float cellUpperY, final float width, final float height, final List<String> textLines, final PDFont font, final float fontSize, final Color color) throws IOException {
        final float lowestY = checkYAndSwitchPage(cellUpperY - height, fontSize);
        final int numOfLines = textLines.size();
        final int centerOfText = numOfLines / 2;
        float actualWidth = width;
        float leftMostX = x + width;
        float centerY = cellUpperY - (height / 2) - (fontSize / 2);
        if (numOfLines % 2 == 0) {
            centerY -= fontSize / 2;
        }
        for (int i = 0; i < numOfLines; i++) {
            final float textLength = StringManager.getStringWidth(font, fontSize, textLines.get(i));
            final float textX = x - (textLength / 2);
            if (textX < leftMostX) {
                leftMostX = textX;
            }
            final float textY;
            final int difference = Math.abs(i - centerOfText);
            if (i < centerOfText) {
                textY = centerY + (difference * fontSize);
            } else {
                textY = centerY - (difference * fontSize);
            }
            final PDRectangle rectangle = writeText(textX, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
        }
        return new PDRectangle(leftMostX, lowestY, actualWidth, height);
    }

    public PDRectangle writeWrappedVerticalCenteredText(final float x, final float cellUpperY, final float width, final float height, final List<String> textLines, final PDFont font, final float fontSize, final Color color)
        throws IOException {
        final float lowestY = checkYAndSwitchPage(cellUpperY - height, fontSize);

        final int numOfLines = textLines.size();
        final int centerOfText = numOfLines / 2;
        float actualWidth = width;
        float centerY = cellUpperY - (height / 2) - fontSize / 3;
        if (numOfLines % 2 == 0) {
            centerY -= fontSize / 2;
        }
        for (int i = 0; i < numOfLines; i++) {
            final float textY;
            final int difference = Math.abs(i - centerOfText);
            if (i < centerOfText) {
                textY = centerY + (difference * fontSize);
            } else {
                textY = centerY - (difference * fontSize);
            }
            final PDRectangle rectangle = writeText(x, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
        }

        return new PDRectangle(x, lowestY, actualWidth, height);
    }

    public PDRectangle writeWrappedText(final float x, final float y, final float width, final String text, final PDFont font, final float fontSize, final Color color) throws IOException {
        final List<String> textLines = StringManager.wrapToCombinedList(font, fontSize, text, width);
        return writeWrappedText(x, y, width, textLines, font, fontSize, color);
    }

    public PDRectangle writeWrappedText(final float x, final float y, final float width, final List<String> textLines, final PDFont font, final float fontSize, final Color color) throws IOException {
        final float startingY = checkYAndSwitchPage(y, fontSize);
        final int numOfLines = textLines.size();
        float actualWidth = width;
        float approximateHeight = 0F;
        float lowestY = startingY;
        for (int i = 0; i < numOfLines; i++) {
            final float textY = startingY - (i * fontSize);
            if (textY < lowestY) {
                lowestY = textY;
            }
            final PDRectangle rectangle = writeText(x, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
            approximateHeight += rectangle.getHeight();
        }
        return new PDRectangle(x, lowestY, actualWidth, approximateHeight);
    }

    public PDRectangle writeLink(final float x, final float y, final String linkText, final String linkURL, final PDFont font, final float fontSize) throws IOException {
        final PDRectangle rectangle = writeText(x, y, linkText, font, fontSize, Color.decode(BLUE_GRAY));
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(final float x, final float y, final float width, final String linkText, final String linkURL, final PDFont font, final float fontSize) throws IOException {
        return writeWrappedLink(x, y, width, linkText, linkURL, font, fontSize, Color.decode(BLUE_GRAY));
    }

    public PDRectangle writeWrappedLink(final float x, final float y, final float width, final String linkText, final String linkURL, final PDFont font, final float fontSize, final Color color) throws IOException {
        final PDRectangle rectangle = writeWrappedText(x, y, width, linkText, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedVerticalCenteredLink(final float x, final float cellUpperY, final float width, final float height, final List<String> linkTextLines, final String linkURL, final PDFont font, final float fontSize,
        final Color color) throws IOException {
        final PDRectangle rectangle = writeWrappedVerticalCenteredText(x, cellUpperY, width, height, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(final float x, final float y, final float width, final List<String> linkTextLines, final String linkURL, final PDFont font, final float fontSize) throws IOException {
        return writeWrappedLink(x, y, width, linkTextLines, linkURL, font, fontSize, Color.decode(BLUE_GRAY));
    }

    public PDRectangle writeWrappedCenteredLink(final float x, final float rowUpperY, final float width, final float height, final List<String> linkTextLines, final String linkURL, final PDFont font, final float fontSize, final Color color)
        throws IOException {
        final PDRectangle rectangle = writeWrappedCenteredText(x, rowUpperY, width, height, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(final float x, final float y, final float width, final List<String> linkTextLines, final String linkURL, final PDFont font, final float fontSize, final Color color) throws IOException {
        final PDRectangle rectangle = writeWrappedText(x, y, width, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    private PDRectangle addAnnotationLinkRectangle(final float x, final float y, final float width, final float height, final String linkURL) throws IOException {
        final float startingY = checkYAndSwitchPage(y, height);
        final PDAnnotationLink txtLink = new PDAnnotationLink();
        final PDRectangle position = new PDRectangle();
        position.setLowerLeftX(x);
        position.setLowerLeftY(startingY);
        position.setUpperRightX(x + width);
        position.setUpperRightY(startingY + height);
        txtLink.setRectangle(position);

        final PDActionURI action = new PDActionURI();
        action.setURI(linkURL);
        txtLink.setAction(action);

        currentPage.getAnnotations().add(txtLink);
        return new PDRectangle(x, startingY, width, height);
    }

    private float checkYAndSwitchPage(final float y, final float height) throws IOException {
        if (y - 20 < 0) {
            contentStream.close();
            this.currentPage = new PDPage();
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, false);
            return currentPage.getMediaBox().getHeight() - 20 - height;
        }
        return y;
    }

    public float getApproximateWrappedStringHeight(final int numberOfTextLines, final float fontSize) {
        return numberOfTextLines * fontSize + fontSize;
    }

    @Override
    public void close() throws IOException {
        contentStream.close();
        document.save(outputFile);
        document.close();
    }
}
