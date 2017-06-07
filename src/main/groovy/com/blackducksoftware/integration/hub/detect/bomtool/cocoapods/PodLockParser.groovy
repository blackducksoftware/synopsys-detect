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
