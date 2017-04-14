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
package com.blackducksoftware.integration.hub.packman.parser;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory;
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;

public class PackageBdioTransformer {

    public SimpleBdioDocument optimusPrime(final String codeLocationName, final Package project) {
        return generateBdio(codeLocationName, project);
    }

    public SimpleBdioDocument generateBdio(final String codeLocationName, final Package project) {

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

        final String projectName = project.externalId.name;
        final String projectVersion = project.externalId.version;
        final String bdioId = bdioPropertyHelper.createBdioId(projectName, projectVersion);
        final Forge forge = project.forge;

        final BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersion);
        final BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier(forge, project.externalId);
        final BdioProject bdioProject = bdioNodeFactory.createProject(projectName, projectVersion, bdioId, externalIdentifier);

        final List<BdioComponent> bdioComponents = new ArrayList<>();
        for (final Package component : project.dependencies) {
            final String componentName = component.externalId.name;
            final String componentVersion = component.externalId.version;
            final String componentBdioId = bdioPropertyHelper.createBdioId(componentName, componentVersion);
            final BdioExternalIdentifier componentExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(component.forge, component.externalId);
            final BdioComponent bdioComponent = bdioNodeFactory.createComponent(componentName, componentVersion, componentBdioId,
                    componentExternalIdentifier);

            for (final Package componentDependency : component.dependencies) {
                final String dependencyName = componentDependency.externalId.name;
                final String dependencyVersion = componentDependency.externalId.version;
                final String componentDependencyBdioId = bdioPropertyHelper.createBdioId(dependencyName, dependencyVersion);
                final BdioExternalIdentifier componentDependencyExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(componentDependency.forge,
                        component.externalId);
                final BdioComponent bdioComponentDependency = bdioNodeFactory.createComponent(dependencyName, dependencyVersion, componentDependencyBdioId,
                        componentDependencyExternalIdentifier);
                bdioPropertyHelper.addRelationship(bdioComponent, bdioComponentDependency);
            }
            bdioComponents.add(bdioComponent);
            bdioPropertyHelper.addRelationship(bdioProject, bdioComponent);
        }

        final SimpleBdioDocument bdioDocument = new SimpleBdioDocument();
        bdioDocument.billOfMaterials = bdioBillOfMaterials;
        bdioDocument.project = bdioProject;
        bdioDocument.components = bdioComponents;

        return bdioDocument;
    }
}
