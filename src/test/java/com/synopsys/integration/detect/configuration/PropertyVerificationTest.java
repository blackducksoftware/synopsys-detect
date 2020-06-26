package com.synopsys.integration.detect.configuration;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.Property;

public class PropertyVerificationTest {

    @Test
    public void verifyProperties() {
        Set<String> missing = new HashSet<>();
        List<String> kotlinNames = DetectProperties.Companion.getProperties().stream()
                                       .map(Property::getName)
                                       .collect(Collectors.toList());

        List<String> javaNames = collectJavaProperties().stream()
                                       .map(Property::getName)
                                       .collect(Collectors.toList());

        for (String propertyName : kotlinNames) {
            if (!javaNames.contains(propertyName)) {
                missing.add(propertyName);
            }
        }
        for (String propertyName : javaNames) {
            if (!kotlinNames.contains(propertyName)) {
                missing.add(propertyName);
            }
        }

        Assertions.assertTrue(missing.isEmpty());
    }

    private List<Property> collectJavaProperties() {
        List<Property> properties2 = new ArrayList<>();
        
            properties2.add(DetectPropertiesJava.BLACKDUCK_API_TOKEN);
            properties2.add(DetectPropertiesJava.BLACKDUCK_OFFLINE_MODE);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PASSWORD);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_HOST);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_IGNORED_HOSTS);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_NTLM_DOMAIN);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_NTLM_WORKSTATION);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_PASSWORD);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_PORT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_PROXY_USERNAME);
            properties2.add(DetectPropertiesJava.BLACKDUCK_TIMEOUT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_TRUST_CERT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_URL);
            properties2.add(DetectPropertiesJava.BLACKDUCK_USERNAME);
            properties2.add(DetectPropertiesJava.DETECT_BASH_PATH);
            properties2.add(DetectPropertiesJava.DETECT_BAZEL_CQUERY_OPTIONS);
            properties2.add(DetectPropertiesJava.DETECT_BAZEL_DEPENDENCY_RULE);
            properties2.add(DetectPropertiesJava.DETECT_BAZEL_PATH);
            properties2.add(DetectPropertiesJava.DETECT_BAZEL_TARGET);
            properties2.add(DetectPropertiesJava.DETECT_BDIO2_ENABLED);
            properties2.add(DetectPropertiesJava.DETECT_BDIO_OUTPUT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_BINARY_SCAN_FILE);
            properties2.add(DetectPropertiesJava.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_BITBAKE_BUILD_ENV_NAME);
            properties2.add(DetectPropertiesJava.DETECT_BITBAKE_PACKAGE_NAMES);
            properties2.add(DetectPropertiesJava.DETECT_BITBAKE_SEARCH_DEPTH);
            properties2.add(DetectPropertiesJava.DETECT_BITBAKE_SOURCE_ARGUMENTS);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_COPYRIGHT_SEARCH);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE);
            properties2.add(DetectPropertiesJava.DETECT_BOM_AGGREGATE_NAME);
            properties2.add(DetectPropertiesJava.DETECT_BOM_AGGREGATE_REMEDIATION_MODE);
            properties2.add(DetectPropertiesJava.DETECT_BUILDLESS);
            properties2.add(DetectPropertiesJava.DETECT_CLEANUP);
            properties2.add(DetectPropertiesJava.DETECT_CLONE_PROJECT_VERSION_LATEST);
            properties2.add(DetectPropertiesJava.DETECT_CLONE_PROJECT_VERSION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_CODE_LOCATION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_CONDA_ENVIRONMENT_NAME);
            properties2.add(DetectPropertiesJava.DETECT_CONDA_PATH);
            properties2.add(DetectPropertiesJava.DETECT_CPAN_PATH);
            properties2.add(DetectPropertiesJava.DETECT_CPANM_PATH);
            properties2.add(DetectPropertiesJava.DETECT_CUSTOM_FIELDS_PROJECT);
            properties2.add(DetectPropertiesJava.DETECT_CUSTOM_FIELDS_VERSION);
            properties2.add(DetectPropertiesJava.DETECT_DEFAULT_PROJECT_VERSION_SCHEME);
            properties2.add(DetectPropertiesJava.DETECT_DEFAULT_PROJECT_VERSION_TEXT);
            properties2.add(DetectPropertiesJava.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_CONTINUE);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_DEPTH);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_EXCLUSION);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_EXCLUSION_FILES);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_IMAGE);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_IMAGE_ID);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_INSPECTOR_PATH);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_INSPECTOR_VERSION);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_PATH);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_PATH_REQUIRED);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_PLATFORM_TOP_LAYER_ID);
            properties2.add(DetectPropertiesJava.DETECT_DOCKER_TAR);
            properties2.add(DetectPropertiesJava.DETECT_DOTNET_PATH);
            properties2.add(DetectPropertiesJava.DETECT_EXCLUDED_DETECTOR_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_FORCE_SUCCESS);
            properties2.add(DetectPropertiesJava.DETECT_GIT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_GO_PATH);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_BUILD_COMMAND);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_EXCLUDED_PROJECTS);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_INCLUDED_CONFIGURATIONS);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_INCLUDED_PROJECTS);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_INSPECTOR_VERSION);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_PATH);
            properties2.add(DetectPropertiesJava.DETECT_HEX_REBAR3_PATH);
            properties2.add(DetectPropertiesJava.DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS);
            properties2.add(DetectPropertiesJava.DETECT_IGNORE_CONNECTION_FAILURES);
            properties2.add(DetectPropertiesJava.DETECT_INCLUDED_DETECTOR_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_JAVA_PATH);
            properties2.add(DetectPropertiesJava.DETECT_LERNA_INCLUDE_PRIVATE);
            properties2.add(DetectPropertiesJava.DETECT_LERNA_PATH);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_BUILD_COMMAND);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_EXCLUDED_MODULES);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_EXCLUDED_SCOPES);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_INCLUDE_PLUGINS);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_INCLUDED_MODULES);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_INCLUDED_SCOPES);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_PATH);
            properties2.add(DetectPropertiesJava.DETECT_NOTICES_REPORT);
            properties2.add(DetectPropertiesJava.DETECT_NOTICES_REPORT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_NPM_ARGUMENTS);
            properties2.add(DetectPropertiesJava.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
            properties2.add(DetectPropertiesJava.DETECT_NPM_PATH);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_CONFIG_PATH);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_EXCLUDED_MODULES);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_IGNORE_FAILURE);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_INCLUDED_MODULES);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_INSPECTOR_VERSION);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_PACKAGES_REPO_URL);
            properties2.add(DetectPropertiesJava.DETECT_OUTPUT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES);
            properties2.add(DetectPropertiesJava.DETECT_PARALLEL_PROCESSORS);
            properties2.add(DetectPropertiesJava.DETECT_PARENT_PROJECT_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PARENT_PROJECT_VERSION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PEAR_ONLY_REQUIRED_DEPS);
            properties2.add(DetectPropertiesJava.DETECT_PEAR_PATH);
            properties2.add(DetectPropertiesJava.DETECT_PIP_ONLY_PROJECT_TREE);
            properties2.add(DetectPropertiesJava.DETECT_PIP_PROJECT_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PIP_PROJECT_VERSION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PIP_REQUIREMENTS_PATH);
            properties2.add(DetectPropertiesJava.DETECT_PIPENV_PATH);
            properties2.add(DetectPropertiesJava.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_APPLICATION_ID);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_CLONE_CATEGORIES);
            properties2.add(DetectPropertiesJava.DETECT_CODE_LOCATION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_CODELOCATION_PREFIX);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_CODELOCATION_SUFFIX);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_CODELOCATION_UNMAP);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_DESCRIPTION);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_DETECTOR);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_LEVEL_ADJUSTMENTS);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_TAGS);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_TIER);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_TOOL);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_USER_GROUPS);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_DISTRIBUTION);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_NAME);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_NICKNAME);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_NOTES);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_PHASE);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_VERSION_UPDATE);
            properties2.add(DetectPropertiesJava.DETECT_PYTHON_PATH);
            properties2.add(DetectPropertiesJava.DETECT_PYTHON_PYTHON3);
            properties2.add(DetectPropertiesJava.DETECT_REPORT_TIMEOUT);
            properties2.add(DetectPropertiesJava.DETECT_REQUIRED_DETECTOR_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_RESOLVE_TILDE_IN_PATHS);
            properties2.add(DetectPropertiesJava.DETECT_RISK_REPORT_PDF);
            properties2.add(DetectPropertiesJava.DETECT_RISK_REPORT_PDF_PATH);
            properties2.add(DetectPropertiesJava.DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES);
            properties2.add(DetectPropertiesJava.DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES);
            properties2.add(DetectPropertiesJava.DETECT_SBT_EXCLUDED_CONFIGURATIONS);
            properties2.add(DetectPropertiesJava.DETECT_SBT_INCLUDED_CONFIGURATIONS);
            properties2.add(DetectPropertiesJava.DETECT_SBT_REPORT_DEPTH);
            properties2.add(DetectPropertiesJava.DETECT_SCAN_OUTPUT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_SOURCE_PATH);
            properties2.add(DetectPropertiesJava.DETECT_SWIFT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_TEST_CONNECTION);
            properties2.add(DetectPropertiesJava.DETECT_TOOLS);
            properties2.add(DetectPropertiesJava.DETECT_TOOLS_EXCLUDED);
            properties2.add(DetectPropertiesJava.DETECT_TOOLS_OUTPUT_PATH);
            properties2.add(DetectPropertiesJava.DETECT_WAIT_FOR_RESULTS);
            properties2.add(DetectPropertiesJava.DETECT_YARN_PROD_ONLY);
            properties2.add(DetectPropertiesJava.DOCKER_PASSTHROUGH);
            properties2.add(DetectPropertiesJava.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION);
            properties2.add(DetectPropertiesJava.LOGGING_LEVEL_DETECT);
            properties2.add(DetectPropertiesJava.PHONEHOME_PASSTHROUGH);
            properties2.add(DetectPropertiesJava.DETECT_API_TIMEOUT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_API_TOKEN);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_OFFLINE_MODE);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PASSWORD);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_HOST);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_IGNORED_HOSTS);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_NTLM_DOMAIN);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_PASSWORD);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_PORT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_PROXY_USERNAME);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_TIMEOUT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_TRUST_CERT);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_URL);
            properties2.add(DetectPropertiesJava.BLACKDUCK_HUB_USERNAME);
            properties2.add(DetectPropertiesJava.DETECT_BITBAKE_REFERENCE_IMPL);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE);
            properties2.add(DetectPropertiesJava.DETECT_BOM_TOOL_SEARCH_CONTINUE);
            properties2.add(DetectPropertiesJava.DETECT_BOM_TOOL_SEARCH_DEPTH);
            properties2.add(DetectPropertiesJava.DETECT_BOM_TOOL_SEARCH_EXCLUSION);
            properties2.add(DetectPropertiesJava.DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS);
            properties2.add(DetectPropertiesJava.DETECT_EXCLUDED_BOM_TOOL_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_INCLUDED_BOM_TOOL_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_REQUIRED_BOM_TOOL_TYPES);
            properties2.add(DetectPropertiesJava.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS);
            properties2.add(DetectPropertiesJava.DETECT_DISABLE_WITHOUT_BLACKDUCK);
            properties2.add(DetectPropertiesJava.DETECT_DISABLE_WITHOUT_HUB);
            properties2.add(DetectPropertiesJava.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_DISABLED);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_MEMORY);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_PATHS);
            properties2.add(DetectPropertiesJava.DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE);
            properties2.add(DetectPropertiesJava.DETECT_MAVEN_SCOPE);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_INSPECTOR_NAME);
            properties2.add(DetectPropertiesJava.DETECT_NUGET_PATH);
            properties2.add(DetectPropertiesJava.DETECT_PROJECT_BOM_TOOL);
            properties2.add(DetectPropertiesJava.DETECT_SUPPRESS_CONFIGURATION_OUTPUT);
            properties2.add(DetectPropertiesJava.DETECT_SUPPRESS_RESULTS_OUTPUT);
            properties2.add(DetectPropertiesJava.DETECT_SWIP_ENABLED);
            properties2.add(DetectPropertiesJava.LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION);
            properties2.add(DetectPropertiesJava.POLARIS_ACCESS_TOKEN);
            properties2.add(DetectPropertiesJava.POLARIS_ARGUMENTS);
            properties2.add(DetectPropertiesJava.POLARIS_COMMAND);
            properties2.add(DetectPropertiesJava.POLARIS_URL);

        return properties2;
    }
}
