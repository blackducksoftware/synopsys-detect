package com.blackducksoftware.integration.hub.packman.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;

@Component
public class Parser {

    @PostConstruct
    public void init() {
        final String podlockFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";
        final String podfileFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";

        try {
            final InputStream podlockStream = new FileInputStream(podlockFilePath);
            final InputStream podfileStream = new FileInputStream(podfileFilePath);
            final CocoapodsPackager packager = new CocoapodsPackager(podfileStream, podlockStream);
            final Package project = packager.makePackage();

            System.out.println(project);

            // TODO: Send to generate bdio from project
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
