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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtConfigurationDependencyTree

public class SbtConfigurationAggregator {

    private final Logger logger = LoggerFactory.getLogger(SbtConfigurationAggregator.class)

    DependencyNode aggregateConfigurations(List<SbtConfigurationDependencyTree> configurations){
        def name = findSharedName(configurations);
        def org = findSharedOrg(configurations);
        def version = findSharedVersion(configurations);

        DependencyNode root = new DependencyNode(new MavenExternalId(org, name, version));
        root.name = name;
        root.version = version;
        root.children = new ArrayList<DependencyNode>();
        configurations.each {config ->
            root.children += config.rootNode.children;
        }

        root
    }

    String findSharedName(List<SbtConfigurationDependencyTree> configurations) {
        def names = configurations.collect{ config -> config.rootNode.name };
        firstUniqueOrLogError(names, "configuration name")
    }

    String findSharedOrg(List<SbtConfigurationDependencyTree> configurations) {
        def orgs = configurations.collect{ config ->
            def id = config.rootNode.externalId as MavenExternalId;
            id.group
        };
        firstUniqueOrLogError(orgs, "organisation")
    }

    String findSharedVersion(List<SbtConfigurationDependencyTree> configurations) {
        def versions = configurations.collect{ config -> config.rootNode.version }
        firstUniqueOrLogError(versions, "version")
    }

    String firstUniqueOrLogError(List<String> things, String thingType) {
        def uniqueThings = things.toUnique()
        def result = ""
        if (uniqueThings.size == 1){
            result = uniqueThings.first()
        }else if (uniqueThings.size == 0){
            logger.error("Could not find any ${thingType} in ivy reports!")
        }else if (uniqueThings.size > 1) {
            logger.error("Found more than 1 unique ${thingType} in ivy reports: ${uniqueThings}!")
        }

        result
    }
}
