/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.help

import org.apache.commons.lang3.text.WordUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HelpHtmlWriter {
    private final Logger logger = LoggerFactory.getLogger(HelpHtmlWriter.class)


    private final String cssStyles = '''
table {
    border-collapse: collapse;
}

th, td {
    border: 1px solid #ddd;
    padding: 7px 10px;
}

th.groupHeader {
    font-weight: bold;
    background-color: #ddd;
    text-align: left;

}

th {
    font-weight: normal;
    background-color: #eee;
    text-align: left;
}

tbody tr:hover:not(.noBorder) {
    background-color: #f8f8f8;
}

.propertyColumn {
    width: 400px;
}

.defaultColumn {
    width: 300px;
}

.noBorder {
    border: 0px;
}
'''

    @Autowired
    DetectOptionManager detectOptionManager

    public void writeHelpMessage(String fileName) {
        def columnHeaders = ['Property Name', 'Default', 'Description']

        Document doc = Jsoup.parse('<html/>')

        Element head = doc.select('head').first()
        Element style = head.appendElement('style')
        style.appendText(cssStyles)

        Element body = doc.select('body').first()
        Element table = body.appendElement('table')
        Element columnGroup = table.appendElement('colGroup')
        columnGroup.appendElement('col').attr('class', 'propertyColumn')
        columnGroup.appendElement('col').attr('class', 'defaultColumn')
        columnGroup.appendElement('col')

        String group = ''
        detectOptionManager.getDetectOptions().each { detectOption ->
            if (!group.equals(detectOption.getGroup())) {
                group = detectOption.getGroup()
                Element spacerRow = table.appendElement('tr').attr('class', 'noBorder')
                spacerRow.appendElement('td').attr('colspan', '3').attr('class', 'noBorder')

                Element groupHeaderRow = table.appendElement('tr')
                Element groupHeader = groupHeaderRow.appendElement('th').attr('colspan', '3').attr('class', 'groupHeader')
                groupHeader.appendText(WordUtils.capitalize(group))

                Element columnHeadersRow = table.appendElement('tr')
                for (String columnHeaderText : columnHeaders) {
                    Element headerCell = columnHeadersRow.appendElement('th')
                    headerCell.appendText(columnHeaderText)
                }
            }

            def bodyColumns = [
                "--" + detectOption.getKey(),
                detectOption.getDefaultValue().originalDefault,
                detectOption.getDescription()
            ]

            Element row = table.appendElement('tr')
            for (String cellText : bodyColumns) {
                Element cell = row.appendElement('td')
                cell.appendText(cellText)
            }
        }

        logger.info("Writing help document ${fileName}")
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))
        writer.write(doc.html())
        File producedFile = new File(fileName)
        logger.info("Finished writing help document, document can be found at: ${producedFile.getCanonicalPath()}")
        writer.close()
    }
}
