package com.blackducksoftware.integration.hub.detect.workflow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public class ArtifactResolver {
    private final ConnectionManager connectionManager;
    private final Gson gson;

    public ArtifactResolver(final ConnectionManager connectionManager, final Gson gson) {
        this.connectionManager = connectionManager;
        this.gson = gson;
    }

    /**
     * Communicates with Artifactory to find the location of an artifact.
     * Will either return the url of the given artifactory property or will calculate url the given version would point to.
     * @param artifactoryBaseUrl      The base url of artifactory, such as http://int-jfrog01.dc1.lan:8081/artifactory/
     * @param repositoryUrl           The url of the repository with the artifact, such as bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector
     * @param propertyKey             The property to find, such as DETECT_GRADLE_INSPECTOR_LATEST_0
     * @param overrideVersion         The version to use, if provided, overrides the property tag.
     * @param overrideArtifactPattern The pattern to use when the override version is provided of the full artifact location.
     * @return the location of the artifact
     */
    public Optional<String> resolveArtifactLocation(final String artifactoryBaseUrl, final String repositoryUrl, final String propertyKey, final String overrideVersion, final String overrideArtifactPattern) {
        if (StringUtils.isNotBlank(overrideVersion) && StringUtils.isNotBlank(overrideArtifactPattern)) {
            String repoUrl = artifactoryBaseUrl + repositoryUrl;
            String versionUrl = overrideArtifactPattern.replace(ArtifactoryConstants.VERSION_PLACEHOLDER, overrideVersion);
            return Optional.of(repoUrl + versionUrl);
        } else {
            String apiUrl = artifactoryBaseUrl + "api/storage/" + repositoryUrl;
            Optional<String> artifactUrl = downloadProperty(apiUrl, propertyKey);
            return artifactUrl;
        }
    }

    /**
     * Communicates with Artifactory to find the actual version of an artifact.
     * @param artifactoryBaseUrl The base url of artifactory, such as http://int-jfrog01.dc1.lan:8081/artifactory/
     * @param repositoryUrl      The url of the repository with the artifact, such as bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector
     * @param propertyKey        The property to find, such as DETECT_GRADLE_INSPECTOR_LATEST_0
     * @param overrideVersion    The version to use, if provided, overrides the property tag.
     * @return the calculated version of the artifact
     */
    public Optional<String> resolveArtifactVersion(final String artifactoryBaseUrl, final String repositoryUrl, final String propertyKey, final String overrideVersion) {
        if (StringUtils.isNotBlank(overrideVersion)) {
            return Optional.of(overrideVersion);
        } else {
            String apiUrl = artifactoryBaseUrl + "api/storage/" + repositoryUrl;
            Optional<String> artifactUrl = downloadProperty(apiUrl, propertyKey);
            return artifactUrl;
        }
    }

    private Optional<String> downloadProperty(String apiUrl, String propertyKey) {
        String propertyUrl = apiUrl + "?properties=" + propertyKey;
        final Request request = new Request.Builder().uri(propertyUrl).build();
        try (final UnauthenticatedRestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(propertyUrl)) {
            try (final Response response = restConnection.executeRequest(request)) {
                try (final InputStreamReader reader = new InputStreamReader(response.getContent())) {
                    Map json = gson.fromJson(reader, Map.class);
                    Map propertyMap = (Map) json.get("properties");
                    List propertyUrls = (List) propertyMap.get(propertyKey);
                    return propertyUrls.stream().findFirst();
                }
            }
        } catch (IntegrationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DetectUserFriendlyException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public String parseFileName(String source) {
        String[] pieces = source.split("/");
        String filename = pieces[pieces.length - 1];
        return filename;
    }

    public Optional<File> downloadOrFindArtifact(File targetDir, String source) {
        File fileTarget = new File(targetDir, parseFileName(source));
        if (fileTarget.exists()) {
            return Optional.of(fileTarget);
        } else {
            return downloadArtifact(fileTarget, source);
        }
    }

    public Optional<File> downloadArtifact(File target, String source) {
        final Request request = new Request.Builder().uri(source).build();
        try (final UnauthenticatedRestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(source)) {
            try (Response response = restConnection.executeRequest(request)) {
                target.delete();
                final InputStream jarBytesInputStream = response.getContent();
                FileUtils.copyInputStreamToFile(jarBytesInputStream, target);
                return Optional.of(target);
            } catch (IntegrationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (DetectUserFriendlyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
