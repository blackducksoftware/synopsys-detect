package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;

public class PodfileParser extends StreamParser<Podfile> {

    final Pattern PLATFORM_REGEX = Pattern.compile("platform :(.*)\\s*");

    final Pattern TARGET_REGEX = Pattern.compile("target *('|\")(.*)\\1 *do\\s*");

    final Pattern TARGET_END_REGEX = Pattern.compile("end\\s*");

    final Pattern FRAMEWORKS_REGEX = Pattern.compile(" *use_frameworks!");

    final Pattern POD_REGEX = Pattern.compile("\\s*pod *('|\")(.*)\\1, *('|\")(.*)\\3\\s*");

    @Override
    public Podfile parse(final BufferedReader bufferedReader) {
        Podfile podfile = new Podfile();

        Package currentTarget = null;
        final String version = DateTime.now().toString("YYYY-MM-DD HH:mm");

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher platformMatcher = PLATFORM_REGEX.matcher(line);
                final Matcher targetMatcher = TARGET_REGEX.matcher(line);
                final Matcher targetEndMatcher = TARGET_END_REGEX.matcher(line);
                final Matcher frameworksMatcher = FRAMEWORKS_REGEX.matcher(line);
                final Matcher podMatcher = POD_REGEX.matcher(line);

                // Handle comments
                if (line.contains("#")) {
                    final String[] sections = line.split("#");
                    if (sections.length > 0) {
                        line = sections[0].trim();
                    } else {
                        line = "";
                    }
                }

                if (line.isEmpty()) {

                } else if (platformMatcher.matches()) {
                    podfile.platform = platformMatcher.group(1);
                    currentTarget = null;
                } else if (targetMatcher.matches()) {
                    final String targetName = targetMatcher.group(2);
                    final Package target = new Package(targetName, version);
                    currentTarget = target;
                    podfile.targets.add(currentTarget);
                } else if (frameworksMatcher.matches()) {
                    podfile.useFramworks = frameworksMatcher.group();
                } else if (podMatcher.matches() && currentTarget != null) {
                    final Package dependency = Package.packageFromString(line, POD_REGEX, 2, 4, Forge.cocoapods);
                    if (dependency != null) {
                        currentTarget.dependencies.add(dependency);
                    }
                } else if (targetEndMatcher.matches()) {
                    currentTarget = null;
                } else {
                    // TODO: Log
                    System.out.println("PodfileParser: Couldn't find if statement for >" + line + "\n");
                }
            }
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
            podfile = null;
        }
        return podfile;
    }
}
