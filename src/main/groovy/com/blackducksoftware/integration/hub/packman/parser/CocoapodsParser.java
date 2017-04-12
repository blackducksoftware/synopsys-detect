package com.blackducksoftware.integration.hub.packman.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.packman.parser.podfile.PodLock;

@Component
class CocoapodsParser {

	// TODO: Currently will throw null pointer if file is invalid.
	// We should change that...

	final String PODLOCK_FILE = "/Users/jmathews/ruby/black-duck-swift-sample/Podfile.lock";

	final Pattern POD_REGEX = Pattern.compile("  - (.*)\\((.*)\\)");
	final Pattern POD_WITH_SUB_REGEX = Pattern.compile("  - (.*)\\((.*)\\):");
	final Pattern SUBPOD_REGEX = Pattern.compile("    - (.*)\\((.*)\\)");
	final Pattern DEPENDENCY_REGEX = Pattern.compile("  - (.*)\\((.*)\\)");
	final Pattern SPEC_CHECKSUM_REGEX = Pattern.compile("  (.*):(.*)");

	// TODO: Parse this info from podfile perhaps?
	final String PROJECT_NAME = "black-duck-sample-project";
	final String PROJECT_VERSION = "1.0.0";

	@PostConstruct
	void init() {
		Package project = new Package(PROJECT_NAME, PROJECT_VERSION);
		PodLock podLock = new PodLock();

		String section = null;
		Package subsection = null;

		String line;
		try (
			InputStream fis = new FileInputStream(PODLOCK_FILE);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
		) {
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {

				} else if (line.contains(PodLock.PODS_SECTION)) {
					section = PodLock.PODS_SECTION;
				} else if (line.contains(PodLock.DEPENDENCIES_SECTION)) {
					section = PodLock.DEPENDENCIES_SECTION;
				} else if (line.contains(PodLock.SPEC_CHECKSUMS_SECTION)) {
					section = PodLock.SPEC_CHECKSUMS_SECTION;
				} else if (line.contains(PodLock.PODFILE_CHECKSUM_SECTION)) {
					section = PodLock.PODFILE_CHECKSUM_SECTION;
					podLock.podfileChecksum = line.split(":")[1].trim();
				} else if (line.contains("COCOAPODS")) {
					// TODO: This won't run.
					System.out.println("----------------------------------------");
					section = PodLock.COCOAPODS_SECTION;
					podLock.cococapodsVersion = line.split(":")[1].trim();

				} else if (section == PodLock.PODS_SECTION) {
					Matcher podMatcher = POD_REGEX.matcher(line);
					Matcher podWithSubMatcher = POD_WITH_SUB_REGEX.matcher(line);
					Matcher subpodMatcher = SUBPOD_REGEX.matcher(line);
					if (podWithSubMatcher.matches()) {
						Package pod = packageFromString(line, POD_WITH_SUB_REGEX);
						if (pod != null) {
							subsection = pod;
							podLock.pods.add(pod);
						}
					} else if (subsection != null && subpodMatcher.matches()) {
						Package subpod = packageFromString(line, SUBPOD_REGEX);
						if (subpod != null) {
							subsection.dependencies.add(subpod);
						}
					} else if (podMatcher.matches()) {
						Package pod = packageFromString(line, POD_REGEX);
						if (pod != null) {
							podLock.pods.add(pod);
							subsection = null;
						}
					}
				} else if (section == PodLock.DEPENDENCIES_SECTION) {
					Package dependency = packageFromString(line, DEPENDENCY_REGEX);
					if (dependency != null) {
						podLock.dependencies.add(dependency);
					}
				} else if (section == PodLock.SPEC_CHECKSUMS_SECTION) {
					Package dependency = packageFromString(line, SPEC_CHECKSUM_REGEX);
					if (dependency != null) {
						podLock.specChecsums.put(dependency.packageName, dependency.packageVersion);
					}
				} else {
					// TODO: Log this
					System.out.println("Couldn't find if statement for >" + line + "\n");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(podLock.toString());
	}

	private Package packageFromString(String str, Pattern regex) {
		Matcher matcher = regex.matcher(str);
		if (matcher.matches()) {
			try {
				Package dependency = new Package(matcher.group(1).trim(), matcher.group(2).trim());
				return dependency;
			} catch (IndexOutOfBoundsException e) {
				// TODO: Grouping is invalid in regex. Log it
				System.out.println("Couldn't regex match " + regex.toString() + " >" + str);
			}
		}
		return null;
	}
}
