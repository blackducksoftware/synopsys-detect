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

        StringBuilder headerLineBuilder = new StringBuilder()
        headerLineBuilder.append(StringUtils.rightPad('Property Name', 40, ' '))
        headerLineBuilder.append(StringUtils.rightPad('Default', 30, ' '))
        headerLineBuilder.append(StringUtils.rightPad('Description', 75, ' '))
        headerLineBuilder.append(StringUtils.rightPad('Type', 20, ' '))

        helpMessagePieces.add(headerLineBuilder.toString())
        helpMessagePieces.add(StringUtils.repeat('_', 165))
        def character = null
        valueDescriptionAnnotationFinder.getDetectValues().each { detectValue ->
            StringBuilder optionLineBuilder = new StringBuilder()
            def currentCharacter = detectValue.getKey()[7]
            if (character == null) {
                character = currentCharacter
            } else if (!character.equals(currentCharacter)) {
                helpMessagePieces.add(StringUtils.repeat(' ', 165))
                character = currentCharacter
            }
            optionLineBuilder.append(StringUtils.rightPad("${detectValue.getKey()}", 40, ' '))
            optionLineBuilder.append(StringUtils.rightPad(detectValue.getDefaultValue(), 30, ' '))
            optionLineBuilder.append(StringUtils.rightPad(detectValue.getDescription(), 75, ' '))
            optionLineBuilder.append(StringUtils.rightPad(detectValue.getValueType().getSimpleName(), 20, ' '))
            helpMessagePieces.add(optionLineBuilder.toString())
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        printStream.println(StringUtils.join(helpMessagePieces, System.getProperty("line.separator")))
    }
}
