package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;

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
    public PodLock parse(final BufferedReader bufferedReader) {
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
                    final Matcher podMatcher = POD_REGEX.matcher(line);
                    final Matcher podWithSubMatcher = POD_WITH_SUB_REGEX.matcher(line);
                    final Matcher subpodMatcher = SUBPOD_REGEX.matcher(line);
                    if (podWithSubMatcher.matches()) {
                        final Package pod = Package.packageFromString(line, POD_WITH_SUB_REGEX, 1, 2);
                        if (pod != null) {
                            subsection = pod;
                            podLock.pods.add(pod);
                        }
                    } else if (subsection != null && subpodMatcher.matches()) {
                        final Package subpod = Package.packageFromString(line, SUBPOD_REGEX, 1, 2);
                        if (subpod != null) {
                            subsection.dependencies.add(subpod);
                        }
                    } else if (podMatcher.matches()) {
                        final Package pod = Package.packageFromString(line, POD_REGEX, 1, 2);
                        if (pod != null) {
                            podLock.pods.add(pod);
                            subsection = null;
                        }
                    }
                } else if (section == DEPENDENCIES_SECTION) {
                    final Package dependency = Package.packageFromString(line, DEPENDENCY_REGEX, 1, 2);
                    if (dependency != null) {
                        podLock.dependencies.add(dependency);
                    }
                } else if (section == SPEC_CHECKSUMS_SECTION) {
                    final Package dependency = Package.packageFromString(line, SPEC_CHECKSUM_REGEX, 1, 2);
                    if (dependency != null) {
                        podLock.specChecsums.put(dependency.name, dependency.version);
                    }
                } else {
                    // TODO: Log
                    System.out.println("PodLockParser: Couldn't find if statement for >" + line + "\n");
                }
            }
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
            podLock = null;
        }
        return podLock;
    }
}
