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
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.PodLock;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class PodLockParser {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    public PodLock parse(final String podlockText) {
        final PodLock podLock = new PodLock();

        try {
            final YamlReader fullReader = new YamlReader(podlockText);
            final Object object = fullReader.read();
            final Map<String, Object> map = (Map<String, Object>) object;

            // Extract dependencies
            final List<String> dependencyNames = (List<String>) map.get("DEPENDENCIES");

            if (dependencyNames != null) {
                dependencyNames.forEach(dependencyName -> {
                    final DependencyNode node = podToDependencyNode(dependencyName);
                    podLock.dependencies.add(node);
                });
            }

            // Extract pods
            final List<Object> pods = (List<Object>) map.get("PODS");
            pods.forEach(pod -> {
                DependencyNode node = null;
                if (pod instanceof HashMap<?, ?>) {
                    // There should only be one parent node
                    final List<DependencyNode> parentNodes = new ArrayList<>();
                    final Map<String, ArrayList<String>> podMap = (Map<String, ArrayList<String>>) pod;
                    podMap.entrySet().forEach(entry -> {
                        final DependencyNode parent = podToDependencyNode(entry.getKey());
                        entry.getValue().forEach(child -> {
                            final DependencyNode childNode = podToDependencyNode(child);
                            parent.children.add(childNode);
                        });
                        parentNodes.add(parent);
                    });
                    node = parentNodes.get(0);
                } else {
                    node = podToDependencyNode(pod.toString());
                }

                if (node != null) {
                    podLock.pods.add(node);
                } else {
                    logger.info("Couldn't extract pod from text >" + pod.toString());
                }
            });
        } catch (final YamlException ingore) {
            logger.error("Cannot parse Podfile.lock. Invalid YAML file");
        }

        return podLock;
    }

    private DependencyNode podToDependencyNode(final String pod) {
        final Matcher podMatcher = Pattern.compile("(.*) \\((.*)\\)").matcher(pod);
        String name;
        String version;
        if (podMatcher.find()) {
            name = podMatcher.group(1);
            version = podMatcher.group(2);
        } else {
            name = pod.trim();
            version = null;
        }
        final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, name, version);
        final DependencyNode node = new DependencyNode(name, version, externalId);
        return node;
    }
}
