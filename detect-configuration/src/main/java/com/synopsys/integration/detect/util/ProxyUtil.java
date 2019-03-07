package com.synopsys.integration.detect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ProxyUtil {
    /**
     * Checks the list of user defined host names that should be connected to directly and not go through the proxy. If the hostToMatch matches any of these hose names then this method returns true.
     */
    public static boolean shouldIgnoreHost(final String hostToMatch, final List<Pattern> ignoredProxyHostPatterns) {
        if (StringUtils.isBlank(hostToMatch) || ignoredProxyHostPatterns == null || ignoredProxyHostPatterns.isEmpty()) {
            return false;
        }

        for (final Pattern ignoredProxyHostPattern : ignoredProxyHostPatterns) {
            final Matcher m = ignoredProxyHostPattern.matcher(hostToMatch);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static List<Pattern> getIgnoredProxyHostPatterns(final String ignoredProxyHosts) {
        final List<Pattern> ignoredProxyHostPatterns = new ArrayList<>();
        if (StringUtils.isNotBlank(ignoredProxyHosts)) {
            String[] ignoreHosts = null;
            if (ignoredProxyHosts.contains(",")) {
                ignoreHosts = ignoredProxyHosts.split(",");
                for (final String ignoreHost : ignoreHosts) {
                    final Pattern pattern = Pattern.compile(ignoreHost.trim());
                    ignoredProxyHostPatterns.add(pattern);
                }
            } else {
                final Pattern pattern = Pattern.compile(ignoredProxyHosts);
                ignoredProxyHostPatterns.add(pattern);
            }
        }
        return ignoredProxyHostPatterns;
    }

}