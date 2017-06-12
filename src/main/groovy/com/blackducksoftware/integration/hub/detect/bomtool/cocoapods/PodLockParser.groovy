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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import org.springframework.stereotype.Component

import com.esotericsoftware.yamlbeans.YamlException
import com.esotericsoftware.yamlbeans.YamlReader

@Component
class PodLockParser {
    PodLock parse(String podLockText) throws YamlException {
        final YamlReader fullReader = new YamlReader(podLockText)
        final Object object = fullReader.read()
        final def fileMap = (Map<String, Object>) object

        // Parse the PODS section
        final Map<String, Pod> allPods = [:]
        def pods = (List<Object>) fileMap.get('PODS')
        for(Object podObj : pods) {
            if(podObj instanceof String) {
                Pod pod = parsePodLine(podObj)
                if(!allPods[pod.name]) {
                    allPods[pod.name] = pod
                }
            } else {
                // If a pod has children it will appear as a map with a single entry
                List<Pod> mappedPods = []
                def podMap = (Map<String, List<String>>) podObj
                podMap.each { key, value ->
                    final Pod parent = parsePodLine(key)
                    // These children will be set to other pods after initial processing
                    value.each { child ->
                        final Pod childPod = parsePodLine(child)
                        childPod.version = null
                        parent.children.add(childPod)
                    }
                    mappedPods.add(parent)
                }
                Pod pod = mappedPods.get(0)
                if(!allPods[pod.name]) {
                    allPods[pod.name] = pod
                }
            }
        }

        // Fix children
        allPods.entrySet().each { pod ->
            def actualChildren = []
            pod.value.children.each  { child ->
                actualChildren += allPods[child.name]
            }
            pod.value.children = actualChildren
        }

        // Parse the DEPENDENCIES section
        def actualDependencies = []
        final Map<String, Pod> dependencies = [:]
        def depPods = (List<Object>) fileMap.get('DEPENDENCIES')
        depPods.each {
            Pod dependency = parsePodLine((String) it)
            actualDependencies += allPods[dependency.name]
        }


        PodLock podLock = new PodLock()
        podLock.pods = new ArrayList<>(allPods.values())
        podLock.dependencies = actualDependencies

        podLock
    }

    Pod parsePodLine(String podText) {
        Pod pod = new Pod()
        String text = podText.trim()
        if(!text) {
            return null
        }

        if(text.contains(' (') && text.contains(')')) {
            String[] segments = text.split(' ')
            pod.name = segments[0].trim()
            pod.version = segments[1].replace('(', '').replace(')', '').trim()
        } else {
            pod.name = text
        }

        pod
    }
}
