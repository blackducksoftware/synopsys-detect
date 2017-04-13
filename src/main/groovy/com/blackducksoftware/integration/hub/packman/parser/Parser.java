package com.blackducksoftware.integration.hub.packman.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class Parser {

    @PostConstruct
    public void init() throws IOException {
        final String podlockFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";
        final String podfileFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile";
        final String outputDirectoryPath = "/Users/jmathews/ruby/black-duck-swift-sample/bdio/";

        try {
            final InputStream podlockStream = new FileInputStream(podlockFilePath);
            final InputStream podfileStream = new FileInputStream(podfileFilePath);
            final CocoapodsPackager packager = new CocoapodsPackager(podfileStream, podlockStream);
            final List<Package> projects = packager.makePackages();

            final PackageBdioTransformer transformer = new PackageBdioTransformer();

            final List<SimpleBdioDocument> bdioDocuments = new ArrayList<>();
            for (final Package project : projects) {
                bdioDocuments.add(transformer.generateBdio(null, project));
            }
            final File outputDirectory = new File(outputDirectoryPath);
            outputDirectory.mkdirs();
            for (final SimpleBdioDocument bdioDocument : bdioDocuments) {
                final File bdioFile = new File(outputDirectory, bdioDocument.project.name + "_bdio.jsonld");
                final OutputStream outputStream = new FileOutputStream(bdioFile);
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                final BdioWriter bdioWriter = new BdioWriter(gson, outputStream);
                bdioWriter.writeBdioNode(bdioDocument.billOfMaterials);
                bdioWriter.writeBdioNode(bdioDocument.project);
                bdioWriter.writeBdioNodes(bdioDocument.components);
                bdioWriter.close();
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
