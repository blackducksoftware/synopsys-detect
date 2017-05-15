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
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class PodLockParser {
    private final Logger logger = LoggerFactory.getLogger(PodLockParser.class);
    private static final Pattern NAME_VERSION_PATTERN = Pattern.compile("(.*) \\((.*)\\)")

    public PodLock parse(final String podlockText) {
        final PodLock podLock = new PodLock();
        try {
            final YamlReader fullReader = new YamlReader(podlockText);
            final Object object = fullReader.read();
            final Map<String, Object> map = (Map<String, Object>) object;

            // Extract dependencies
            final List<String> dependencyNames = (List<String>) map.get("DEPENDENCIES");

            dependencyNames.each { dependencyName ->
                final DependencyNode node = podToDependencyNode(dependencyName);
                podLock.dependencies.add(node);
            }

            // Extract pods
            final List<Object> pods = (List<Object>) map.get("PODS");
            pods.each { pod ->
                DependencyNode node = null;
                if (pod instanceof HashMap<?, ?>) {
                    // There should only be one parent node
                    final List<DependencyNode> parentNodes = []
                    final Map<String, List<String>> podMap = (Map<String, ArrayList<String>>) pod;
                    podMap.each { key, value ->
                        final DependencyNode parent = podToDependencyNode(key);
                        value.each { child ->
                            final DependencyNode childNode = podToDependencyNode(child);
                            parent.children.add(childNode);
                        }
                        parentNodes.add(parent);
                    }
                    node = parentNodes.get(0);
                } else {
                    node = podToDependencyNode(pod.toString());
                }
                podLock.pods.add(node);
            }
        } catch (final YamlException ingore) {
            logger.error("Cannot parse Podfile.lock. Invalid YAML file");
            return null;
        }
        return podLock;
    }

    private DependencyNode podToDependencyNode(final String pod) {
        final Matcher podMatcher = NAME_VERSION_PATTERN.matcher(pod);
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
