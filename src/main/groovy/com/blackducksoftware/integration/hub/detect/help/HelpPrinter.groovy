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
