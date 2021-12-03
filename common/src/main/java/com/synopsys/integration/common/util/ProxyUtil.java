package com.synopsys.integration.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.log.IntLogger;

public class ProxyUtil {
    /**
     * Checks the list of user defined host names that should be connected to directly and not go through the proxy. If the hostToMatch matches any of these hose names then this method returns true.
     */
    public static boolean shouldIgnoreHost(String hostToMatch, List<Pattern> ignoredProxyHostPatterns) {
        if (StringUtils.isBlank(hostToMatch) || ignoredProxyHostPatterns == null || ignoredProxyHostPatterns.isEmpty()) {
            return false;
        }

        for (Pattern ignoredProxyHostPattern : ignoredProxyHostPatterns) {
            Matcher m = ignoredProxyHostPattern.matcher(hostToMatch);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldIgnoreUrl(String url, List<Pattern> ignoredProxyHostPatterns, IntLogger logger) {
        try {
            return ProxyUtil.shouldIgnoreHost(new URL(url).getHost(), ignoredProxyHostPatterns);
        } catch (MalformedURLException e) {
            logger.error("Unable to decide if proxy should be used for the given host, will use proxy.");
            return false;
        }
    }

}