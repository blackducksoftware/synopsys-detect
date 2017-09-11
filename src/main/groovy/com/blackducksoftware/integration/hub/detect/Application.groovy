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
package com.blackducksoftware.integration.hub.detect

import javax.annotation.PostConstruct

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
import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.detect.help.HelpPrinter
import com.blackducksoftware.integration.hub.detect.help.ValueDescriptionAnnotationFinder
import com.blackducksoftware.integration.hub.detect.hub.HubManager
import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.summary.DetectSummary
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import freemarker.template.Configuration
import groovy.transform.TypeChecked

@TypeChecked
@SpringBootApplication
class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class)

    @Autowired
    ValueDescriptionAnnotationFinder valueDescriptionAnnotationFinder

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableManager executableManager

    @Autowired
    DetectProjectManager detectProjectManager

    @Autowired
    BdioPropertyHelper bdioPropertyHelper

    @Autowired
    BdioNodeFactory bdioNodeFactory

    @Autowired
    ApplicationArguments applicationArguments

    @Autowired
    HelpPrinter helpPrinter

    @Autowired
    HubManager hubManager

    @Autowired
    HubServiceWrapper hubServiceWrapper

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    DetectSummary detectSummary

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args)
    }

    @PostConstruct
    void init() {
        int postResult = 0
        try {
            valueDescriptionAnnotationFinder.init()
            if ('-h' in applicationArguments.getSourceArgs() || '--help' in applicationArguments.getSourceArgs()) {
                helpPrinter.printHelpMessage(System.out)
                return
            }

            detectConfiguration.init()
            executableManager.init()
            logger.info('Configuration processed completely.')
            if (!detectConfiguration.suppressConfigurationOutput) {
                detectConfiguration.logConfiguration()
            }

            if (detectConfiguration.testConnection) {
                hubServiceWrapper.testHubConnection()
                return
            }

            DetectProject detectProject = detectProjectManager.createDetectProject()
            List<File> createdBdioFiles = detectProjectManager.createBdioFiles(detectProject)
            if (!detectConfiguration.hubOfflineMode) {
                hubServiceWrapper.init()
                ProjectVersionView projectVersionView = hubManager.updateHubProjectVersion(detectProject, createdBdioFiles)
                postResult = hubManager.performPostHubActions(detectProject, projectVersionView)
            } else if (!detectConfiguration.hubSignatureScannerDisabled){
                hubSignatureScanner.scanPathsOffline(detectProject)
            }
        } catch (DetectException e) {
            logger.error('An unrecoverable error occurred - most likely this is due to your environment and/or configuration. Please double check the Hub Detect documentation: https://blackducksoftware.atlassian.net/wiki/x/Y7HtAg')
            logger.error(e.getMessage())
        }
        detectSummary.logResults(new Slf4jIntLogger(logger))
        System.exit(postResult)
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
        new DependencyNodeTransformer(bdioNodeFactory(), bdioPropertyHelper())
    }

    @Bean
    IntegrationEscapeUtil integrationEscapeUtil() {
        new IntegrationEscapeUtil()
    }

    @Bean
    Configuration configuration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26)
        configuration.setClassForTemplateLoading(Application.class, '/')
        configuration.setDefaultEncoding('UTF-8')
        configuration.setLogTemplateExceptions(true)

        configuration
    }
}
