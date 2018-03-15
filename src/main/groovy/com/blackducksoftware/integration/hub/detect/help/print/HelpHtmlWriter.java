/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;

@Component
public class HelpHtmlWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpHtmlWriter.class);

    @Autowired
    DetectOptionManager detectOptionManager;

    private final String cssStyles = "table {\n" +
            "    border-collapse: collapse;\n" +
            "}\n" +
            "th,td {\n" +
            "    border: 1\n" +
            "    final px solid#ddd;padding:7 px 10 px;\n" +
            "}\n" +
            "\n" +
            "th.groupHeader {\n" +
            "    font-weight:bold;\n" +
            "    background-color:#ddd;\n" +
            "    text-align:left;\n" +
            "}\n" +
            "\n" +
            "th {\n" +
            "    font-weight:normal;\n" +
            "    background-color:#eee;\n" +
            "    text-align:left;\n" +
            "}\n" +
            "\n" +
            "tbody tr:hover:not(final .noBorder) {\n" +
            "    background-color: #f8f8f8;\n" +
            "}\n" +
            "\n" +
            ".propertyColumn {\n" +
            "    width: 400px;\n" +
            "}\n" +
            "\n" +
            ".defaultColumn {\n" +
            "    width: 300px;\n" +
            "}\n" +
            "\n" +
            ".noBorder {\n" +
            "    border: 0px;\n" +
            "}";

    public void writeHelpMessage(final String fileName) {
        final List<String> columnHeaders = Arrays.asList("Property Name",
                "Default",
                "Description");

        final Document doc = Jsoup.parse("<html/>");

        final Element head = doc.select("head").first();
        final Element style = head.appendElement("style");
        style.appendText(cssStyles);

        final Element body = doc.select("body").first();
        final Element table = body.appendElement("table");
        final Element columnGroup = table.appendElement("colGroup");
        columnGroup.appendElement("col").attr("class", "propertyColumn");
        columnGroup.appendElement("col").attr("class", "defaultColumn");
        columnGroup.appendElement("col");

        String group = "";
        for (final DetectOption detectOption : detectOptionManager.getDetectOptions()) {
            if (!group.equals(detectOption.getGroup())) {
                group = detectOption.getGroup();
                final Element spacerRow = table.appendElement("tr").attr("class", "noBorder");
                spacerRow.appendElement("td").attr("colspan", "3").attr("class", "noBorder");

                final Element groupHeaderRow = table.appendElement("tr");
                final Element groupHeader = groupHeaderRow.appendElement("th").attr("colspan", "3").attr("class", "groupHeader");
                groupHeader.appendText(WordUtils.capitalize(group));

                final Element columnHeadersRow = table.appendElement("tr");
                for (final String columnHeaderText : columnHeaders) {
                    final Element headerCell = columnHeadersRow.appendElement("th");
                    headerCell.appendText(columnHeaderText);
                }
            }

            final List<String> bodyColumns = Arrays.asList("--" + detectOption.getKey(),
                    detectOption.getDefaultValue(),
                    detectOption.getDescription());

            final Element row = table.appendElement("tr");
            for (final String cellText : bodyColumns) {
                final Element cell = row.appendElement("td");
                cell.appendText(cellText);
            }
        }

        logger.info("Writing help document " + fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(doc.html());
            final File producedFile = new File(fileName);
            logger.info("Finished writing help document, document can be found at: " + producedFile.getCanonicalPath());
        } catch (final IOException e) {
            logger.error("Issue writing to file " + fileName);
        }
    }
}
