package com.blackducksoftware.integration.hub.packman

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.packman.help.AnnotationFinder

@Component
class HelpLogger {
    private final Logger logger = LoggerFactory.getLogger(HelpPrinter.class)

    @Autowired
    AnnotationFinder finder

    void printHelp(){
        List<String> printList = new ArrayList<>()
        printList.add('')
        printList.add('Properties : ')
        finder.getPackmanValues().each { packmanValue ->
            String optionLine = ""
            String key = StringUtils.rightPad(packmanValue.getKey(), 50, ' ')
            if(StringUtils.isNotBlank(packmanValue.getDescription())){
                optionLine = "\t${key}${packmanValue.getDescription()}"
            } else {
                optionLine = "\t${key}"
            }
            printList.add(optionLine)
        }
        printList.add('')
        printList.add('Usage : ')
        printList.add('\t--<property name>=<value>')
        printList.add('')
        logger.info(StringUtils.join(printList, System.getProperty("line.separator")))
    }
}
