package com.synopsys.integration.detectable.detectables.git;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

public class GitUrlParser {
    // Parses urls such as: https://github.com/blackducksoftware/synopsys-detect
    public String getRepoName(String remoteUrlString) throws MalformedURLException {
        String[] pieces = remoteUrlString.split("[/:]");
        if (pieces.length >= 2) {
            String organization = pieces[pieces.length - 2];
            String repo = pieces[pieces.length - 1];
            String name = String.format("%s/%s", organization, repo);
            return StringUtils.removeEnd(StringUtils.removeStart(name, "/"), ".git");
        } else {
            throw new MalformedURLException("Failed to extract repository name from url. Not logging url for security.");
        }
    }

    /**
     * Strip out credentials if the string is a URI and contains credentials.
     * 
     * For example, a URL such as https://user:pass@synopsys.com/some/repo will become https://synopsys.com/some/repo
     * 
     * @param remoteUrlString
     * @return sanitized URI or original string
     */
    public String removeCredentialsFromUri(String remoteUrlString) {
        if (remoteUrlString != null) {
            try {
                URI uri = new URI(remoteUrlString);
                String userInfo = uri.getUserInfo();
                if (userInfo != null) {
                    remoteUrlString = remoteUrlString.replace(userInfo + "@", "");
                }
            } catch (URISyntaxException e) {
                // this is not a valid URI, so we will not attempt to remove credentials
            }
        }
        return remoteUrlString;
    }    
}
