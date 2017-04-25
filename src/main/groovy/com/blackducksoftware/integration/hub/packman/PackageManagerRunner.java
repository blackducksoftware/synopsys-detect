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
package com.blackducksoftware.integration.hub.packman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.packman.packagemanager.PackageManager;
import com.google.gson.Gson;

@Component
public class PackageManagerRunner {
    private final Logger logger = LoggerFactory.getLogger(PackageManagerRunner.class);

    @Autowired
    private List<PackageManager> packageManagers;

    @Autowired
    private Gson gson;

    @Autowired
    private DependencyNodeTransformer dependencyNodeTransformer;

    public void parseSourcePaths(final String[] sourcePaths, final String outputDirectoryPath) throws IOException {
        for (final PackageManager packageManager : packageManagers) {
            for (final String sourcePath : sourcePaths) {
                final String packageManagerName = packageManager.getPackageManagerType().toString().toLowerCase();
                logger.info(String.format("Searching source path for %s: %s", packageManagerName, sourcePath));
                if (packageManager.isPackageManagerApplicable(sourcePath)) {
                    logger.info(String.format("Found files for %s", packageManagerName));
                    final List<DependencyNode> projectNodes = packageManager.extractDependencyNodes(sourcePath);
                    if (projectNodes != null && projectNodes.size() > 0) {
                        createOutput(outputDirectoryPath, packageManager.getPackageManagerType(), projectNodes);
                    }
                }
            }
        }
    }

    private void createOutput(final String outputDirectoryPath, final PackageManagerType packageManagerType, final List<DependencyNode> projectNodes) {
        final File outputDirectory = new File(outputDirectoryPath);
        outputDirectory.mkdirs();

        for (final DependencyNode project : projectNodes) {
            final String filename = String.format("%s_%s_%s_bdio.jsonld", packageManagerType.toString(), project.name, project.version);
            final File outputFile = new File(outputDirectory, filename);
            try (final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))) {
                final SimpleBdioDocument bdioDocument = dependencyNodeTransformer.transformDependencyNode(project);
                bdioWriter.writeSimpleBdioDocument(bdioDocument);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
