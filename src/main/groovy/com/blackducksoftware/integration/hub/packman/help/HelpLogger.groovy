package com.blackducksoftware.integration.hub.packman.help

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HelpLogger {
    private final Logger logger = LoggerFactory.getLogger(HelpLogger.class)

    @Autowired
    ValueDescriptionAnnotationFinder valueDescriptionAnnotationFinder

    void logHelpMessage(){
        def helpMessagePieces = []
        helpMessagePieces.add('')
        helpMessagePieces.add('Properties : ')
        valueDescriptionAnnotationFinder.packmanValues.each { packmanValue ->
            String optionLine = ""
            String key = StringUtils.rightPad(packmanValue.getKey(), 50, ' ')
            if (StringUtils.isNotBlank(packmanValue.getDescription())) {
                optionLine = "\t${key}${packmanValue.getDescription()}"
            } else {
                optionLine = "\t${key}"
            }
            helpMessagePieces.add(optionLine)
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        logger.info(StringUtils.join(helpMessagePieces, System.getProperty("line.separator")))
    }
}
