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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId

@Component
class CocoapodsPackager {
    @Autowired
    PodLockParser podLockParser

    List<DependencyNode> extractProjectDependencies(final String podLockText) {
        final PodLock podLock = podLockParser.parse(podLockText)
        if (podLock == null) {
            return []
        }

        collapseSubpods(podLock)

        List<DependencyNode> dependencies = podTransformer(podLock.dependencies)
        dependencies
    }

    List<DependencyNode> podTransformer(List<Pod> pods) {
        def nodes = []
        pods.each { pod ->
            ExternalId externalId = new NameVersionExternalId(Forge.COCOAPODS, pod.name, pod.version)
            List<DependencyNode> children = podTransformer(pod.children)
            nodes += new DependencyNode(pod.name, pod.version, externalId, children as Set)
        }

        nodes
    }

    void collapseSubpods(PodLock podlock) {
        Map<String, List<Pod>> allPods = podlock.pods.groupBy({ pod -> getCleanPodName(pod) })
        List<Pod> finalPods = []
        allPods.each { name, pods ->
            // Set the default masterPod as the first pod
            Pod masterPod = pods[0]
            masterPod.name = getCleanPodName(pods[0])
            List<Pod> children = []
            pods.each { pod ->
                if (!isFakePod(pod)) {
                    masterPod = pod
                }
                children += pod.children.findAll { child -> getCleanPodName(child) != masterPod.name}
            }
            masterPod.children = children
            if (!masterPod.version) {
                masterPod.version = masterPod.children[0].version
            }

            finalPods += masterPod
        }
        podlock.pods = finalPods

        Map<String, Pod> finalPodsMap = [:]
        finalPods.each { pod ->
            finalPodsMap[pod.name] = pod
        }

        finalPods.each { collapsePod(finalPodsMap, it) }

        Map<String, List<Pod>> newPods = finalPodsMap
        List<Pod> finalDependencies = []
        podlock.dependencies.each {
            finalDependencies += newPods[getCleanPodName(it)]
        }
        podlock.dependencies = finalDependencies
    }

    void collapsePod(Map<String, Pod> pods, Pod pod) {
        Set<Pod> newChildren = new HashSet<>()
        pod.children.each {
            newChildren += pods[getCleanPodName(it)]
        }
        pod.children = newChildren as List
    }

    boolean isFakePod(Pod pod) {
        pod.name.contains('/')
    }

    String getCleanPodName(Pod pod) {
        pod.name.split('/')[0].trim()
    }
}
