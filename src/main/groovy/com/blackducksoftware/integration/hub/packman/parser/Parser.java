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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory;
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper;
import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class Parser {

    @PostConstruct
    public void init() throws IOException {
        final String podlockFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";
        final String podfileFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile";
        final String podspecFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/BlackDuckSwiftSample.podspec";
        final String project1FilePath = "/Users/jmathews/ruby/black-duck-swift-sample/black-duck-swift-sample.xcodeproj/project.pbxproj";
        final String project2FilePath = "/Users/jmathews/ruby/black-duck-swift-sample/swift-ios-test/swift-ios-test.xcodeproj/project.pbxproj";

        final String outputDirectoryPath = "/Users/jmathews/ruby/black-duck-swift-sample/bdio/";

        try (
                final InputStream podlockStream = new FileInputStream(podlockFilePath);
                final InputStream podfileStream = new FileInputStream(podfileFilePath);
                final InputStream podspecStream = new FileInputStream(podspecFilePath);
        // final InputStream project1Stream = new FileInputStream(project1FilePath);
        // final InputStream project2Stream = new FileInputStream(project2FilePath);
        ) {
            // final InputStream[] projectStreams = { project1Stream, project2Stream };
            final CocoapodsPackager packager = new CocoapodsPackager(podfileStream, podlockStream, podspecStream);
            final List<DependencyNode> projects = packager.makeDependencyNodes();

            final File outputDirectory = new File(outputDirectoryPath);
            outputDirectory.mkdirs();
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();

            for (final DependencyNode project : projects) {

                final File bdioFile = new File(outputDirectory, project.name + "_bdio.jsonld");
                try (
                        final OutputStream bdioStream = new FileOutputStream(bdioFile);
                        final BdioWriter bdioWriter = new BdioWriter(gson, bdioStream);
                ) {
                    final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
                    final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
                    final DependencyNodeTransformer transformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper);
                    final SimpleBdioDocument bdioDocument = transformer.transformDependencyNode(project);
                    bdioWriter.writeSimpleBdioDocument(bdioDocument);
                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
