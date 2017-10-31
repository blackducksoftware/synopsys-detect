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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HelpHtmlWriter {
    @Autowired
    ValueDescriptionAnnotationFinder valueDescriptionAnnotationFinder

    public void writeHelpMessage(String fileName) {
        Document doc = Jsoup.parse('<html/>')
        Element body = doc.select('body').first()
        Element table = body.appendElement('table').attr('style', 'border-collapse:collapse')
        Element tableHeader = table.appendElement('thead').attr('style', 'text-align:left')

        def columnHeaders = [
            'Property Name',
            'Default',
            'Description'
        ]

        for(String columnHeaderText : columnHeaders) {
            Element headerCell = tableHeader.appendElement('th').attr('style', 'border:1px solid #ddd; padding: 7px 10px;')
            headerCell.appendText(columnHeaderText)
        }

        valueDescriptionAnnotationFinder.getDetectValues().each { detectValue ->
            Element row = table.appendElement('tr')
            def bodyColumns = [
                "--" + detectValue.getKey(),
                detectValue.getDefaultValue(),
                detectValue.getDescription()
            ]

            for (String cellText : bodyColumns) {
                Element cell = row.appendElement('td').attr('style', 'border:1px solid #ddd; padding: 7px 10px; border-collapse:collapse')
                cell.appendText(cellText)
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))
            writer.write(doc.html())

            writer.close()
        }
    }
}
