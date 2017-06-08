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

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HelpPrinter {
    @Autowired
    ValueDescriptionAnnotationFinder valueDescriptionAnnotationFinder

    void printHelpMessage(PrintStream printStream) {
        def helpMessagePieces = []
        helpMessagePieces.add('')
        helpMessagePieces.add('Properties : ')
        valueDescriptionAnnotationFinder.detectValues.each { detectValue ->
            String optionLine = ""
            String key = StringUtils.rightPad(detectValue.getKey(), 50, ' ')
            if (StringUtils.isNotBlank(detectValue.getDescription())) {
                optionLine = "\t${key}${detectValue.getDescription()}"
            } else {
                optionLine = "\t${key}"
            }
            helpMessagePieces.add(optionLine)
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        printStream.println(StringUtils.join(helpMessagePieces, System.getProperty("line.separator")))
    }
}
