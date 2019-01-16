/**
 * detect-configuration
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.configuration;

import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_BAZEL;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_BITBAKE;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_BLACKDUCK_CONFIGURATION;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_BOMTOOL;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_CLEANUP;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_CONDA;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_CPAN;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_DETECTOR;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_DOCKER;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_GENERAL;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_GO;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_GRADLE;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_HEX;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_HUB_CONFIGURATION;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_LOGGING;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_MAVEN;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_NPM;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_NUGET;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PACKAGIST;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PATHS;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PEAR;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PIP;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_POLARIS;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_POLICY_CHECK;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PROJECT_INFO;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_PYTHON;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_SBT;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_SIGNATURE_SCANNER;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.GROUP_YARN;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_BLACKDUCK;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_DEBUG;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_HUB;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_OFFLINE;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_POLICY;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_PROJECT;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_PROXY;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_SEARCH;
import static com.blackducksoftware.integration.hub.detect.configuration.DetectProperty.PropertyConstants.SEARCH_GROUP_SIGNATURE_SCANNER;

import com.blackducksoftware.integration.hub.detect.DetectMajorVersion;
import com.blackducksoftware.integration.hub.detect.help.AcceptableValues;
import com.blackducksoftware.integration.hub.detect.help.DetectDeprecation;
import com.blackducksoftware.integration.hub.detect.help.HelpDescription;
import com.blackducksoftware.integration.hub.detect.help.HelpDetailed;
import com.blackducksoftware.integration.hub.detect.help.HelpGroup;
import com.blackducksoftware.integration.hub.detect.property.PropertyType;

public enum DetectProperty {

    @HelpGroup(primary = GROUP_GENERAL)
    @HelpDescription("If true, detect will always exit with code 0.")
    DETECT_FORCE_SUCCESS("detect.force.success", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is being removed. Configuration can no longer be suppressed individually. Log level can be used.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing your configuration properties at startup will be suppressed.")
    DETECT_SUPPRESS_CONFIGURATION_OUTPUT("detect.suppress.configuration.output", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is being removed. Results can no longer be suppressed individually. Log level can be used.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing the Detect Results will be suppressed.")
    DETECT_SUPPRESS_RESULTS_OUTPUT("detect.suppress.results.output", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_CLEANUP)
    @HelpDescription("If true the files created by Detect will be cleaned up.")
    DETECT_CLEANUP("detect.cleanup", "3.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Test the connection to Black Duck with the current configuration")
    DETECT_TEST_CONNECTION("detect.test.connection", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("Timeout for response from Black Duck regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.")
    DETECT_API_TIMEOUT("detect.api.timeout", "3.0.0", PropertyType.LONG, PropertyAuthority.None, "300000"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.url in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("URL of the Hub server")
    BLACKDUCK_HUB_URL("blackduck.hub.url", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("URL of the Black Duck server")
    BLACKDUCK_URL("blackduck.url", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.timeout in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Time to wait for rest connections to complete")
    BLACKDUCK_HUB_TIMEOUT("blackduck.hub.timeout", "3.0.0", PropertyType.INTEGER, PropertyAuthority.None, "120"),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Time to wait for rest connections to complete")
    BLACKDUCK_TIMEOUT("blackduck.timeout", "4.2.0", PropertyType.INTEGER, PropertyAuthority.None, "120"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.username in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub username")
    BLACKDUCK_HUB_USERNAME("blackduck.hub.username", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Black Duck username")
    BLACKDUCK_USERNAME("blackduck.username", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.password in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub password")
    BLACKDUCK_HUB_PASSWORD("blackduck.hub.password", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Black Duck password")
    BLACKDUCK_PASSWORD("blackduck.password", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.api.token in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub API Token")
    BLACKDUCK_HUB_API_TOKEN("blackduck.hub.api.token", "3.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Black Duck API Token")
    BLACKDUCK_API_TOKEN("blackduck.api.token", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.host in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy host")
    BLACKDUCK_HUB_PROXY_HOST("blackduck.hub.proxy.host", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy host")
    BLACKDUCK_PROXY_HOST("blackduck.proxy.host", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.port in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy port")
    BLACKDUCK_HUB_PROXY_PORT("blackduck.hub.proxy.port", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy port")
    BLACKDUCK_PROXY_PORT("blackduck.proxy.port", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.username in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy username")
    BLACKDUCK_HUB_PROXY_USERNAME("blackduck.hub.proxy.username", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy username")
    BLACKDUCK_PROXY_USERNAME("blackduck.proxy.username", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.password in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy password")
    BLACKDUCK_HUB_PROXY_PASSWORD("blackduck.hub.proxy.password", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy password")
    BLACKDUCK_PROXY_PASSWORD("blackduck.proxy.password", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ntlm.domain in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy domain")
    BLACKDUCK_HUB_PROXY_NTLM_DOMAIN("blackduck.hub.proxy.ntlm.domain", "3.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy domain")
    BLACKDUCK_PROXY_NTLM_DOMAIN("blackduck.proxy.ntlm.domain", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ignored.hosts in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Comma separated list of host patterns that should not use the proxy")
    BLACKDUCK_HUB_PROXY_IGNORED_HOSTS("blackduck.hub.proxy.ignored.hosts", "3.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Comma separated list of host patterns that should not use the proxy")
    BLACKDUCK_PROXY_IGNORED_HOSTS("blackduck.proxy.ignored.hosts", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ntlm.workstation in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy workstation")
    BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION("blackduck.hub.proxy.ntlm.workstation", "3.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy workstation")
    BLACKDUCK_PROXY_NTLM_WORKSTATION("blackduck.proxy.ntlm.workstation", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.trust.cert in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, automatically trust the certificate for the current run of Detect only")
    BLACKDUCK_HUB_TRUST_CERT("blackduck.hub.trust.cert", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("If true, automatically trust the certificate for the current run of Detect only")
    BLACKDUCK_TRUST_CERT("blackduck.trust.cert", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.offline.mode in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_OFFLINE })
    @HelpDescription("This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
    BLACKDUCK_HUB_OFFLINE_MODE("blackduck.hub.offline.mode", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK, SEARCH_GROUP_OFFLINE })
    @HelpDescription("This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
    BLACKDUCK_OFFLINE_MODE("blackduck.offline.mode", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.disable.without.blackduck in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.")
    DETECT_DISABLE_WITHOUT_HUB("detect.disable.without.hub", "4.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_BLACKDUCK_CONFIGURATION, additional = { SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("If true, during initialization Detect will check for Black Duck connectivity and exit with status code 0 if it cannot connect.")
    DETECT_DISABLE_WITHOUT_BLACKDUCK("detect.disable.without.blackduck", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("If set to false we will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.")
    DETECT_RESOLVE_TILDE_IN_PATHS("detect.resolve.tilde.in.paths", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Source path to inspect")
    DETECT_SOURCE_PATH("detect.source.path", "3.0.0", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Output path")
    DETECT_OUTPUT_PATH("detect.output.path", "3.0.0", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("The output directory for all bdio files. If not set, the bdio files will be in a 'bdio' subdirectory of the output path.")
    DETECT_BDIO_OUTPUT_PATH("detect.bdio.output.path", "3.0.0", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    @HelpGroup(primary = GROUP_PATHS, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The output directory for all scan files. If not set, the scan files will be in a 'scan' subdirectory of the output path.")
    DETECT_SCAN_OUTPUT_PATH("detect.scan.output.path", "3.0.0", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    @HelpGroup(primary = GROUP_SBT, additional = { GROUP_PATHS })
    @HelpDescription("Depth the sbt detector will use to search for report files.")
    DETECT_SBT_REPORT_DEPTH("detect.sbt.report.search.depth", "4.3.0", PropertyType.INTEGER, PropertyAuthority.None, "3"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.sbt.report.depth in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Depth from source paths to search for sbt report files.")
    DETECT_SEARCH_DEPTH("detect.search.depth", "3.0.0", PropertyType.INTEGER, PropertyAuthority.None, "3"),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("The tool priority for project name and version. The first tool in this list that provides a project name and version will be used.")
    @AcceptableValues(value = { "DETECTOR", "DOCKER", "BAZEL" }, caseSensitive = true, strict = true, isCommaSeparatedList = true)
    DETECT_PROJECT_TOOL("detect.project.tool", "5.0.0", PropertyType.STRING, PropertyAuthority.None, "DOCKER,DETECTOR,BAZEL"),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("The tools detect should allow in a comma-separated list. Included and not excluded tools will be allowed to run if all criteria of the tool is met. Exclusion rules always win.")
    @AcceptableValues(value = { "DETECTOR", "DOCKER", "SIGNATURE_SCAN", "BINARY_SCAN", "POLARIS", "NONE", "ALL" }, caseSensitive = true, strict = false, isCommaSeparatedList = true)
    DETECT_TOOLS("detect.tools", "5.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("The tools detect should not allow in a comma-separated list. Excluded tools will not be run even if all criteria for the tool is met. Exclusion rules always win.")
    @AcceptableValues(value = { "BAZEL", "DETECTOR", "DOCKER", "SIGNATURE_SCAN", "BINARY_SCAN", "POLARIS", "NONE", "ALL" }, caseSensitive = true, strict = false, isCommaSeparatedList = true)
    DETECT_TOOLS_EXCLUDED("detect.tools.excluded", "5.0.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.project.detector in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.")
    DETECT_PROJECT_BOM_TOOL("detect.project.bom.tool", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_DETECTOR, SEARCH_GROUP_SEARCH })
    @HelpDescription("The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.")
    DETECT_PROJECT_DETECTOR("detect.project.detector", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.depth in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("Depth from source paths to search for files to determine if a bom tool applies.")
    DETECT_BOM_TOOL_SEARCH_DEPTH("detect.bom.tool.search.depth", "3.2.0", PropertyType.INTEGER, PropertyAuthority.None, "0"),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_DETECTOR, SEARCH_GROUP_SEARCH })
    @HelpDescription("Depth from source paths to search for files to determine if a detector applies.")
    DETECT_DETECTOR_SEARCH_DEPTH("detect.detector.search.depth", "3.2.0", PropertyType.INTEGER, PropertyAuthority.None, "0"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.required.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_BOMTOOL, additional = { GROUP_BOMTOOL })
    @HelpDescription("If set, detect will fail if it does not find the bom tool types supplied here.")
    DETECT_REQUIRED_BOM_TOOL_TYPES("detect.required.bom.tool.types", "4.3.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DETECTOR, additional = { GROUP_DETECTOR })
    @HelpDescription("If set, detect will fail if it does not find the detector types supplied here.")
    DETECT_REQUIRED_DETECTOR_TYPES("detect.required.detector.types", "4.3.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.continue in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.")
    @HelpDetailed("If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc.\r\nIf false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.")
    DETECT_BOM_TOOL_SEARCH_CONTINUE("detect.bom.tool.search.continue", "3.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_DETECTOR, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.")
    @HelpDetailed("If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc.\r\nIf false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.")
    DETECT_DETECTOR_SEARCH_CONTINUE("detect.detector.search.continue", "3.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.exclusion in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("A comma-separated list of directory names to exclude from the bom tool search.")
    DETECT_BOM_TOOL_SEARCH_EXCLUSION("detect.bom.tool.search.exclusion", "3.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_DETECTOR, SEARCH_GROUP_SEARCH })
    @HelpDescription("A comma-separated list of directory names to exclude from the bom tool search.")
    DETECT_DETECTOR_SEARCH_EXCLUSION("detect.detector.search.exclusion", "3.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.exclusion.defaults in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.")
    @HelpDetailed("If true, these directories will be excluded from the bom tool search: " + DetectorSearchExcludedDirectories.DIRECTORY_NAMES)
    DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS("detect.bom.tool.search.exclusion.defaults", "3.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_DETECTOR, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.")
    @HelpDetailed("If true, these directories will be excluded from the detector search: " + DetectorSearchExcludedDirectories.DIRECTORY_NAMES)
    DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS("detect.detector.search.exclusion.defaults", "3.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.excluded.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_BOMTOOL, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.")
    DETECT_EXCLUDED_BOM_TOOL_TYPES("detect.excluded.bom.tool.types", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DETECTOR, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.")
    DETECT_EXCLUDED_DETECTOR_TYPES("detect.excluded.detector.types", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.included.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_BOMTOOL, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
    DETECT_INCLUDED_BOM_TOOL_TYPES("detect.included.bom.tool.types", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DETECTOR, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
    DETECT_INCLUDED_DETECTOR_TYPES("detect.included.detector.types", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the name detect will use for the code location it creates. If supplied and multiple code locations are found, detect will append an index to each code location name.")
    DETECT_CODE_LOCATION_NAME("detect.code.location.name", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the name to use for the Black Duck project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
    DETECT_PROJECT_NAME("detect.project.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project description is specified, your project version will be created with this description.")
    DETECT_PROJECT_DESCRIPTION("detect.project.description", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the version to use for the Black Duck project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
    DETECT_PROJECT_VERSION_NAME("detect.project.version.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If a project version nickname is specified, your project version will be created with this nickname.")
    DETECT_PROJECT_VERSION_NICKNAME("detect.project.version.nickname", "5.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project version notes are specified, your project version will be created with these notes.")
    DETECT_PROJECT_VERSION_NOTES("detect.project.version.notes", "3.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If a Black Duck project tier is specified, your project will be created with this tier.")
    @AcceptableValues(value = { "1", "2", "3", "4", "5" }, caseSensitive = false, strict = false)
    DETECT_PROJECT_TIER("detect.project.tier", "3.1.0", PropertyType.INTEGER, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.")
    DETECT_PROJECT_CODELOCATION_PREFIX("detect.project.codelocation.prefix", "3.0.0", PropertyType.STRING, PropertyAuthority.None, ""),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A suffix to the name of the codelocations created by Detect.")
    DETECT_PROJECT_CODELOCATION_SUFFIX("detect.project.codelocation.suffix", "3.0.0", PropertyType.STRING, PropertyAuthority.None, ""),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, unmaps all other code locations mapped to the project version produced by the current run of Detect.")
    DETECT_PROJECT_CODELOCATION_UNMAP("detect.project.codelocation.unmap", "4.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project level matches.")
    DETECT_PROJECT_LEVEL_ADJUSTMENTS("detect.project.level.adjustments", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version phase.")
    @AcceptableValues(value = { "PLANNING", "DEVELOPMENT", "RELEASED", "DEPRECATED", "ARCHIVED" }, caseSensitive = false, strict = false)
    DETECT_PROJECT_VERSION_PHASE("detect.project.version.phase", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "Development"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Clone Categories that are used when cloning a version. If the project already exists, make sure to use --detect.project.version.update to make sure these are set.")
    @AcceptableValues(value = { "COMPONENT_DATA", "VULN_DATA" }, caseSensitive = false, strict = false, isCommaSeparatedList = true)
    DETECT_PROJECT_CLONE_CATEGORIES("detect.project.clone.categories", "4.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None, "COMPONENT_DATA,VULN_DATA"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The name of the project version to clone this project version from. Respects the Clone Categories as set on the Black Duck server.")
    DETECT_CLONE_PROJECT_VERSION_NAME("detect.clone.project.version.name", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version distribution")
    @AcceptableValues(value = { "EXTERNAL", "SAAS", "INTERNAL", "OPENSOURCE" }, caseSensitive = false, strict = false)
    DETECT_PROJECT_VERSION_DISTRIBUTION("detect.project.version.distribution", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "External"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, will update the Project Version with the configured properties. See detailed help for more information.")
    @HelpDetailed("When set to true, the following properties will be updated on the Project. Project tier (detect.project.tier) and Project Level Adjustments (detect.project.level.adjustments).\r\n The following properties will also be updated on the Version. Version notes (detect.project.version.notes), phase (detect.project.version.phase), distribution (detect.project.version.distribution)")
    DETECT_PROJECT_VERSION_UPDATE("detect.project.version.update", "4.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("Sets the 'Application ID' project setting")
    DETECT_PROJECT_APPLICATION_ID("detect.project.application.id", "5.2.0", PropertyType.STRING, PropertyAuthority.None, null),

    @HelpGroup(primary = GROUP_POLICY_CHECK, additional = { SEARCH_GROUP_POLICY })
    @HelpDescription("A comma-separated list of policy violation severities that will fail detect. If this is not set, detect will not fail due to policy violations.")
    @AcceptableValues(value = { "ALL", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL" }, caseSensitive = false, strict = false, isCommaSeparatedList = true)
    DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES("detect.policy.check.fail.on.severities", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The override version of the Gradle Inspector to use. By default, detect will try to automatically determine the correct gradle version.")
    DETECT_GRADLE_INSPECTOR_VERSION("detect.gradle.inspector.version", "3.0.0", PropertyType.STRING, PropertyAuthority.None, ""),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The path to the directory containing the air gap dependencies for the gradle inspector")
    DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH("detect.gradle.inspector.air.gap.path", "3.0.0", PropertyType.STRING, PropertyAuthority.AirGapManager),

    @Deprecated
    @DetectDeprecation(description = "In the future, the gradle inspector will no longer be downloaded from a custom repository, please use Detect Air Gap instead.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The respository gradle should use to look for the gradle inspector dependencies")
    DETECT_GRADLE_INSPECTOR_REPOSITORY_URL("detect.gradle.inspector.repository.url", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Gradle build command")
    DETECT_GRADLE_BUILD_COMMAND("detect.gradle.build.command", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to exclude")
    DETECT_GRADLE_EXCLUDED_CONFIGURATIONS("detect.gradle.excluded.configurations", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to include")
    DETECT_GRADLE_INCLUDED_CONFIGURATIONS("detect.gradle.included.configurations", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to exclude")
    DETECT_GRADLE_EXCLUDED_PROJECTS("detect.gradle.excluded.projects", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to include")
    DETECT_GRADLE_INCLUDED_PROJECTS("detect.gradle.included.projects", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path to the Nuget.Config file to supply to the nuget exe")
    DETECT_NUGET_CONFIG_PATH("detect.nuget.config.path", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "In the future, detect will not look for a custom named inspector.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Name of the Nuget Inspector package and the Nuget Inspector exe. (Do not include .exe)")
    @HelpDetailed("The nuget inspector (previously) could be hosted on a custom nuget feed. In this case, detect needed to know the name of the package to pull and the name of the exe file (which has to match). In the future, detect will only retreive it from Artifactory or from Air Gap so a custom name is no longer supported.")
    DETECT_NUGET_INSPECTOR_NAME("detect.nuget.inspector.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "IntegrationNugetInspector"),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Version of the Nuget Inspector. By default detect will communicate with Artifactory.")
    DETECT_NUGET_INSPECTOR_VERSION("detect.nuget.inspector.version", "3.0.0", PropertyType.STRING, PropertyAuthority.None, ""),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to exclude")
    DETECT_NUGET_EXCLUDED_MODULES("detect.nuget.excluded.modules", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to include (overrides exclude)")
    DETECT_NUGET_INCLUDED_MODULES("detect.nuget.included.modules", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("If true errors will be logged and then ignored.")
    DETECT_NUGET_IGNORE_FAILURE("detect.nuget.ignore.failure", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The name of the dependency scope to include")
    DETECT_MAVEN_SCOPE("detect.maven.scope", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("Maven build command")
    DETECT_MAVEN_BUILD_COMMAND("detect.maven.build.command", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Path of the Gradle executable")
    DETECT_GRADLE_PATH("detect.gradle.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The path of the Maven executable")
    DETECT_MAVEN_PATH("detect.maven.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to exclude")
    DETECT_MAVEN_EXCLUDED_MODULES("detect.maven.excluded.modules", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to include")
    DETECT_MAVEN_INCLUDED_MODULES("detect.maven.included.modules", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BAZEL)
    @HelpDescription("The path of the Bazel executable")
    DETECT_BAZEL_PATH("detect.bazel.path", "5.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BAZEL)
    @HelpDescription("The bazel target (e.g. //foo:foolib) to collect dependencies for. For detect to run bazel this property must be set.")
    DETECT_BAZEL_TARGET("detect.bazel.target", "5.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_BAZEL)
    @HelpDescription("The path to a file containing a list of BazelExternalIdExtractionXPathRule objects in json (to override the default behavior)")
    DETECT_BAZEL_ADVANCED_RULES_PATH("detect.bazel.advanced.rules.path", "5.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "In the future, detect will no longer need a nuget executable as it will download the inspector from Artifactory exclusively.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path of the Nuget executable. Nuget is used to download the classic inspectors nuget package.")
    DETECT_NUGET_PATH("detect.nuget.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path of the dotnet executable")
    DETECT_DOTNET_PATH("detect.dotnet.path", "4.4.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PIP)
    @DetectDeprecation(description = "This property is being removed. Please use --detect.project.name in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)
    @HelpDescription("The name of your bitbake project, to be used if your project's name cannot be correctly inferred from its setup.py file")
    DETECT_PIP_PROJECT_NAME("detect.bitbake.project.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PIP)
    @DetectDeprecation(description = "This property is being removed. Please use --detect.project.version.name in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)
    @HelpDescription("The version of your bitbake project, to be used if your project's version name cannot be correctly inferred from its setup.py file")
    DETECT_PIP_PROJECT_VERSION_NAME("detect.bitbake.project.version.name", "4.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("If true will use Python 3 if available on class path")
    DETECT_PYTHON_PYTHON3("detect.python.python3", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("The path of the Python executable")
    DETECT_PYTHON_PATH("detect.python.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("The path of the Pipenv executable")
    DETECT_PIPENV_PATH("detect.pipenv.path", "4.1.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("A space-separated list of additional arguments to use when running Detect against an NPM project")
    DETECT_NPM_ARGUMENTS("detect.npm.arguments", "4.3.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the Npm executable")
    DETECT_NPM_PATH("detect.npm.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("Set this value to false if you would like to exclude your dev dependencies when ran")
    DETECT_NPM_INCLUDE_DEV_DEPENDENCIES("detect.npm.include.dev.dependencies", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the node executable that is used by Npm")
    DETECT_NPM_NODE_PATH("detect.npm.node.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("The path of the pear executable")
    DETECT_PEAR_PATH("detect.pear.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("Set to true if you would like to include only required packages")
    DETECT_PEAR_ONLY_REQUIRED_DEPS("detect.pear.only.required.deps", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("The path of the requirements.txt file")
    DETECT_PIP_REQUIREMENTS_PATH("detect.bitbake.requirements.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("Path of the Go Dep executable")
    DETECT_GO_DEP_PATH("detect.go.dep.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("If set to true, we will attempt to run 'init' and 'ensure' which can modify your development environment.")
    DETECT_GO_RUN_DEP_INIT("detect.go.run.dep.init", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Path of the docker executable")
    DETECT_DOCKER_PATH("detect.docker.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("If set to false, detect will attempt to run docker even if it cannot find a docker path.")
    DETECT_DOCKER_PATH_REQUIRED("detect.docker.path.required", "4.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("This is used to override using the hosted script by github url. You can provide your own script at this path.")
    DETECT_DOCKER_INSPECTOR_PATH("detect.docker.inspector.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Version of the Docker Inspector to use. By default detect will attempt to automatically determine the version to use.")
    DETECT_DOCKER_INSPECTOR_VERSION("detect.docker.inspector.version", "3.0.0", PropertyType.STRING, PropertyAuthority.None, ""),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.")
    DETECT_DOCKER_TAR("detect.docker.tar", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.")
    DETECT_DOCKER_IMAGE("detect.docker.image", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Path of the bash executable")
    DETECT_BASH_PATH("detect.bash.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Path of the java executable")
    DETECT_JAVA_PATH("detect.java.path", "5.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_LOGGING, additional = { GROUP_LOGGING, SEARCH_GROUP_DEBUG })
    @HelpDescription("The logging level of Detect")
    @AcceptableValues(value = { "ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF" }, caseSensitive = false, strict = true)
    LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION("logging.level.com.blackducksoftware.integration", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "INFO"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.dry.run in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.")
    DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN("detect.hub.signature.scanner.dry.run", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("If set to true, the signature scanner results will not be uploaded to Black Duck and the scanner results will be written to disk.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN("detect.blackduck.signature.scanner.dry.run", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.snippet.mode in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.")
    DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE("detect.hub.signature.scanner.snippet.mode", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("If set to true, the signature scanner will, if supported by your Black Duck version, run in snippet scanning mode.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE("detect.blackduck.signature.scanner.snippet.mode", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.patterns in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("A comma-separated list of values to be used with the Signature Scanner --exclude flag.")
    DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS("detect.hub.signature.scanner.exclusion.patterns", "3.0.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("A comma-separated list of values to be used with the Signature Scanner --exclude flag.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS("detect.blackduck.signature.scanner.exclusion.patterns", "4.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Enables you to adjust the depth to which detect will search when creating signature scanner exclusion patterns")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH("detect.blackduck.signature.scanner.exclusion.pattern.search.depth", "5.0.0", PropertyType.INTEGER, PropertyAuthority.None, "4"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.paths in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("These paths and only these paths will be scanned.")
    DETECT_HUB_SIGNATURE_SCANNER_PATHS("detect.hub.signature.scanner.paths", "3.0.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("These paths and only these paths will be scanned.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS("detect.blackduck.signature.scanner.paths", "4.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.name.patterns in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("A comma-separated list of directory name patterns detect will search for and add to the Signature Scanner --exclude flag values.")
    @HelpDetailed("Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner.\r\nThese patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns")
    DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS("detect.hub.signature.scanner.exclusion.name.patterns", "4.0.0", PropertyType.STRING_ARRAY, PropertyAuthority.None, "node_modules"),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("A comma-separated list of directory name patterns detect will search for and add to the Signature Scanner --exclude flag values.")
    @HelpDetailed("Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner.\r\nThese patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS("detect.blackduck.signature.scanner.exclusion.name.patterns", "4.2.0", PropertyType.STRING_ARRAY, PropertyAuthority.None, "node_modules"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.memory in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The memory for the scanner to use.")
    DETECT_HUB_SIGNATURE_SCANNER_MEMORY("detect.hub.signature.scanner.memory", "3.0.0", PropertyType.INTEGER, PropertyAuthority.None, "4096"),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("The memory for the scanner to use.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY("detect.blackduck.signature.scanner.memory", "4.2.0", PropertyType.INTEGER, PropertyAuthority.None, "4096"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_DEBUG, SEARCH_GROUP_HUB })
    @HelpDescription("Set to true to disable the Hub Signature Scanner.")
    DETECT_HUB_SIGNATURE_SCANNER_DISABLED("detect.hub.signature.scanner.disabled", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_DEBUG, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Set to true to disable the Black Duck Signature Scanner.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED("detect.blackduck.signature.scanner.disabled", "4.2.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.offline.local.path in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_HUB })
    @HelpDescription("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
    DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH("detect.hub.signature.scanner.offline.local.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH("detect.blackduck.signature.scanner.offline.local.path", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.local.path in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_HUB })
    @HelpDescription("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
    DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH("detect.hub.signature.scanner.local.path", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH("detect.blackduck.signature.scanner.local.path", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.host.url in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.")
    DETECT_HUB_SIGNATURE_SCANNER_HOST_URL("detect.hub.signature.scanner.host.url", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Black Duck's urls for different operating systems.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL("detect.blackduck.signature.scanner.host.url", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.parallel.processors in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
    DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS("detect.hub.signature.scanner.parallel.processors", "3.0.0", PropertyType.INTEGER, PropertyAuthority.None, "1"),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS("detect.blackduck.signature.scanner.parallel.processors", "4.2.0", PropertyType.INTEGER, PropertyAuthority.None, "1"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.arguments in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("Additional arguments to use when running the Hub signature scanner.")
    DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS("detect.hub.signature.scanner.arguments", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("Additional arguments to use when running the Black Duck signature scanner.")
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS("detect.blackduck.signature.scanner.arguments", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_BLACKDUCK })
    @HelpDescription("The path of a binary file to scan.")
    DETECT_BINARY_SCAN_FILE("detect.binary.scan.file.path", "4.2.0", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools and POLARIS in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @HelpGroup(primary = GROUP_POLARIS)
    @HelpDescription("Set to false to disable the Synopsys Polaris Tool.")
    DETECT_SWIP_ENABLED("detect.polaris.enabled", "4.4.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PACKAGIST)
    @HelpDescription("Set this value to false if you would like to exclude your dev requires dependencies when ran")
    DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES("detect.packagist.include.dev.dependencies", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the perl executable")
    DETECT_PERL_PATH("detect.perl.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpan executable")
    DETECT_CPAN_PATH("detect.cpan.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpanm executable")
    DETECT_CPANM_PATH("detect.cpanm.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to exclude")
    DETECT_SBT_EXCLUDED_CONFIGURATIONS("detect.sbt.excluded.configurations", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to include")
    DETECT_SBT_INCLUDED_CONFIGURATIONS("detect.sbt.included.configurations", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'")
    DETECT_DEFAULT_PROJECT_VERSION_SCHEME("detect.default.project.version.scheme", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "text"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The text to use as the default project version")
    DETECT_DEFAULT_PROJECT_VERSION_TEXT("detect.default.project.version.text", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "Default Detect Version"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The timestamp format to use as the default project version")
    DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT("detect.default.project.version.timeformat", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "yyyy-MM-dd\\'T\\'HH:mm:ss.SSS"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.")
    DETECT_BOM_AGGREGATE_NAME("detect.bom.aggregate.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck risk report in PDF form will be created")
    DETECT_RISK_REPORT_PDF("detect.risk.report.pdf", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for risk report in PDF. Default is the source directory")
    DETECT_RISK_REPORT_PDF_PATH("detect.risk.report.pdf.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "."),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck notices report in text form will be created in your source directory")
    DETECT_NOTICES_REPORT("detect.notices.report", "3.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for notices report. Default is the source directory")
    DETECT_NOTICES_REPORT_PATH("detect.notices.report.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None, "."),

    @HelpGroup(primary = GROUP_BITBAKE)
    @HelpDescription("The name of the build environment init script")
    DETECT_BITBAKE_BUILD_ENV_NAME("detect.bitbake.build.env.name", "4.4.0", PropertyType.STRING, PropertyAuthority.None, "oe-init-build-env"),

    @HelpGroup(primary = GROUP_BITBAKE)
    @HelpDescription("A comma separated list of package names to extract dependencies from")
    DETECT_BITBAKE_PACKAGE_NAMES("detect.bitbake.package.names", "4.4.0", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The path of the conda executable")
    DETECT_CONDA_PATH("detect.conda.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The name of the anaconda environment used by your project")
    DETECT_CONDA_ENVIRONMENT_NAME("detect.conda.environment.name", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The path to the directory containing the docker inspector script, jar, and images")
    DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH("detect.docker.inspector.air.gap.path", "3.0.0", PropertyType.STRING, PropertyAuthority.AirGapManager),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path to the directory containing the nuget inspector nupkg")
    DETECT_NUGET_INSPECTOR_AIR_GAP_PATH("detect.nuget.inspector.air.gap.path", "3.0.0", PropertyType.STRING, PropertyAuthority.AirGapManager),

    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The source for nuget packages")
    @HelpDetailed("Set this to \"https://www.nuget.org/api/v2/\" if your are still using a nuget client expecting the v2 api")
    DETECT_NUGET_PACKAGES_REPO_URL("detect.nuget.packages.repo.url", "3.0.0", PropertyType.STRING_ARRAY, PropertyAuthority.None, "https://api.nuget.org/v3/index.json"),

    @HelpGroup(primary = GROUP_HEX)
    @HelpDescription("The path of the rebar3 executable")
    DETECT_HEX_REBAR3_PATH("detect.hex.rebar3.path", "3.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpDescription("The path of the Yarn executable")
    @HelpGroup(primary = GROUP_YARN)
    DETECT_YARN_PATH("detect.yarn.path", "4.0.0", PropertyType.STRING, PropertyAuthority.None),

    @HelpDescription("Set this to true to only scan production dependencies")
    @HelpGroup(primary = GROUP_YARN)
    DETECT_YARN_PROD_ONLY("detect.yarn.prod.only", "4.0.0", PropertyType.BOOLEAN, PropertyAuthority.None, "false");

    private final String propertyName;
    private final PropertyType propertyType;
    private final String defaultValue;
    private final String asOf;
    private final PropertyAuthority propertyAuthority;

    DetectProperty(final String propertyName, final String asOf, final PropertyType propertyType, final PropertyAuthority propertyAuthority) {
        this(propertyName, asOf, propertyType, propertyAuthority, null);
    }

    DetectProperty(final String propertyName, final String asOf, final PropertyType propertyType, final PropertyAuthority propertyAuthority, final String defaultValue) {
        this.propertyName = propertyName;
        this.asOf = asOf;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
        this.propertyAuthority = propertyAuthority;
    }

    public PropertyAuthority getPropertyAuthority() {
        return propertyAuthority;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getAddedInVersion() {
        return asOf;
    }

    public Boolean isEqualToDefault(final String value) {
        String defaultValue = "";
        if (null != getDefaultValue()) {
            defaultValue = getDefaultValue();
        }
        return value.equals(defaultValue);
    }

    public final class PropertyConstants {
        @Deprecated
        public static final String GROUP_BOMTOOL = "detector";
        @Deprecated
        public static final String GROUP_HUB_CONFIGURATION = "hub configuration";

        public static final String GROUP_BLACKDUCK_CONFIGURATION = "blackduck configuration";
        public static final String GROUP_GENERAL = "general";
        public static final String GROUP_LOGGING = "logging";
        public static final String GROUP_CLEANUP = "cleanup";
        public static final String GROUP_PATHS = "paths";
        public static final String GROUP_CODELOCATION = "codelocation";
        public static final String GROUP_DETECTOR = "detector";
        public static final String GROUP_BITBAKE = "bitbake";
        public static final String GROUP_CONDA = "conda";
        public static final String GROUP_CPAN = "cpan";
        public static final String GROUP_DOCKER = "docker";
        public static final String GROUP_GO = "go";
        public static final String GROUP_GRADLE = "gradle";
        public static final String GROUP_ARTIFACTORY = "artifactory";
        public static final String GROUP_HEX = "hex";
        public static final String GROUP_MAVEN = "maven";
        public static final String GROUP_NPM = "npm";
        public static final String GROUP_NUGET = "nuget";
        public static final String GROUP_PACKAGIST = "packagist";
        public static final String GROUP_PEAR = "pear";
        public static final String GROUP_PIP = "bitbake";
        public static final String GROUP_POLICY_CHECK = "policy check";
        public static final String GROUP_PROJECT_INFO = "project info";
        public static final String GROUP_PYTHON = "python";
        public static final String GROUP_SBT = "sbt";
        public static final String GROUP_SIGNATURE_SCANNER = "signature scanner";
        public static final String GROUP_YARN = "yarn";
        public static final String GROUP_POLARIS = "polaris";
        public static final String GROUP_BAZEL = "bazel";

        @Deprecated
        public static final String SEARCH_GROUP_HUB = "hub";
        public static final String SEARCH_GROUP_BLACKDUCK = "blackduck";
        public static final String SEARCH_GROUP_SIGNATURE_SCANNER = "scanner";
        public static final String SEARCH_GROUP_POLICY = "policy";
        public static final String SEARCH_GROUP_PROXY = "proxy";
        public static final String SEARCH_GROUP_OFFLINE = "offline";
        public static final String SEARCH_GROUP_PROJECT = "project";
        public static final String SEARCH_GROUP_DEBUG = "debug";
        public static final String SEARCH_GROUP_SEARCH = "search";

        public static final String PRINT_GROUP_DEFAULT = SEARCH_GROUP_BLACKDUCK;

    }

}
