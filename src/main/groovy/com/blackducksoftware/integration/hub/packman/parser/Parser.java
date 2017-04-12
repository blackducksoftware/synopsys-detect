package com.blackducksoftware.integration.hub.packman.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.packman.parser.cocoapods.PodLock;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.PodLockParser;

@Component
public class Parser {

	@PostConstruct
	public void init() {
		String filePath = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";

		try {
			InputStream inputStream = new FileInputStream(filePath);

			PodLockParser parser = new PodLockParser();
			PodLock podLock = parser.parse(inputStream);

			System.out.println(podLock);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
