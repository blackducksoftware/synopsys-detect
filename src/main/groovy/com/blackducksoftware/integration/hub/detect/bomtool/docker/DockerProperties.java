package com.blackducksoftware.integration.hub.detect.bomtool.docker;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Component
public class DockerProperties {
    @Autowired
    DetectConfiguration detectConfiguration;

    public void populatePropertiesFile(final File dockerPropertiesFile, final File bomToolOutputDirectory) throws IOException, FileNotFoundException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.blackducksoftware", this.detectConfiguration.getLoggingLevel());
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("no.prompt", "true");
        dockerProperties.setProperty("output.path", bomToolOutputDirectory.getAbsolutePath());
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("logging.level.com.blackducksoftware", this.detectConfiguration.getLoggingLevel());
        dockerProperties.setProperty("phone.home", "false");

        for (final String additionalProperty : this.detectConfiguration.getAdditionalDockerPropertyNames()) {
            final String dockerKey = getKeyWithoutPrefix(additionalProperty, DetectConfiguration.DOCKER_PROPERTY_PREFIX);
            addDockerProperty(dockerProperties, additionalProperty, dockerKey);
        }

        dockerProperties.store(new FileOutputStream(dockerPropertiesFile), "");
    }

    public void populateEnvironmentVariables(final Map<String, String> environmentVariables, final String dockerExecutablePath) throws IOException {
        String path = System.getenv("PATH");
        final File dockerExecutableFile = new File(dockerExecutablePath);
        path += File.pathSeparator + dockerExecutableFile.getParentFile().getCanonicalPath();
        environmentVariables.put("PATH", path);
        environmentVariables.put("DOCKER_INSPECTOR_VERSION", this.detectConfiguration.getDockerInspectorVersion());

        final String detectCurlOpts = System.getenv("DETECT_CURL_OPTS");
        if (StringUtils.isNotBlank(detectCurlOpts)) {
            environmentVariables.put("DOCKER_INSPECTOR_CURL_OPTS", detectCurlOpts);
        }

        environmentVariables.put("BLACKDUCK_HUB_PROXY_HOST", this.detectConfiguration.getHubProxyHost());
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PORT", this.detectConfiguration.getHubProxyPort());
        environmentVariables.put("BLACKDUCK_HUB_PROXY_USERNAME", this.detectConfiguration.getHubProxyUsername());
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PASSWORD", this.detectConfiguration.getHubProxyPassword());
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_DOMAIN", this.detectConfiguration.getHubProxyNtlmDomain());
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION", this.detectConfiguration.getHubProxyNtlmWorkstation());

        for (final Map.Entry<String, String> environmentProperty : System.getenv().entrySet()) {
            final String key = environmentProperty.getKey();
            if (key != null && key.startsWith(DetectConfiguration.DOCKER_ENVIRONMENT_PREFIX)) {
                environmentVariables.put(getKeyWithoutPrefix(key, DetectConfiguration.DOCKER_ENVIRONMENT_PREFIX), environmentProperty.getValue());
            }
        }
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        return key.substring(prefix.length());
    }

    private void addDockerProperty(final Properties dockerProperties, final String key, final String dockerKey) {
        final String value = this.detectConfiguration.getDetectProperty(key);
        dockerProperties.setProperty(dockerKey, value);
    }

}
