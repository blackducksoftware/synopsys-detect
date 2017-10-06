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
package com.blackducksoftware.integration.hub.detect.bomtool.pear

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

class PearDependencyTest {
    private PearDependencyFinder pearDependencyFinder
    private TestUtil testUtil

    @Before
    public void init() {
        pearDependencyFinder = new PearDependencyFinder()
        pearDependencyFinder.externalIdFactory = new ExternalIdFactory()
        testUtil = new TestUtil()
    }

    @Test
    public void findDependencyNamesTest() {
        DetectProperties detectProperties = new DetectProperties()
        detectProperties.pearOnlyRequiredDependencies = true
        DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.detectProperties = detectProperties
        pearDependencyFinder.detectConfiguration = detectConfiguration

        def dependenciesList = testUtil.getResourceAsUTF8String('/pear/dependencies-list.txt')
        def exeOutput = new ExecutableOutput(dependenciesList, '')

        List<String> actual = pearDependencyFinder.findDependencyNames(exeOutput.getStandardOutputAsList())
        List<String> expected = [
            'Archive_Tar',
            'Structures_Graph',
            'Console_Getopt',
            'XML_Util',
            'PEAR_Frontend_Web',
            'PEAR_Frontend_Gtk',
            'xml',
            'pcre'
        ]

        Assert.assertEquals(expected, actual)
    }

    @Test
    public void createPearDependencyNodeFromListTest() {
        def installedPackages = testUtil.getResourceAsUTF8String('/pear/installed-packages.txt')
        ExecutableOutput exeOutput = new ExecutableOutput(installedPackages, '')

        def dependencyNames = [
            'Archive_Tar',
            'Console_Getopt',
            'Structures_Graph'
        ]
        def actual = pearDependencyFinder.createPearDependencyGraphFromList(exeOutput.getStandardOutputAsList(), dependencyNames)

        DependencyGraphTestUtil.assertGraph('/pear/dependency-node-list_graph.json', actual);
    }
}
