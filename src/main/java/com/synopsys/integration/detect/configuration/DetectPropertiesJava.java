package com.synopsys.integration.detect.configuration;

import java.util.Collections;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.types.bool.BooleanProperty;
import com.synopsys.integration.configuration.property.types.integer.IntegerProperty;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;
import com.synopsys.integration.configuration.property.types.string.StringListProperty;

public class DetectPropertiesJava {

    NullableStringProperty BLACKDUCK_API_TOKEN = new PropertyBuilder<NullableStringProperty>("blackduck.api.token")
                                       .info("Black Duck API Token", "4.2.0")
                                       .help("The API token used to authenticate with the Black Duck Server.")
                                       .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                       .build(NullableStringProperty.class);

    Property BLACKDUCK_OFFLINE_MODE = new PropertyBuilder<BooleanProperty>("blackduck.offline.mode", false)
                                          .info("Offline Mode", "4.2.0")
                                          .help("This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
                                          .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.OFFLINE, DetectGroup.DEFAULT)
                                          .build(BooleanProperty.class);

    Property BLACKDUCK_PASSWORD = new PropertyBuilder<NullableStringProperty>("blackduck.password")
                                      .info("Black Duck Password", "4.2.0")
                                      .help("Black Duck password.")
                                      .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                      .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_HOST = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.host")
                                        .info("Proxy Host", "4.2.0")
                                        .help("Hostname for proxy server.")
                                        .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                        .category(DetectCategory.Advanced)
                                        .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_IGNORED_HOSTS = new PropertyBuilder<StringListProperty>("blackduck.proxy.ignored.hosts", Collections.emptyList())
                                                 .info("Bypass Proxy Hosts", "4.2.0")
                                                 .help("A comma separated list of regular expression host patterns that should not use the proxy.", "These patterns must adhere to Java regular expressions: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html")
                                                 .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                                 .category(DetectCategory.Advanced)
                                                 .build(StringListProperty.class);

    Property BLACKDUCK_PROXY_NTLM_DOMAIN = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.ntlm.domain")
                                               .info("NTLM Proxy Domain", "4.2.0")
                                               .help("NTLM Proxy domain.")
                                               .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                               .category(DetectCategory.Advanced)
                                               .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_NTLM_WORKSTATION = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.ntlm.workstation")
                                                    .info("NTLM Proxy Workstation", "4.2.0")
                                                    .help("NTLM Proxy workstation.")
                                                    .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                                    .category(DetectCategory.Advanced)
                                                    .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_PASSWORD = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.password")
                                            .info("Proxy Password", "4.2.0")
                                            .help("Proxy password.")
                                            .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                            .category(DetectCategory.Advanced)
                                            .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_PORT = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.port")
                                        .info("Proxy Port", "4.2.0")
                                        .help("Proxy port.")
                                        .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                        .category(DetectCategory.Advanced)
                                        .build(NullableStringProperty.class);

    Property BLACKDUCK_PROXY_USERNAME = new PropertyBuilder<NullableStringProperty>("blackduck.proxy.username")
                                            .info("Proxy Username", "4.2.0")
                                            .help("Proxy username.")
                                            .groups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                            .category(DetectCategory.Advanced)
                                            .build(NullableStringProperty.class);

    Property BLACKDUCK_TIMEOUT = new PropertyBuilder<IntegerProperty>("blackduck.timeout", 120)
                                     .info("Black Duck Timeout", "4.2.0")
                                     .help("The time to wait for network connections to complete (in seconds).")
                                     .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                     .category(DetectCategory.Advanced).build(IntegerProperty.class);

    Property BLACKDUCK_TRUST_CERT = new PropertyBuilder<BooleanProperty>("blackduck.trust.cert", false)
                                        .info("Trust All SSL Certificates", "4.2.0")
                                        .help("If true, automatically trust the certificate for the current run of Detect only.")
                                        .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                        .category(DetectCategory.Advanced)
                                        .build(BooleanProperty.class);

    Property BLACKDUCK_URL = new PropertyBuilder<NullableStringProperty>("blackduck.url")
                                 .info("Black Duck URL", "4.2.0")
                                 .help("URL of the Black Duck server.")
                                 .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                 .build(NullableStringProperty.class);

    Property BLACKDUCK_USERNAME = new PropertyBuilder<NullableStringProperty>("blackduck.username")
                                      .info("Black Duck Username", "4.2.0")
                                      .help("Black Duck username.")
                                      .groups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT)
                                      .build(NullableStringProperty.class);

    Property DETECT_PARALLEL_PROCESSORS = new PropertyBuilder<IntegerProperty>("detect.parallel.processors", 1)
                                              .info("Detect Parallel Processors", "6.0.0")
                                              .help("The number of threads to run processes in parallel, defaults to 1, but if you specify less than or equal to 0, the number of processors on the machine will be used.")
                                              .groups(DetectGroup.GENERAL, DetectGroup.GLOBAL)
                                              .category(DetectCategory.Advanced)
                                              .build(IntegerProperty.class);

    Property DETECT_BASH_PATH = new PropertyBuilder<NullablePathProperty>("detect.bash.path")
                                    .info("Bash Executable", "3.0.0")
                                    .help("Path to the Bash executable.", "If set, Detect will use the given Bash executable instead of searching for one.")
                                    .groups(DetectGroup.PATHS, DetectGroup.GLOBAL)
                                    .build(NullablePathProperty.class);


    public DetectPropertiesJava() throws InstantiationException, IllegalAccessException {}
}
