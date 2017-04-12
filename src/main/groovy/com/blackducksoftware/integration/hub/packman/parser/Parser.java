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
		String podlockFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";
		String podfileFilePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";

		try {
			InputStream podlockStream = new FileInputStream(podlockFilePath);
			InputStream podfileStream = new FileInputStream(podfileFilePath);
			CocoapodsPackager packager = new CocoapodsPackager(podfileStream, podlockStream);
			Package project = packager.makePackage();

			System.out.println(project);

			// TODO: Send to generate bdio from project
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
