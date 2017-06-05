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
package com.blackducksoftware.integration.hub.packman.bomtool.cocoapods

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.Pod
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.PodLock
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.PodLockParser
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

@Component
class CocoapodsPackager {
    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    PodLockParser podLockParser

    List<DependencyNode> makeDependencyNodes(final String sourcePath) {
        final File sourceDirectory = new File(sourcePath)
        final String podLockText = new File(sourceDirectory, "Podfile.lock").text
        final PodLock podLock = podLockParser.parse(podLockText)
        if (podLock == null) {
            return []
        }

        collapseSubpods(podLock)

        String name = projectInfoGatherer.getDefaultProjectName(BomToolType.COCOAPODS, sourcePath)
        String version = projectInfoGatherer.getDefaultProjectVersionName()
        ExternalId externalId = new NameVersionExternalId(Forge.COCOAPODS, name, version)
        List<DependencyNode> dependencies = podTransformer(podLock.dependencies)

        [
            new DependencyNode(name, version, externalId, dependencies as Set)
        ]
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
                if(!isFakePod(pod)) {
                    masterPod = pod
                }
                children += pod.children.findAll { child -> getCleanPodName(child) != masterPod.name}
            }
            masterPod.children = children
            if(!masterPod.version) {
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
