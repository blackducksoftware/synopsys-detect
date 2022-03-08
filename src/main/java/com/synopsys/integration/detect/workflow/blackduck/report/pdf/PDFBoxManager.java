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

    public PDFBoxManager(File outputFile, PDDocument document) throws IOException {
        this.outputFile = outputFile;
        this.document = document;
        this.currentPage = new PDPage();
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, false);
    }

    public PDRectangle drawRectangleCentered(float x, float cellUpperY, float width, float height, float cellHeight, Color color) throws IOException {
        return drawRectangle(x - (width / 2), cellUpperY - (cellHeight / 2) - (height / 2), width, height, color);
    }

    public PDRectangle drawRectangle(float x, float y, float width, float height, Color color) throws IOException {
        float startingY = checkYAndSwitchPage(y, height);
        contentStream.setNonStrokingColor(color);
        contentStream.addRect(x, startingY, width, height);
        contentStream.fill();
        return new PDRectangle(x, startingY, width, height);
    }

    public PDRectangle drawImageCentered(float x, float cellUpperY, float width, float height, float cellWidth, float cellHeight, String resourceImageName) throws IOException {
        return drawImage(x - (cellWidth / 2), cellUpperY - (cellHeight / 2) - (height / 2), width, height, resourceImageName);
    }

    public PDRectangle drawImage(float x, float y, float width, float height, String resourceImageName) throws IOException {
        float startingY = checkYAndSwitchPage(y, height);
        BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream(resourceImageName));
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
        contentStream.drawImage(pdImage, x, startingY, width, height);
        return new PDRectangle(x, startingY, width, height);
    }

    public PDRectangle writeTextCentered(float x, float y, String text, PDFont font, float fontSize, Color textColor) throws IOException {
        float textLength = StringManager.getStringWidth(font, fontSize, text);
        return writeText(x - (textLength / 2), y, text, font, fontSize, textColor);
    }

    public PDRectangle writeTextCentered(float x, float cellUpperY, float height, String text, PDFont font, float fontSize, Color textColor) throws IOException {
        float textLength = StringManager.getStringWidth(font, fontSize, text);
        return writeText(x - (textLength / 2), cellUpperY - (height / 2) - (fontSize / 2), text, font, fontSize, textColor);
    }

    public PDRectangle writeText(float x, float y, String text, PDFont font, float fontSize, Color textColor) throws IOException {
        float startingY = checkYAndSwitchPage(y, fontSize);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(textColor);
        contentStream.newLineAtOffset(x, startingY);
        String fixedText = StringManager.replaceUnsupportedCharacters(text, font);
        contentStream.showText(fixedText);
        contentStream.endText();
        return new PDRectangle(x, startingY, StringManager.getStringWidth(font, fontSize, fixedText), fontSize);
    }

    public PDRectangle writeWrappedCenteredText(float x, float cellUpperY, float width, float height, List<String> textLines, PDFont font, float fontSize, Color color)
        throws IOException {
        float lowestY = checkYAndSwitchPage(cellUpperY - height, fontSize);
        int numOfLines = textLines.size();
        int centerOfText = numOfLines / 2;
        float actualWidth = width;
        float leftMostX = x + width;
        float centerY = cellUpperY - (height / 2) - (fontSize / 2);
        if (numOfLines % 2 == 0) {
            centerY -= fontSize / 2;
        }
        for (int i = 0; i < numOfLines; i++) {
            float textLength = StringManager.getStringWidth(font, fontSize, textLines.get(i));
            float textX = x - (textLength / 2);
            if (textX < leftMostX) {
                leftMostX = textX;
            }
            float textY;
            int difference = Math.abs(i - centerOfText);
            if (i < centerOfText) {
                textY = centerY + (difference * fontSize);
            } else {
                textY = centerY - (difference * fontSize);
            }
            PDRectangle rectangle = writeText(textX, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
        }
        return new PDRectangle(leftMostX, lowestY, actualWidth, height);
    }

    public PDRectangle writeWrappedVerticalCenteredText(float x, float cellUpperY, float width, float height, List<String> textLines, PDFont font, float fontSize, Color color)
        throws IOException {
        float lowestY = checkYAndSwitchPage(cellUpperY - height, fontSize);

        int numOfLines = textLines.size();
        int centerOfText = numOfLines / 2;
        float actualWidth = width;
        float centerY = cellUpperY - (height / 2) - fontSize / 3;
        if (numOfLines % 2 == 0) {
            centerY -= fontSize / 2;
        }
        for (int i = 0; i < numOfLines; i++) {
            float textY;
            int difference = Math.abs(i - centerOfText);
            if (i < centerOfText) {
                textY = centerY + (difference * fontSize);
            } else {
                textY = centerY - (difference * fontSize);
            }
            PDRectangle rectangle = writeText(x, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
        }

        return new PDRectangle(x, lowestY, actualWidth, height);
    }

    public PDRectangle writeWrappedText(float x, float y, float width, String text, PDFont font, float fontSize, Color color) throws IOException {
        List<String> textLines = StringManager.wrapToCombinedList(font, fontSize, text, width);
        return writeWrappedText(x, y, width, textLines, font, fontSize, color);
    }

    public PDRectangle writeWrappedText(float x, float y, float width, List<String> textLines, PDFont font, float fontSize, Color color) throws IOException {
        float startingY = checkYAndSwitchPage(y, fontSize);
        int numOfLines = textLines.size();
        float actualWidth = width;
        float approximateHeight = 0F;
        float lowestY = startingY;
        for (int i = 0; i < numOfLines; i++) {
            float textY = startingY - (i * fontSize);
            if (textY < lowestY) {
                lowestY = textY;
            }
            PDRectangle rectangle = writeText(x, textY, textLines.get(i), font, fontSize, color);
            if (numOfLines == 1) {
                actualWidth = rectangle.getWidth();
            }
            approximateHeight += rectangle.getHeight();
        }
        return new PDRectangle(x, lowestY, actualWidth, approximateHeight);
    }

    public PDRectangle writeLink(float x, float y, String linkText, String linkURL, PDFont font, float fontSize) throws IOException {
        PDRectangle rectangle = writeText(x, y, linkText, font, fontSize, Color.decode(BLUE_GRAY));
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(float x, float y, float width, String linkText, String linkURL, PDFont font, float fontSize) throws IOException {
        return writeWrappedLink(x, y, width, linkText, linkURL, font, fontSize, Color.decode(BLUE_GRAY));
    }

    public PDRectangle writeWrappedLink(float x, float y, float width, String linkText, String linkURL, PDFont font, float fontSize, Color color) throws IOException {
        PDRectangle rectangle = writeWrappedText(x, y, width, linkText, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedVerticalCenteredLink(
        float x,
        float cellUpperY,
        float width,
        float height,
        List<String> linkTextLines,
        String linkURL,
        PDFont font,
        float fontSize,
        Color color
    ) throws IOException {
        PDRectangle rectangle = writeWrappedVerticalCenteredText(x, cellUpperY, width, height, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(float x, float y, float width, List<String> linkTextLines, String linkURL, PDFont font, float fontSize) throws IOException {
        return writeWrappedLink(x, y, width, linkTextLines, linkURL, font, fontSize, Color.decode(BLUE_GRAY));
    }

    public PDRectangle writeWrappedCenteredLink(
        float x,
        float rowUpperY,
        float width,
        float height,
        List<String> linkTextLines,
        String linkURL,
        PDFont font,
        float fontSize,
        Color color
    )
        throws IOException {
        PDRectangle rectangle = writeWrappedCenteredText(x, rowUpperY, width, height, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    public PDRectangle writeWrappedLink(float x, float y, float width, List<String> linkTextLines, String linkURL, PDFont font, float fontSize, Color color) throws IOException {
        PDRectangle rectangle = writeWrappedText(x, y, width, linkTextLines, font, fontSize, color);
        addAnnotationLinkRectangle(rectangle.getLowerLeftX(), rectangle.getLowerLeftY(), rectangle.getWidth(), rectangle.getHeight(), linkURL);
        return rectangle;
    }

    private PDRectangle addAnnotationLinkRectangle(float x, float y, float width, float height, String linkURL) throws IOException {
        float startingY = checkYAndSwitchPage(y, height);
        PDAnnotationLink txtLink = new PDAnnotationLink();
        PDRectangle position = new PDRectangle();
        position.setLowerLeftX(x);
        position.setLowerLeftY(startingY);
        position.setUpperRightX(x + width);
        position.setUpperRightY(startingY + height);
        txtLink.setRectangle(position);

        PDActionURI action = new PDActionURI();
        action.setURI(linkURL);
        txtLink.setAction(action);

        currentPage.getAnnotations().add(txtLink);
        return new PDRectangle(x, startingY, width, height);
    }

    private float checkYAndSwitchPage(float y, float height) throws IOException {
        if (y - 20 < 0) {
            contentStream.close();
            this.currentPage = new PDPage();
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, false);
            return currentPage.getMediaBox().getHeight() - 20 - height;
        }
        return y;
    }

    public float getApproximateWrappedStringHeight(int numberOfTextLines, float fontSize) {
        return numberOfTextLines * fontSize + fontSize;
    }

    @Override
    public void close() throws IOException {
        contentStream.close();
        document.save(outputFile);
        document.close();
    }
}
