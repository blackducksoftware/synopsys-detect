/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.packman.help.AnnotationFinder
import com.google.gson.Gson
import com.google.gson.GsonBuilder


@SpringBootApplication
class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class)

    @Autowired
    PackageManagerRunner packageManagerRunner

    @Autowired
    BdioUploader bdioUploader

    @Autowired
    ApplicationArguments args

    @Autowired
    AnnotationFinder finder

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(true).run(args)
    }

    @PostConstruct
    void init() {
        if ('-h' in args.getSourceArgs() || '--help' in args.getSourceArgs()){
            printHelp()
        } else {
            List<File> createdBdioFiles = parser.createBdioFiles()
            bdioUploader.uploadBdioFiles(createdBdioFiles)
        }
    }

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

    @Bean
    Gson gson() {
        new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ExternalId.class, new ExternalIdTypeAdapter()).create()
    }

    @Bean
    BdioPropertyHelper bdioPropertyHelper() {
        new BdioPropertyHelper()
    }

    @Bean
    BdioNodeFactory bdioNodeFactory() {
        // TODO: Fix bean dependencies
        new BdioNodeFactory(bdioPropertyHelper())
    }

    @Bean
    DependencyNodeTransformer dependencyNodeTransformer() {
        // TODO: Fix bean dependencies
        new DependencyNodeTransformer(bdioNodeFactory(), bdioPropertyHelper())
    }
}