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
package com.blackducksoftware.integration.hub.detect.testutils

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.BdioWriter
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.bdio.model.BdioBillOfMaterials
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.model.BdioExternalIdentifier
import com.blackducksoftware.integration.hub.bdio.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class BdioCreationUtil {

    private Gson gson
    private BdioPropertyHelper bdioPropertyHelper
    private BdioNodeFactory bdioNodeFactory
    private DependencyGraphTransformer dependencyGraphTransformer


    public BdioCreationUtil() {
        gson = new GsonBuilder().setPrettyPrinting().create()
        bdioPropertyHelper = new BdioPropertyHelper()
        bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)
        dependencyGraphTransformer = new DependencyGraphTransformer(bdioNodeFactory, bdioPropertyHelper)
    }

    public File createBdioDocument(File outputFile, DetectCodeLocation detectCodeLocation) {
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(detectCodeLocation)

        writeSimpleBdioDocument(outputFile, simpleBdioDocument)
    }

    public SimpleBdioDocument createSimpleBdioDocument(DetectCodeLocation detectCodeLocation) {
        final String projectName = detectCodeLocation.bomToolProjectName
        final String projectVersionName = detectCodeLocation.bomToolProjectVersionName
        final String codeLocationName = "${projectName}/${projectVersionName}"

        final BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersionName)
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(detectCodeLocation.externalId)
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, String.format("uuid:%s", UUID.randomUUID()), projectExternalIdentifier)

        final List<BdioComponent> bdioComponents = dependencyGraphTransformer.transformDependencyGraph(project, detectCodeLocation.dependencyGraph)

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument()
        simpleBdioDocument.billOfMaterials = bdioBillOfMaterials
        simpleBdioDocument.project = project
        simpleBdioDocument.components = bdioComponents

        simpleBdioDocument
    }

    public File writeSimpleBdioDocument(File outputFile, SimpleBdioDocument simpleBdioDocument) {
        final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))
        try {
            bdioWriter.writeSimpleBdioDocument(simpleBdioDocument)
        } finally {
            bdioWriter.close()
        }
        println ("BDIO Generated: " + outputFile.getAbsolutePath())

        outputFile
    }
}
