package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.integration.hub.packman.parser.Package;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;

public class PodLockParser extends StreamParser<PodLock> {

	final String PODS_SECTION = "PODS:";
	final String DEPENDENCIES_SECTION = "DEPENDENCIES:";
	final String SPEC_CHECKSUMS_SECTION = "SPEC CHECKSUMS:";
	final String PODFILE_CHECKSUM_SECTION = "PODFILE CHECKSUM:";
	final String COCOAPODS_SECTION = "COCOAPODS:";

	final Pattern POD_REGEX = Pattern.compile("  - (.*)\\((.*)\\)");
	final Pattern POD_WITH_SUB_REGEX = Pattern.compile("  - (.*)\\((.*)\\):");
	final Pattern SUBPOD_REGEX = Pattern.compile("    - (.*)\\((.*)\\)");
	final Pattern DEPENDENCY_REGEX = Pattern.compile("  - (.*)\\((.*)\\)");
	final Pattern SPEC_CHECKSUM_REGEX = Pattern.compile("  (.*):(.*)");

	@Override
	public PodLock parse(BufferedReader bufferedReader) {
		PodLock podLock = new PodLock();

		String section = null;
		Package subsection = null;

		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if (line.isEmpty()) {

				} else if (line.contains(COCOAPODS_SECTION)) {
					section = COCOAPODS_SECTION;
					podLock.cococapodsVersion = line.split(":")[1].trim();
				} else if (line.contains(PODS_SECTION)) {
					section = PODS_SECTION;
				} else if (line.contains(DEPENDENCIES_SECTION)) {
					section = DEPENDENCIES_SECTION;
				} else if (line.contains(SPEC_CHECKSUMS_SECTION)) {
					section = SPEC_CHECKSUMS_SECTION;
				} else if (line.contains(PODFILE_CHECKSUM_SECTION)) {
					section = PODFILE_CHECKSUM_SECTION;
					podLock.podfileChecksum = line.split(":")[1].trim();
				} else if (section == PODS_SECTION) {
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
				} else if (section == DEPENDENCIES_SECTION) {
					Package dependency = packageFromString(line, DEPENDENCY_REGEX);
					if (dependency != null) {
						podLock.dependencies.add(dependency);
					}
				} else if (section == SPEC_CHECKSUMS_SECTION) {
					Package dependency = packageFromString(line, SPEC_CHECKSUM_REGEX);
					if (dependency != null) {
						podLock.specChecsums.put(dependency.packageName, dependency.packageVersion);
					}
				} else {
					// TODO: Log
					System.out.println("Couldn't find if statement for >" + line + "\n");
				}
			}
		} catch (IOException e) {
			// TODO: Log
			e.printStackTrace();
			podLock = null;
		}
		return podLock;
	}

	private Package packageFromString(String str, Pattern regex) {
		Matcher matcher = regex.matcher(str);
		if (matcher.matches()) {
			try {
				Package dependency = new Package(matcher.group(1).trim(), matcher.group(2).trim());
				return dependency;
			} catch (IndexOutOfBoundsException e) {
				// TODO: Log
				System.out.println("Couldn't regex match " + regex.toString() + " >" + str);
			}
		}
		return null;
	}
}
