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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@SpringBootApplication
class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class)

    @Autowired
    BdioPropertyHelper bdioPropertyHelper

    @Autowired
    BdioNodeFactory bdioNodeFactory

    @Autowired
    PackageManagerRunner parser

    @Autowired
    BdioUploader bdioUploader

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(true).run(args)
    }

    @PostConstruct
    void init() {
        List<File> createdBdioFiles = parser.createBdioFiles()
        bdioUploader.uploadBdioFiles(createdBdioFiles)
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
        new BdioNodeFactory(bdioPropertyHelper)
    }

    @Bean
    DependencyNodeTransformer dependencyNodeTransformer() {
        new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper)
    }
}