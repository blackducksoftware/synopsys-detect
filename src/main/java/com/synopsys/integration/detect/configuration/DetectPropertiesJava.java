package com.synopsys.integration.detect.configuration;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.Arrays;

import com.synopsys.integration.blackduck.api.generated.enumeration.LicenseFamilyLicenseFamilyRiskRulesReleaseDistributionType;
import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.manual.throwaway.generated.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.IndividualFileMatching;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.types.bool.BooleanProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enums.EnumListProperty;
import com.synopsys.integration.configuration.property.types.enums.EnumProperty;
import com.synopsys.integration.configuration.property.types.integer.IntegerProperty;
import com.synopsys.integration.configuration.property.types.integer.NullableIntegerProperty;
import com.synopsys.integration.configuration.property.types.longs.LongProperty;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.path.PathListProperty;
import com.synopsys.integration.configuration.property.types.path.PathProperty;
import com.synopsys.integration.configuration.property.types.path.PathValue;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;
import com.synopsys.integration.configuration.property.types.string.StringListProperty;
import com.synopsys.integration.configuration.property.types.string.StringProperty;
import com.synopsys.integration.detect.DetectMajorVersion;
import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.configuration.enums.DefaultVersionNameScheme;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedIndividualFileMatchingMode;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedSnippetMode;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.log.LogLevel;

import java.lang.Deprecated;

public class DetectPropertiesJava {

   public static final NullableStringProperty BLACKDUCK_API_TOKEN = new PropertyBuilder<>(new NullableStringProperty("blackduck.api.token")).build(property -> {
        property.setInfo("Black Duck API Token", "4.2.0");
        property.setHelp("The API token used to authenticate with the Black Duck Server.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
    });

   public static final BooleanProperty BLACKDUCK_OFFLINE_MODE = new PropertyBuilder<>(new BooleanProperty("blackduck.offline.mode", false)).build(property -> {
        property.setInfo("Offline Mode", "4.2.0");
        property.setHelp("This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.OFFLINE, DetectGroup.DEFAULT);
    });

   public static final NullableStringProperty BLACKDUCK_PASSWORD = new PropertyBuilder<>(new NullableStringProperty("blackduck.password")).build(property -> {
        property.setInfo("Black Duck Password", "4.2.0");
        property.setHelp("Black Duck password.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_HOST = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.host")).build(property -> {
        property.setInfo("Proxy Host", "4.2.0");
        property.setHelp("Hostname for proxy server.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final StringListProperty BLACKDUCK_PROXY_IGNORED_HOSTS = new PropertyBuilder<>(new StringListProperty("blackduck.proxy.ignored.hosts", emptyList())).build(property -> {
        property.setInfo("Bypass Proxy Hosts", "4.2.0");
        property.setHelp("A comma separated list of regular expression host patterns that should not use the proxy.", "These patterns must adhere to Java regular expressions: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_NTLM_DOMAIN = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.ntlm.domain")).build(property -> {
        property.setInfo("NTLM Proxy Domain", "4.2.0");
        property.setHelp("NTLM Proxy domain.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_NTLM_WORKSTATION = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.ntlm.workstation")).build(property -> {
        property.setInfo("NTLM Proxy Workstation", "4.2.0");
        property.setHelp("NTLM Proxy workstation.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_PASSWORD = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.password")).build(property -> {
        property.setInfo("Proxy Password", "4.2.0");
        property.setHelp("Proxy password.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_PORT = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.port")).build(property -> {
        property.setInfo("Proxy Port", "4.2.0");
        property.setHelp("Proxy port.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final NullableStringProperty BLACKDUCK_PROXY_USERNAME = new PropertyBuilder<>(new NullableStringProperty("blackduck.proxy.username")).build(property -> {
        property.setInfo("Proxy Username", "4.2.0");
        property.setHelp("Proxy username.");
        property.setGroups(DetectGroup.PROXY, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

   public static final IntegerProperty BLACKDUCK_TIMEOUT = new PropertyBuilder<>(new IntegerProperty("blackduck.timeout", 120)).build(property -> {
        property.setInfo("Black Duck Timeout", "4.2.0");
        property.setHelp("The time to wait for network connections to complete (in seconds).");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  BLACKDUCK_TRUST_CERT = new PropertyBuilder<>(new BooleanProperty("blackduck.trust.cert", false)).build(property -> {
        property.setInfo("Trust All SSL Certificates", "4.2.0");
        property.setHelp("If true, automatically trust the certificate for the current run of Detect only.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  BLACKDUCK_URL = new PropertyBuilder<>(new NullableStringProperty("blackduck.url")).build(property -> {
        property.setInfo("Black Duck URL", "4.2.0");
        property.setHelp("URL of the Black Duck server.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
    });

    public static final NullableStringProperty  BLACKDUCK_USERNAME = new PropertyBuilder<>(new NullableStringProperty("blackduck.username")).build(property -> {
        property.setInfo("Black Duck Username", "4.2.0");
        property.setHelp("Black Duck username.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
    });

    public static final IntegerProperty  DETECT_PARALLEL_PROCESSORS = new PropertyBuilder<>(new IntegerProperty("detect.parallel.processors", 1)).build(property -> {
        property.setInfo("Detect Parallel Processors", "6.0.0");
        property.setHelp("The number of threads to run processes in parallel, defaults to 1, but if you specify less than or equal to 0, the number of processors on the machine will be used.");
        property.setGroups(DetectGroup.GENERAL, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_BASH_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.bash.path")).build(property -> {
        property.setInfo("Bash Executable", "3.0.0");
        property.setHelp("Path to the Bash executable.", "If set, Detect will use the given Bash executable instead of searching for one.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_BAZEL_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.bazel.path")).build(property -> {
        property.setInfo("Bazel Executable", "5.2.0");
        property.setHelp("The path to the Bazel executable.");
        property.setGroups(DetectGroup.BAZEL, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_BAZEL_TARGET = new PropertyBuilder<>(new NullableStringProperty("detect.bazel.target")).build(property -> {
        property.setInfo("Bazel Target", "5.2.0");
        property.setHelp("The Bazel target (for example, //foo:foolib) for which dependencies are collected. For Detect to run Bazel, this property must be set.");
        property.setGroups(DetectGroup.BAZEL, DetectGroup.SOURCE_SCAN);
    });

    public static final StringListProperty  DETECT_BAZEL_CQUERY_OPTIONS = new PropertyBuilder<>(new StringListProperty("detect.bazel.cquery.options", emptyList())).build(property -> {
        property.setInfo("Bazel cquery additional options", "6.1.0");
        property.setHelp("A comma-separated list of additional options to pass to the bazel cquery command.");
        property.setGroups(DetectGroup.BAZEL, DetectGroup.SOURCE_SCAN);
    });

    public static final EnumProperty  DETECT_BAZEL_DEPENDENCY_RULE = new PropertyBuilder<>(new EnumProperty("detect.bazel.dependency.type", WorkspaceRule.UNSPECIFIED, WorkspaceRule.class)).build(property -> {
        property.setInfo("Bazel workspace external dependency rule", "6.0.0");
        property.setHelp("The Bazel workspace rule used to pull in external dependencies. If not set, Detect will attempt to determine the rule from the contents of the WORKSPACE file.");
        property.setGroups(DetectGroup.BAZEL, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_BDIO_OUTPUT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.bdio.output.path")).build(property -> {
        property.setInfo("BDIO Output Directory", "3.0.0");
        property.setHelp("The path to the output directory for all BDIO files.", "If not set, the BDIO files are placed in a 'BDIO' subdirectory of the output directory.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_BDIO2_ENABLED = new PropertyBuilder<>(new BooleanProperty("detect.bdio2.enabled", false)).build(property -> {
        property.setInfo("BDIO 2 Enabled", "6.1.0");
        property.setHelp("The version of BDIO files to generate.", "If set to false, BDIO version 1 will be generated. If set to true, BDIO version 2 will be generated.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_BINARY_SCAN_FILE = new PropertyBuilder<>(new NullablePathProperty("detect.binary.scan.file.path")).build(property -> {
        property.setInfo("Binary Scan Target", "4.2.0");
        property.setHelp("If specified, this file and this file only will be uploaded for binary scan analysis. This property takes precedence over detect.binary.scan.file.name.patterns. The BINARY_SCAN tool does not provide project and version name defaults to Detect, so you need to set project and version names via properties when only the BINARY_SCAN tool is invoked.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.SOURCE_PATH);
    });

    public static final StringListProperty DETECT_BINARY_SCAN_FILE_NAME_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.binary.scan.file.name.patterns", emptyList())).build(property -> {
        property.setInfo("Binary Scan Filename Patterns", "6.0.0");
        property.setHelp("If specified, all files in the source directory whose names match these file name patterns will be zipped and uploaded for binary scan analysis. This property will not be used if detect.binary.scan.file.path is specified.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.SOURCE_PATH);
    });

    public static final StringProperty DETECT_BITBAKE_BUILD_ENV_NAME = new PropertyBuilder<>(new StringProperty("detect.bitbake.build.env.name", "oe-init-build-env")).build(property -> {
        property.setInfo("BitBake Init Script Name", "4.4.0");
        property.setHelp("The name of the build environment init script.");
        property.setGroups(DetectGroup.BITBAKE, DetectGroup.SOURCE_SCAN);
    });

    public static final StringListProperty  DETECT_BITBAKE_PACKAGE_NAMES = new PropertyBuilder<>(new StringListProperty("detect.bitbake.package.names", emptyList())).build(property -> {
        property.setInfo("BitBake Package Names", "4.4.0");
        property.setHelp("A comma-separated list of package names from which dependencies are extracted.");
        property.setGroups(DetectGroup.BITBAKE, DetectGroup.SOURCE_SCAN);
    });

    public static final StringListProperty  DETECT_BITBAKE_SOURCE_ARGUMENTS = new PropertyBuilder<>(new StringListProperty("detect.bitbake.source.arguments", emptyList())).build(property -> {
        property.setInfo("BitBake Source Arguments", "6.0.0");
        property.setHelp("A comma-separated list of arguments to supply when sourcing the build environment init script.");
        property.setGroups(DetectGroup.BITBAKE, DetectGroup.SOURCE_SCAN);
    });

    public static final IntegerProperty  DETECT_BITBAKE_SEARCH_DEPTH = new PropertyBuilder<>(new IntegerProperty("detect.bitbake.search.depth", 1)).build(property -> {
        property.setInfo("BitBake Search Depth", "6.1.0");
        property.setHelp("The depth at which Detect will search for the recipe-depends.dot or package-depends.dot files.");
        property.setGroups(DetectGroup.BITBAKE, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS = new PropertyBuilder<>(new NullableStringProperty("detect.blackduck.signature.scanner.arguments")).build(property -> {
        property.setInfo("Signature Scanner Arguments", "4.2.0");
        property.setHelp("Additional arguments to use when running the Black Duck signature scanner.", "For example: Suppose you are running in bash on Linux and want to use the signature scanner's ability to read a list of directories to exclude from a file (using the signature scanner --exclude-from option). You tell the signature scanner read excluded directories from a file named excludes.txt in your home directory with: --detect.blackduck.signature.scanner.arguments='--exclude-from \\${HOME}/excludes.txt'");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_COPYRIGHT_SEARCH = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.copyright.search", false)).build(property -> {
        property.setInfo("Signature Scanner Copyright Search", "6.4.0");
        property.setHelp("When set to true, user will be able to scan and discover copyright names in Black Duck.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
    });

    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.dry.run", false)).build(property -> {
        property.setInfo("Signature Scanner Dry Run", "4.2.0");
        property.setHelp("If set to true, the signature scanner results are not uploaded to Black Duck, and the scanner results are written to disk.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
    });

    public static final StringListProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.blackduck.signature.scanner.exclusion.name.patterns", singletonList("node_modules"))).build(property -> {
        property.setInfo("Directory Name Exclusion Patterns", "4.2.0");
        property.setHelp("A comma-separated list of directory name patterns for which Detect searches and adds to the signature scanner --exclude flag values.", "These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters). Detect will recursively search within the scan targets for files/directories that match these patterns and will create the corresponding exclusion patterns (paths relative to the scan target directory) for the signature scanner (Black Duck scan CLI). Please note that the signature scanner will only exclude directories; matched filenames will be passed to the signature scanner but will have no effect. These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns and passed as --exclude values. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.name.patterns=blackduck-common, --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-common', --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-*'. Use this property when you want Detect to convert the given patterns to actual paths. Use detect.blackduck.signature.scanner.exclusion.patterns to pass patterns directly to the signature scanner as-is.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.SOURCE_SCAN);
    });

    public static final IntegerProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH = new PropertyBuilder<>(new IntegerProperty("detect.blackduck.signature.scanner.exclusion.pattern.search.depth", 4)).build(property -> {
        property.setInfo("Exclusion Patterns Search Depth", "5.0.0");
        property.setHelp("Enables you to adjust the depth to which Detect will search when creating signature scanner exclusion patterns.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.SOURCE_SCAN);
    });

    public static final StringListProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.blackduck.signature.scanner.exclusion.patterns", emptyList())).build(property -> {
        property.setInfo("Exclusion Patterns", "4.2.0");
        property.setHelp("A comma-separated list of values to be used with the Signature Scanner --exclude flag.", "Each pattern provided is passed to the signature scanner (Black Duck scan CLI) as a value for an --exclude option. The signature scanner requires that these exclusion patterns start and end with a forward slash (/) and may not contain double asterisks (**). These patterns will be added to the paths created from detect.blackduck.signature.scanner.exclusion.name.patterns and passed as --exclude values. Use this property to pass patterns directly to the signature scanner as-is. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude from signature scanning. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.patterns=/blackduck-common/, --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-common/', --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-*/'. Use detect.blackduck.signature.scanner.exclusion.name.patterns when you want Detect to convert the given patterns to actual paths.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.SOURCE_SCAN);
    });

    public static final ExtendedEnumProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING = new PropertyBuilder<>(new ExtendedEnumProperty("detect.blackduck.signature.scanner.individual.file.matching", ExtendedEnumValue.ofExtendedValue(
        ExtendedIndividualFileMatchingMode.NONE), ExtendedIndividualFileMatchingMode.class, IndividualFileMatching.class)).build(property -> {
        property.setInfo("Individual File Matching", "6.2.0");
        property.setHelp("Users may set this property to indicate what types of files they want to match");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
    });

    public static final NullableStringProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL = new PropertyBuilder<>(new NullableStringProperty("detect.blackduck.signature.scanner.host.url")).build(property -> {
        property.setInfo("Signature Scanner Host URL", "4.2.0");
        property.setHelp("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Black Duck's urls for different operating systems.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.license.search", false)).build(property -> {
        property.setInfo("Signature Scanner License Search", "6.2.0");
        property.setHelp("When set to true, user will be able to scan and discover license names in Black Duck");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
    });

    public static final NullablePathProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.blackduck.signature.scanner.local.path")).build(property -> {
        property.setInfo("Signature Scanner Local Path", "4.2.0");
        property.setHelp("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
    });

    public static final IntegerProperty DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY = new PropertyBuilder<>(new IntegerProperty("detect.blackduck.signature.scanner.memory", 4096)).build(property -> {
        property.setInfo("Signature Scanner Memory", "4.2.0");
        property.setHelp("The memory for the scanner to use.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.blackduck.signature.scanner.offline.local.path")).build(property -> {
        property.setInfo("Signature Scanner Local Path (Offline)", "4.2.0");
        property.setHelp("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final PathListProperty DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS = new PropertyBuilder<>(new PathListProperty("detect.blackduck.signature.scanner.paths", emptyList())).build(property -> {
        property.setInfo("Signature Scanner Target Paths", "4.2.0");
        property.setHelp("These paths and only these paths will be scanned.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
    });

    public static final ExtendedEnumProperty DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING = new PropertyBuilder<>(new ExtendedEnumProperty("detect.blackduck.signature.scanner.snippet.matching", ExtendedEnumValue.ofExtendedValue(
        ExtendedSnippetMode.NONE), ExtendedSnippetMode.class, SnippetMatching.class)).build(property -> {
        property.setInfo("Snippet Matching", "5.5.0");
        property.setHelp("Use this value to enable the various snippet scanning modes. For a full explanation, please refer to the 'Running a component scan using the Signature Scanner command line' section in your Black Duck server's online help.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.upload.source.mode", false)).build(property -> {
        property.setInfo("Upload source mode", "5.4.0");
        property.setHelp("If set to true, the signature scanner will, if supported by your Black Duck version, upload source code to Black Duck.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_BOM_AGGREGATE_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.bom.aggregate.name")).build(property -> {
        property.setInfo("Aggregate BDIO File Name", "3.0.0");
        property.setHelp("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final EnumProperty  DETECT_BOM_AGGREGATE_REMEDIATION_MODE = new PropertyBuilder<>(new EnumProperty("detect.bom.aggregate.remediation.mode", AggregateMode.TRANSITIVE, AggregateMode.class)).build(property -> {
        property.setInfo("BDIO Aggregate Remediation Mode", "6.1.0");
        property.setHelp("If an aggregate BDIO file is being generated and this property is set to DIRECT, the aggregate BDIO file will exclude code location nodes from the top layer of the dependency tree to preserve the correct identification of direct dependencies in the resulting Black Duck BOM. When this property is set to TRANSITIVE (the default), component source information is preserved by including code location nodes at the top of the dependency tree, but all components will appear as TRANSITIVE in the BOM.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_BUILDLESS = new PropertyBuilder<>(new BooleanProperty("detect.detector.buildless", false)).build(property -> {
        property.setInfo("Buildless Mode", "5.4.0");
        property.setHelp("If set to true, only Detector's capable of running without a build will be run.");
        property.setGroups(DetectGroup.GENERAL, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_CLEANUP = new PropertyBuilder<>(new BooleanProperty("detect.cleanup", true)).build(property -> {
        property.setInfo("Cleanup Output", "3.2.0");
        property.setHelp("If true, the files created by Detect will be cleaned up.");
        property.setGroups(DetectGroup.CLEANUP, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_CLONE_PROJECT_VERSION_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.clone.project.version.name")).build(property -> {
        property.setInfo("Clone Project Version Name", "4.2.0");
        property.setHelp("The name of the project version to clone this project version from. Respects the given Clone Categories in detect.project.clone.categories or as set on the Black Duck server.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_CLONE_PROJECT_VERSION_LATEST = new PropertyBuilder<>(new BooleanProperty("detect.clone.project.version.latest", false)).build(property -> {
        property.setInfo("Clone Latest Project Version", "5.6.0");
        property.setHelp("If set to true, detect will attempt to use the latest project version as the clone for this project. The project must exist and have at least one version.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_CODE_LOCATION_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.code.location.name")).build(property -> {
        property.setInfo("Scan Name", "4.0.0");
        property.setHelp("An override for the name Detect will use for the scan file it creates. If supplied and multiple scans are found, Detect will append an index to each scan name.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_CONDA_ENVIRONMENT_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.conda.environment.name")).build(property -> {
        property.setInfo("Anaconda Environment Name", "3.0.0");
        property.setHelp("The name of the anaconda environment used by your project.");
        property.setGroups(DetectGroup.CONDA, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_CONDA_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.conda.path")).build(property -> {
        property.setInfo("Conda Executable", "3.0.0");
        property.setHelp("The path to the conda executable.");
        property.setGroups(DetectGroup.CONDA, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_CPAN_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.cpan.path")).build(property -> {
        property.setInfo("cpan Executable", "3.0.0");
        property.setHelp("The path to the cpan executable.");
        property.setGroups(DetectGroup.CPAN, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_CPANM_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.cpanm.path")).build(property -> {
        property.setInfo("cpanm Executable", "3.0.0");
        property.setHelp("The path to the cpanm executable.");
        property.setGroups(DetectGroup.CPAN, DetectGroup.GLOBAL);
    });

    public static final EnumProperty  DETECT_DEFAULT_PROJECT_VERSION_SCHEME = new PropertyBuilder<>(new EnumProperty("detect.default.project.version.scheme", DefaultVersionNameScheme.TEXT, DefaultVersionNameScheme.class)).build(property -> {
        property.setInfo("Default Project Version Name Scheme", "3.0.0");
        property.setHelp("The scheme to use when the package managers can not determine a version. See detailed help for more information.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringProperty  DETECT_DEFAULT_PROJECT_VERSION_TEXT = new PropertyBuilder<>(new StringProperty("detect.default.project.version.text", "Default Detect Version")).build(property -> {
        property.setInfo("Default Project Version Name Text", "3.0.0");
        property.setHelp("The text to use as the default project version.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringProperty  DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT = new PropertyBuilder<>(new StringProperty("detect.default.project.version.timeformat", "yyyy-MM-dd\'T\'HH:mm:ss.SSS")).build(property -> {
        property.setInfo("Default Project Version Name Timestamp Format", "3.0.0");
        property.setHelp("The timestamp format to use as the default project version.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final IntegerProperty  DETECT_DETECTOR_SEARCH_DEPTH = new PropertyBuilder<>(new IntegerProperty("detect.detector.search.depth", 0)).build(property -> {
        property.setInfo("Detector Search Depth", "3.2.0");
        property.setHelp("Depth of subdirectories within the source directory to which Detect will search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_DETECTOR_SEARCH_CONTINUE = new PropertyBuilder<>(new BooleanProperty("detect.detector.search.continue", false)).build(property -> {
        property.setInfo("Detector Search Continue", "3.2.0");
        property.setHelp("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. " +
                                                                                                                                                                                         "If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty  DETECT_DETECTOR_SEARCH_EXCLUSION =  new PropertyBuilder<>(new StringListProperty("detect.detector.search.exclusion", emptyList())).build(property -> {
        property.setInfo("Detector Directory Exclusions", "3.2.0");
        property.setHelp("A comma-separated list of directory names to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name appear in this list will not be searched.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty  DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.detector.search.exclusion.patterns", emptyList())).build(property -> {
        property.setInfo(" Detector Directory Patterns Exclusions", "3.2.0");
        property.setHelp("A comma-separated list of directory name patterns to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name match a pattern in this list will not be searched. These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters).For example, suppose you're running in bash on Linux, you've set--detect.detector.search.depth = 1, and have a subdirectory named blackduck-common (a gradle project) that you want to exclude from the detector search. Any of the following would exclude it:--detect.detector.search.exclusion.patterns = blackduck-common, --detect.detector.search.exclusion.patterns = 'blackduck-common', --detect.detector.search.exclusion.patterns = 'blackduck-*'");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS = new PropertyBuilder<>(new StringListProperty("detect.detector.search.exclusion.paths", emptyList())).build(property -> {
        property.setInfo(" Detector Directory Path Exclusions", "5.5.0");
        property.setHelp("A comma-separated list of directory paths to exclude from detector search. (E.g. 'foo/bar/biz' will only exclude the 'biz' directory if the parent directory structure is 'foo/bar/'.)", "This property performs the same basic function as detect.detector.search.exclusion, but lets you be more specific.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty  DETECT_DETECTOR_SEARCH_EXCLUSION_FILES =  new PropertyBuilder<>(new StringListProperty("detect.detector.search.exclusion.files", emptyList())).build(property -> {
        property.setInfo(" Detector File Exclusions", "6.0.0");
        property.setHelp("A comma-separated list of file names to exclude from detector search.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS = new PropertyBuilder<>(new BooleanProperty("detect.detector.search.exclusion.defaults", true)).build(property -> {
        property.setInfo(" Detector File Exclusions", "6.0.0");
        property.setInfo("Detector Exclude Default Directories", "3.2.0");
        property.setHelp("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the detector search: bin, build, .git, .gradle, node_modules, out, packages, target.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_IGNORE_CONNECTION_FAILURES = new PropertyBuilder<>(new BooleanProperty("detect.ignore.connection.failures", false)).build(property -> {
        property.setInfo("Detect Ignore Connection Failures", "5.3.0");
        property.setHelp("If true, Detect will ignore any products that it cannot connect to.", "If true, when Detect attempts to boot a product it will also check if it can communicate with it - if it cannot, it will not run the product.");
        property.setGroups(DetectGroup.GENERAL, DetectGroup.BLACKDUCK_SERVER, DetectGroup.POLARIS);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final PassthroughProperty PHONEHOME_PASSTHROUGH = new PropertyBuilder<>(new PassthroughProperty("detect.phone.home.passthrough")).build(property -> {
        property.setInfo("Phone Home Passthrough", "6.0.0");
        property.setHelp("Additional values may be sent home for usage information. The keys will be sent without the prefix.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final PassthroughProperty  DOCKER_PASSTHROUGH = new PropertyBuilder<>(new PassthroughProperty("detect.docker.passthrough")).build(property -> {
        property.setInfo("Docker Passthrough", "6.0.0");
        property.setHelp("Additional properties may be passed to the docker inspector by adding the prefix detect.docker.passthrough. The keys will be given to docker inspector without the prefix.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.DEFAULT);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_DOCKER_IMAGE = new PropertyBuilder<>(new NullableStringProperty("detect.docker.image")).build(property -> {
        property.setInfo("Docker Image Name", "3.0.0");
        property.setHelp("The Docker image name to inspect. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.SOURCE_PATH);
    });

    public static final NullableStringProperty  DETECT_DOCKER_IMAGE_ID = new PropertyBuilder<>(new NullableStringProperty("detect.docker.image.id")).build(property -> {
        property.setInfo("Docker Image ID", "6.1.0");
        property.setHelp("The Docker image ID to inspect.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.SOURCE_PATH);
    });

    public static final NullablePathProperty  DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.docker.inspector.air.gap.path")).build(property -> {
        property.setInfo("Docker Inspector AirGap Path", "3.0.0");
        property.setHelp("The path to the directory containing the Docker Inspector jar and images.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_DOCKER_INSPECTOR_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.docker.inspector.path")).build(property -> {
        property.setInfo("Docker Inspector .jar File Path", "3.0.0");
        property.setHelp("This is used to override using the hosted Docker Inspector .jar file by binary repository url. You can use a compatible (the same major version that Detect downloads by default) local Docker Inspector .jar file at this path.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_DOCKER_INSPECTOR_VERSION = new PropertyBuilder<>(new NullableStringProperty("detect.docker.inspector.version")).build(property -> {
        property.setInfo("Docker Inspector Version", "3.0.0");
        property.setHelp("Version of the Docker Inspector to use. By default Detect will attempt to automatically determine the version to use.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_DOCKER_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.docker.path")).build(property -> {
        property.setInfo("Docker Executable", "3.0.0");
        property.setHelp("Path to the docker executable.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_DOCKER_PATH_REQUIRED = new PropertyBuilder<>(new BooleanProperty("detect.docker.path.required", false)).build(property -> {
        property.setInfo("Run Without Docker in Path", "4.0.0");
        property.setHelp("If set to true, Detect will attempt to run the Docker Inspector only if it finds a docker client executable.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_DOCKER_PLATFORM_TOP_LAYER_ID = new PropertyBuilder<>(new NullableStringProperty("detect.docker.platform.top.layer.id")).build(property -> {
        property.setInfo("Platform Top Layer ID", "6.1.0");
        property.setHelp("To exclude components from platform layers from the results, assign to this property the ID of the top layer of the platform image. Get the platform top layer ID from the output of 'docker inspect platformimage:tag'. The platform top layer ID is the last item in RootFS.Layers. For more information, see 'Isolating application components' in the Docker Inspector documentation.", "If you are interested in components from the application layers of your image, but not interested in components from the underlying platform layers, you can exclude components from platform layers from the results by using this property to specify the boundary between platform layers and application layers. ");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_DOCKER_TAR = new PropertyBuilder<>(new NullableStringProperty("detect.docker.tar")).build(property -> {
        property.setInfo("Docker Image Archive File", "3.0.0");
        property.setHelp("A saved Docker image - must be a .tar file. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.");
        property.setGroups(DetectGroup.DOCKER, DetectGroup.SOURCE_PATH);
    });

    public static final NullablePathProperty  DETECT_DOTNET_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.dotnet.path")).build(property -> {
        property.setInfo("dotnet Executable", "4.4.0");
        property.setHelp("The path to the dotnet executable.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.GLOBAL);
    });

    public static final FilterableEnumListProperty  DETECT_EXCLUDED_DETECTOR_TYPES = new PropertyBuilder<>(new FilterableEnumListProperty("detect.excluded.detector.types", emptyList(), DetectorType.class)).build(property -> {
        property.setInfo("Detector Types Excluded", "3.0.0");
        property.setHelp("By default, all detectors will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all detectors, specify \"ALL\". Exclusion rules always win.", "If Detect runs one or more detector on your project that you would like to exclude, you can use this property to prevent Detect from running them.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_FORCE_SUCCESS = new PropertyBuilder<>(new BooleanProperty("detect.force.success", false)).build(property -> {
        property.setInfo("Force Success", "3.0.0");
        property.setHelp("If true, Detect will always exit with code 0.");
        property.setGroups(DetectGroup.GENERAL, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_GIT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.git.path")).build(property -> {
        property.setInfo("Git Executable", "5.5.0");
        property.setHelp("Path of the git executable");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_GO_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.go.path")).build(property -> {
        property.setInfo("Go Executable", "3.0.0");
        property.setHelp("Path to the Go executable.");
        property.setGroups(DetectGroup.GO, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_GRADLE_BUILD_COMMAND = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.build.command")).build(property -> {
        property.setInfo("Gradle Build Command", "3.0.0");
        property.setHelp("Gradle command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the gradle (or gradlew) command with one task: dependencies. You can use this property to insert one or more additional gradle command line arguments (options or tasks) before the dependencies argument.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_GRADLE_EXCLUDED_CONFIGURATIONS = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.excluded.configurations")).build(property -> {
        property.setInfo("Gradle Exclude Configurations", "3.0.0");
        property.setHelp("A comma-separated list of Gradle configurations to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle configurations specified via this property.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_GRADLE_EXCLUDED_PROJECTS = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.excluded.projects")).build(property -> {
        property.setInfo("Gradle Exclude Projects", "3.0.0");
        property.setHelp("A comma-separated list of Gradle sub-projects to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle sub-projects specified via this property.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_GRADLE_INCLUDED_CONFIGURATIONS = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.included.configurations")).build(property -> {
        property.setInfo("Gradle Include Configurations", "3.0.0");
        property.setHelp("A comma-separated list of Gradle configurations to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those Gradle configurations specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_GRADLE_INCLUDED_PROJECTS = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.included.projects")).build(property -> {
        property.setInfo("Gradle Include Projects", "3.0.0");
        property.setHelp("A comma-separated list of Gradle sub-projects to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those sub-projects specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.gradle.inspector.air.gap.path")).build(property -> {
        property.setInfo("Gradle Inspector AirGap Path", "3.0.0");
        property.setHelp("The path to the directory containing the air gap dependencies for the gradle inspector.", "Use this property when running Detect on a Gradle project in 'air gap' mode (offline). Download and unzip the Detect air gap zip file, and point this property to the packaged-inspectors/gradle directory.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_GRADLE_INSPECTOR_VERSION = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.inspector.version")).build(property -> {
        property.setInfo("Gradle Inspector Version", "3.0.0");
        property.setHelp("The version of the Gradle Inspector that Detect should use. By default, Detect will try to automatically determine the correct Gradle Inspector version.", "The Detect Gradle detector uses a separate program, the Gradle Inspector, to discover dependencies from Gradle projects. Detect automatically downloads the Gradle Inspector as needed. Use the property to use a specific version of the Gradle Inspector.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_GRADLE_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.gradle.path")).build(property -> {
        property.setInfo("Gradle Executable", "3.0.0");
        property.setHelp("The path to the Gradle executable (gradle or gradlew).", "If set, Detect will use the given Gradle executable instead of searching for one.");
        property.setGroups(DetectGroup.GRADLE, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_HEX_REBAR3_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.hex.rebar3.path")).build(property -> {
        property.setInfo("Rebar3 Executable", "3.0.0");
        property.setHelp("The path to the rebar3 executable.");
        property.setGroups(DetectGroup.HEX, DetectGroup.GLOBAL);
    });

    public static final FilterableEnumListProperty  DETECT_INCLUDED_DETECTOR_TYPES = new PropertyBuilder<>(new FilterableEnumListProperty("detect.included.detector.types", emptyList(), DetectorType.class)).build(property -> {
        property.setInfo("Detector Types Included", "3.0.0");
        property.setHelp("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", "If you want to limit Detect to a subset of its detectors, use this property to specify that subset.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_JAVA_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.java.path")).build(property -> {
        property.setInfo("Java Executable", "5.0.0");
        property.setHelp("Path to the java executable.", "If set, Detect will use the given java executable instead of searching for one.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_LERNA_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.lerna.path")).build(property -> {
        property.setInfo("Lerna Executable", "6.0.0");
        property.setHelp("Path of the lerna executable.");
        property.setGroups(DetectGroup.LERNA, DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_LERNA_INCLUDE_PRIVATE = new PropertyBuilder<>(new BooleanProperty("detect.lerna.include.private", false)).build(property -> {
        property.setInfo("Include Lerna Packages defined as private.", "6.0.0");
        property.setHelp("Lerna allows for private packages that do not get published. Set this to true to include all packages including private packages.");
        property.setGroups(DetectGroup.LERNA, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_MAVEN_BUILD_COMMAND = new PropertyBuilder<>(new NullableStringProperty("detect.maven.build.command")).build(property -> {
        property.setInfo("Maven Build Command", "3.0.0");
        property.setHelp("Maven command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the mvn (or mvnw) command with one argument: dependency:tree. You can use this property to insert one or more additional mvn command line arguments (goals, etc.) before the dependency:tree argument. For example: suppose you are running in bash on Linux, and want to point maven to your settings file (maven_dev_settings.xml in your home directory) and assign the value 'other' to property 'reason'. You could do this with: --detect.maven.build.command='--settings \\${HOME}/maven_dev_settings.xml --define reason=other'");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_MAVEN_EXCLUDED_MODULES = new PropertyBuilder<>(new NullableStringProperty("detect.maven.excluded.modules")).build(property -> {
        property.setInfo("Maven Modules Excluded", "3.0.0");
        property.setHelp("A comma-separated list of Maven modules (sub-projects) to exclude.", "As Detect parses the mvn dependency:tree output for dependencies, Detect will skip any Maven modules specified via this property.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_MAVEN_INCLUDED_MODULES = new PropertyBuilder<>(new NullableStringProperty("detect.maven.included.modules")).build(property -> {
        property.setInfo("Maven Modules Included", "3.0.0");
        property.setHelp("A comma-separated list of Maven modules (sub-projects) to include.", "As Detect parses the mvn dependency:tree output for dependencies, if this property is set, Detect will include only those Maven modules specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_MAVEN_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.maven.path")).build(property -> {
        property.setInfo("Maven Executable", "3.0.0");
        property.setHelp("The path to the Maven executable (mvn or mvnw).", "If set, Detect will use the given Maven executable instead of searching for one.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_MAVEN_INCLUDED_SCOPES = new PropertyBuilder<>(new NullableStringProperty("detect.maven.included.scopes")).build(property -> {
        property.setInfo("Dependency Scope Included", "6.0.0");
        property.setHelp("A comma separated list of Maven scopes. Output will be limited to dependencies within these scopes (overridden by exclude).", "If set, Detect will include only dependencies of the given Maven scope.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_MAVEN_EXCLUDED_SCOPES = new PropertyBuilder<>(new NullableStringProperty("detect.maven.excluded.scopes")).build(property -> {
        property.setInfo("Dependency Scope Excluded", "6.0.0");
        property.setHelp("A comma separated list of Maven scopes. Output will be limited to dependencies outside these scopes (overrides include).", "If set, Detect will include only dependencies outside of the given Maven scope.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_MAVEN_INCLUDE_PLUGINS = new PropertyBuilder<>(new BooleanProperty("detect.maven.include.plugins", false)).build(property -> {
        property.setInfo("Maven Include Plugins", "5.6.0");
        property.setHelp("Whether or not detect will include the plugins section when parsing a pom.xml.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_NOTICES_REPORT = new PropertyBuilder<>(new BooleanProperty("detect.notices.report", false)).build(property -> {
        property.setInfo("Generate Notices Report", "3.0.0");
        property.setHelp("When set to true, a Black Duck notices report in text form will be created in your source directory.");
        property.setGroups(DetectGroup.REPORT, DetectGroup.GLOBAL);
    });

    public static final PathProperty DETECT_NOTICES_REPORT_PATH = new PropertyBuilder<>(new PathProperty("detect.notices.report.path", new PathValue("."))).build(property -> {
        property.setInfo("Notices Report Path", "3.0.0");
        property.setHelp("The output directory for notices report. Default is the source directory.");
        property.setGroups(DetectGroup.REPORT, DetectGroup.GLOBAL, DetectGroup.REPORT_SETTING);
    });

    public static final NullableStringProperty  DETECT_NPM_ARGUMENTS = new PropertyBuilder<>(new NullableStringProperty("detect.npm.arguments")).build(property -> {
        property.setInfo("Additional NPM Command Arguments", "4.3.0");
        property.setHelp("A space-separated list of additional arguments to add to the npm command line when running Detect against an NPM project.");
        property.setGroups(DetectGroup.NPM, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_NPM_INCLUDE_DEV_DEPENDENCIES = new PropertyBuilder<>(new BooleanProperty("detect.npm.include.dev.dependencies", true)).build(property -> {
        property.setInfo("Include NPM Development Dependencies", "3.0.0");
        property.setHelp("Set this value to false if you would like to exclude your dev dependencies when ran.");
        property.setGroups(DetectGroup.NPM, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_NPM_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.npm.path")).build(property -> {
        property.setInfo("NPM Executable", "3.0.0");
        property.setHelp("The path to the Npm executable.");
        property.setGroups(DetectGroup.NPM, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_NUGET_CONFIG_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.nuget.config.path")).build(property -> {
        property.setInfo("Nuget Config File", "4.0.0");
        property.setHelp("The path to the Nuget.Config file to supply to the nuget exe.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_NUGET_EXCLUDED_MODULES = new PropertyBuilder<>(new NullableStringProperty("detect.nuget.excluded.modules")).build(property -> {
        property.setInfo("Nuget Projects Excluded", "3.0.0");
        property.setHelp("The names of the projects in a solution to exclude.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_NUGET_IGNORE_FAILURE = new PropertyBuilder<>(new BooleanProperty("detect.nuget.ignore.failure", false)).build(property -> {
        property.setInfo("Ignore Nuget Failures", "3.0.0");
        property.setHelp("If true errors will be logged and then ignored.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_NUGET_INCLUDED_MODULES = new PropertyBuilder<>(new NullableStringProperty("detect.nuget.included.modules")).build(property -> {
        property.setInfo("Nuget Modules Included", "3.0.0");
        property.setHelp("The names of the projects in a solution to include (overrides exclude).");
        property.setGroups(DetectGroup.NUGET, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullablePathProperty  DETECT_NUGET_INSPECTOR_AIR_GAP_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.nuget.inspector.air.gap.path")).build(property -> {
        property.setInfo("Nuget Inspector AirGap Path", "3.0.0");
        property.setHelp("The path to the directory containing the nuget inspector nupkg.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_NUGET_INSPECTOR_VERSION = new PropertyBuilder<>(new NullableStringProperty("detect.nuget.inspector.version")).build(property -> {
        property.setInfo("Nuget Inspector Version", "3.0.0");
        property.setHelp("Version of the Nuget Inspector. By default Detect will run the latest version that is compatible with the Detect version.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty  DETECT_NUGET_PACKAGES_REPO_URL = new PropertyBuilder<>(new StringListProperty("detect.nuget.packages.repo.url", singletonList("https://api.nuget.org/v3/index.json"))).build(property -> {
        property.setInfo("Nuget Packages Repository URL", "3.0.0");
        property.setHelp("The source for nuget packages", "Set this to \"https://www.nuget.org/api/v2/\" if your are still using a nuget client expecting the v2 api.");
        property.setGroups(DetectGroup.NUGET, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_OUTPUT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.output.path")).build(property -> {
        property.setInfo("Detect Output Path", "3.0.0");
        property.setHelp("The path to the output directory.", "If set, Detect will use the given directory to store files that it downloads and creates, instead of using the default location (~/blackduck).");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_TOOLS_OUTPUT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.tools.output.path")).build(property -> {
        property.setInfo("Detect Tools Output Path", "5.6.0");
        property.setHelp("The path to the tools directory where detect should download and/or access things like the Signature Scanner that it shares over multiple runs.", "If set, Detect will use the given directory instead of using the default location of output path plus tools.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES = new PropertyBuilder<>(new BooleanProperty("detect.packagist.include.dev.dependencies", true)).build(property -> {
        property.setInfo("Include Packagist Development Dependencies", "3.0.0");
        property.setHelp("Set this value to false if you would like to exclude your dev requires dependencies when ran.");
        property.setGroups(DetectGroup.PACKAGIST, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_PEAR_ONLY_REQUIRED_DEPS = new PropertyBuilder<>(new BooleanProperty("detect.pear.only.required.deps", false)).build(property -> {
        property.setInfo("Include Only Required Pear Dependencies", "3.0.0");
        property.setHelp("Set to true if you would like to include only required packages.");
        property.setGroups(DetectGroup.PEAR, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_PEAR_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.pear.path")).build(property -> {
        property.setInfo("Pear Executable", "3.0.0");
        property.setHelp("The path to the pear executable.");
        property.setGroups(DetectGroup.PEAR, DetectGroup.GLOBAL);
    });

    public static final NullableStringProperty  DETECT_PIP_PROJECT_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.pip.project.name")).build(property -> {
        property.setInfo("PIP Project Name", "3.0.0");
        property.setHelp("The name of your PIP project, to be used if your project's name cannot be correctly inferred from its setup.py file.");
        property.setGroups(DetectGroup.PIP, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_PIP_PROJECT_VERSION_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.pip.project.version.name")).build(property -> {
        property.setInfo("PIP Project Version Name", "4.1.0");
        property.setHelp("The version of your PIP project, to be used if your project's version name cannot be correctly inferred from its setup.py file.");
        property.setGroups(DetectGroup.PIP, DetectGroup.SOURCE_SCAN);
    });

    public static final PathListProperty  DETECT_PIP_REQUIREMENTS_PATH = new PropertyBuilder<>(new PathListProperty("detect.pip.requirements.path", emptyList())).build(property -> {
        property.setInfo("PIP Requirements Path", "3.0.0");
        property.setHelp("A comma-separated list of paths to requirements.txt files.");
        property.setGroups(DetectGroup.PIP, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_PIP_ONLY_PROJECT_TREE = new PropertyBuilder<>(new BooleanProperty("detect.pip.only.project.tree", false)).build(property -> {
        property.setInfo("PIP Include Only Project Tree", "6.1.0");
        property.setHelp("By default, pipenv includes all dependencies found in the graph. Set to true to only include dependencies found underneath the dependency that matches the provided pip project and version name.");
        property.setGroups(DetectGroup.PIP, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_PIPENV_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.pipenv.path")).build(property -> {
        property.setInfo("Pipenv Executable", "4.1.0");
        property.setHelp("The path to the Pipenv executable.");
        property.setGroups(DetectGroup.PIP, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_SWIFT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.swift.path")).build(property -> {
        property.setInfo("Swift Executable", "6.0.0");
        property.setHelp("Path of the swift executable.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final FilterableEnumListProperty  DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES = new PropertyBuilder<>(new FilterableEnumListProperty("detect.policy.check.fail.on.severities", FilterableEnumUtils.noneList(), PolicyRuleSeverityType.class)).build(property -> {
        property.setInfo("Fail on Policy Violation Severities", "3.0.0");
        property.setHelp("A comma-separated list of policy violation severities that will fail Detect. If this is set to NONE, Detect will not fail due to policy violations. A value of ALL is equivalent to all of the other possible values except NONE.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.GLOBAL, DetectGroup.PROJECT_SETTING, DetectGroup.POLICY);
    });

    public static final NullableStringProperty  DETECT_PROJECT_APPLICATION_ID = new PropertyBuilder<>(new NullableStringProperty("detect.project.application.id")).build(property -> {
        property.setInfo("Application ID", "5.2.0");
        property.setHelp("Sets the 'Application ID' project setting.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_CUSTOM_FIELDS_PROJECT = new PropertyBuilder<>(new NullableStringProperty("detect.custom.fields.project")).build(property -> {
        property.setInfo("Custom Fields", "5.6.0");
        property.setHelp("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.project[0].label='example' and detect.custom.fields.project[0].value='one,two'. Note that these will not show up in the detect configuration log.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_CUSTOM_FIELDS_VERSION = new PropertyBuilder<>(new NullableStringProperty("detect.custom.fields.version")).build(property -> {
        property.setInfo("Custom Fields", "5.6.0");
        property.setHelp("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.version[0].label='example' and detect.custom.fields.version[0].value='one,two'. Note that these will not show up in the detect configuration log.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final EnumListProperty  DETECT_PROJECT_CLONE_CATEGORIES = new PropertyBuilder<>(new EnumListProperty("detect.project.clone.categories", Arrays.asList(ProjectCloneCategoriesType.COMPONENT_DATA, ProjectCloneCategoriesType.VULN_DATA), ProjectCloneCategoriesType.class)).build(property -> {
        property.setInfo("Clone Project Categories", "4.2.0");
        property.setHelp("An override for the Project Clone Categories that are used when cloning a version. If the project already exists, make sure to use --detect.project.version.update to make sure these are set.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_CODELOCATION_PREFIX = new PropertyBuilder<>(new NullableStringProperty("detect.project.codelocation.prefix")).build(property -> {
        property.setInfo("Scan Name Prefix", "3.0.0");
        property.setHelp("A prefix to the name of the scans created by Detect. Useful for running against the same projects on multiple machines.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_CODELOCATION_SUFFIX = new PropertyBuilder<>(new NullableStringProperty("detect.project.codelocation.suffix")).build(property -> {
        property.setInfo("Scan Name Suffix", "3.0.0");
        property.setHelp("A suffix to the name of the scans created by Detect.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_PROJECT_CODELOCATION_UNMAP = new PropertyBuilder<>(new BooleanProperty("detect.project.codelocation.unmap", false)).build(property -> {
        property.setInfo("Unmap All Other Scans for Project", "4.0.0");
        property.setHelp("If set to true, unmaps all other scans mapped to the project version produced by the current run of Detect.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_DESCRIPTION = new PropertyBuilder<>(new NullableStringProperty("detect.project.description")).build(property -> {
        property.setInfo("Project Description", "4.0.0");
        property.setHelp("If project description is specified, your project version will be created with this description.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final StringListProperty  DETECT_PROJECT_USER_GROUPS = new PropertyBuilder<>(new StringListProperty("detect.project.user.groups", emptyList())).build(property -> {
        property.setInfo("Project User Groups", "5.4.0");
        property.setHelp("A comma-separated list of names of user groups to add to the project.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final StringListProperty  DETECT_PROJECT_TAGS = new PropertyBuilder<>(new StringListProperty("detect.project.tags", emptyList())).build(property -> {
        property.setInfo("Project Tags", "5.6.0");
        property.setHelp("A comma-separated list of tags to add to the project.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_DETECTOR = new PropertyBuilder<>(new NullableStringProperty("detect.project.detector")).build(property -> {
        property.setInfo("Project Name/Version Detector", "4.0.0");
        property.setHelp("The detector that will be used to determine the project name and version when multiple detector types. This property should be used with the detect.project.tool.", "If Detect finds that multiple detectors apply, this property can be used to select the detector that will provide the project name and version. When using this property, you should also set detect.project.tool=DETECTOR");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final BooleanProperty  DETECT_PROJECT_LEVEL_ADJUSTMENTS = new PropertyBuilder<>(new BooleanProperty("detect.project.level.adjustments", true)).build(property -> {
        property.setInfo("Allow Project Level Adjustments", "3.0.0");
        property.setHelp("An override for the Project level matches.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.project.name")).build(property -> {
        property.setInfo("Project Name", "3.0.0");
        property.setHelp("An override for the name to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final NullableStringProperty  DETECT_PARENT_PROJECT_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.parent.project.name")).build(property -> {
        property.setInfo("Parent Project Name", "3.0.0");
        property.setHelp("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PARENT_PROJECT_VERSION_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.parent.project.version.name")).build(property -> {
        property.setInfo("Parent Project Version Name", "3.0.0");
        property.setHelp("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableIntegerProperty DETECT_PROJECT_TIER = new PropertyBuilder<>(new NullableIntegerProperty("detect.project.tier")).build(property -> {
        property.setInfo("Project Tier", "3.1.0");
        property.setHelp("If a Black Duck project tier is specified, your project will be created with this tier.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final EnumListProperty  DETECT_PROJECT_TOOL = new PropertyBuilder<>(new EnumListProperty("detect.project.tool", Arrays.asList(DetectTool.DOCKER, DetectTool.DETECTOR, DetectTool.BAZEL), DetectTool.class)).build(property -> {
        property.setInfo("Detector Tool Priority", "5.0.0");
        property.setHelp("The tool priority for project name and version. The project name and version will be determined by the first tool in this list that provides them.", "This allows you to control which tool provides the project name and version when more than one tool are capable of providing it.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final EnumProperty  DETECT_PROJECT_VERSION_DISTRIBUTION = new PropertyBuilder<>(new EnumProperty("detect.project.version.distribution", LicenseFamilyLicenseFamilyRiskRulesReleaseDistributionType.EXTERNAL, LicenseFamilyLicenseFamilyRiskRulesReleaseDistributionType.class)).build(property -> {
        property.setInfo("Version Distribution", "3.0.0");
        property.setHelp("An override for the Project Version distribution");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_PROJECT_VERSION_NAME = new PropertyBuilder<>(new NullableStringProperty("detect.project.version.name")).build(property -> {
        property.setInfo("Version Name", "3.0.0");
        property.setHelp("An override for the version to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final NullableStringProperty  DETECT_PROJECT_VERSION_NICKNAME = new PropertyBuilder<>(new NullableStringProperty("detect.project.version.nickname")).build(property -> {
        property.setInfo("Version Nickname", "5.2.0");
        property.setHelp("If a project version nickname is specified, your project version will be created with this nickname.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final NullableStringProperty  DETECT_PROJECT_VERSION_NOTES = new PropertyBuilder<>(new NullableStringProperty("detect.project.version.notes")).build(property -> {
        property.setInfo("Version Notes", "3.1.0");
        property.setHelp("If project version notes are specified, your project version will be created with these notes.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final EnumProperty  DETECT_PROJECT_VERSION_PHASE = new PropertyBuilder<>(new EnumProperty("detect.project.version.phase", ProjectVersionPhaseType.DEVELOPMENT, ProjectVersionPhaseType.class)).build(property -> {
        property.setInfo("Version Phase", "3.0.0");
        property.setHelp("An override for the Project Version phase.");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final BooleanProperty  DETECT_PROJECT_VERSION_UPDATE = new PropertyBuilder<>(new BooleanProperty("detect.project.version.update", false)).build(property -> {
        property.setInfo("Update Project Version", "4.0.0");
        property.setHelp("If set to true, will update the Project Version with the configured properties. See detailed help for more information.", "When set to true, the following properties will be updated on the Project. Project tier (detect.project.tier) and Project Level Adjustments (detect.project.level.adjustments). " +
                                                                                                                                               "The following properties will also be updated on the Version.Version notes (detect.project.version.notes), phase(detect.project.version.phase), distribution(detect.project.version.distribution).");
        property.setGroups(DetectGroup.PROJECT, DetectGroup.PROJECT_SETTING);
    });

    public static final NullablePathProperty  DETECT_PYTHON_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.python.path")).build(property -> {
        property.setInfo("Python Executable", "3.0.0");
        property.setHelp("The path to the Python executable.");
        property.setGroups(DetectGroup.PYTHON, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_PYTHON_PYTHON3 = new PropertyBuilder<>(new BooleanProperty("detect.python.python3", false)).build(property -> {
        property.setInfo("Use Python3", "3.0.0");
        property.setHelp("If true will use Python 3 if available on class path.");
        property.setGroups(DetectGroup.PYTHON, DetectGroup.GLOBAL);
    });

    public static final LongProperty  DETECT_REPORT_TIMEOUT = new PropertyBuilder<>(new LongProperty("detect.report.timeout", new Long(300))).build(property -> {
        property.setInfo("Report Generation Timeout", "5.2.0");
        property.setHelp("The amount of time in seconds Detect will wait for scans to finish and to generate reports (i.e. risk and policy check). When changing this value, keep in mind the checking of policies might have to wait for scans to process which can take some time.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.GLOBAL);
    });

    public static final EnumListProperty  DETECT_REQUIRED_DETECTOR_TYPES = new PropertyBuilder<>(new EnumListProperty("detect.required.detector.types", emptyList(), DetectorType.class)).build(property -> {
        property.setInfo("Required Detect Types", "4.3.0");
        property.setHelp("The set of required detectors.", "If you want one or more detectors to be required (must be found to apply), use this property to specify the set of required detectors. If this property is set, and one (or more) of the given detectors is not found to apply, Detect will fail.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_RESOLVE_TILDE_IN_PATHS = new PropertyBuilder<>(new BooleanProperty("detect.resolve.tilde.in.paths", true)).build(property -> {
        property.setInfo("Resolve Tilde in Paths", "3.0.0");
        property.setHelp("If set to false Detect will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_RISK_REPORT_PDF = new PropertyBuilder<>(new BooleanProperty("detect.risk.report.pdf", false)).build(property -> {
        property.setInfo("Generate Risk Report (PDF)", "3.0.0");
        property.setHelp("When set to true, a Black Duck risk report in PDF form will be created.");
        property.setGroups(DetectGroup.REPORT, DetectGroup.GLOBAL, DetectGroup.REPORT_SETTING);
    });

    public static final PathProperty  DETECT_RISK_REPORT_PDF_PATH = new PropertyBuilder<>(new PathProperty("detect.risk.report.pdf.path", new PathValue("."))).build(property -> {
        property.setInfo("Risk Report Output Path", "3.0.0");
        property.setHelp("The output directory for risk report in PDF. Default is the source directory.");
        property.setGroups(DetectGroup.REPORT, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES = new PropertyBuilder<>(new BooleanProperty("detect.ruby.include.runtime.dependencies", true)).build(property -> {
        property.setInfo("Ruby Runtime Dependencies", "5.4.0");
        property.setHelp("If set to false, runtime dependencies will not be included when parsing *.gemspec files.");
        property.setGroups(DetectGroup.RUBY, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final BooleanProperty  DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES = new PropertyBuilder<>(new BooleanProperty("detect.ruby.include.dev.dependencies", false)).build(property -> {
        property.setInfo("Ruby Development Dependencies", "5.4.0");
        property.setHelp("If set to true, development dependencies will be included when parsing *.gemspec files.");
        property.setGroups(DetectGroup.RUBY, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final NullableStringProperty  DETECT_SBT_EXCLUDED_CONFIGURATIONS = new PropertyBuilder<>(new NullableStringProperty("detect.sbt.excluded.configurations")).build(property -> {
        property.setInfo("SBT Configurations Excluded", "3.0.0");
        property.setHelp("The names of the sbt configurations to exclude.");
        property.setGroups(DetectGroup.SBT, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final NullableStringProperty  DETECT_SBT_INCLUDED_CONFIGURATIONS = new PropertyBuilder<>(new NullableStringProperty("detect.sbt.included.configurations")).build(property -> {
        property.setInfo("SBT Configurations Included", "3.0.0");
        property.setHelp("The names of the sbt configurations to include.");
        property.setGroups(DetectGroup.SBT, DetectGroup.SOURCE_SCAN);
        property.setCategory(DetectCategory.Advanced);
    });

    public static final IntegerProperty  DETECT_SBT_REPORT_DEPTH = new PropertyBuilder<>(new IntegerProperty("detect.sbt.report.search.depth", 3)).build(property -> {
        property.setInfo("SBT Report Search Depth", "4.3.0");
        property.setHelp("Depth the sbt detector will use to search for report files.");
        property.setGroups(DetectGroup.SBT, DetectGroup.SOURCE_SCAN);
    });

    public static final NullablePathProperty  DETECT_SCAN_OUTPUT_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.scan.output.path")).build(property -> {
        property.setInfo("Scan Output Path", "3.0.0");
        property.setHelp("The output directory for all signature scanner output files. If not set, the signature scanner output files will be in a 'scan' subdirectory of the output directory.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final NullablePathProperty  DETECT_SOURCE_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.source.path")).build(property -> {
        property.setInfo("Source Path", "3.0.0");
        property.setHelp("The path to the project directory to inspect.", "Detect will search the given directory for hints that indicate which package manager(s) the project uses, and will attempt to run the corresponding detector(s).");
        property.setGroups(DetectGroup.PATHS, DetectGroup.SOURCE_PATH);
    });

    public static final BooleanProperty  DETECT_TEST_CONNECTION = new PropertyBuilder<>(new BooleanProperty("detect.test.connection", false)).build(property -> {
        property.setInfo("Test Connection to Black Duck", "3.0.0");
        property.setHelp("Test the connection to Black Duck with the current configuration.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.GLOBAL);
    });

    public static final FilterableEnumListProperty  DETECT_TOOLS = new PropertyBuilder<>(new FilterableEnumListProperty("detect.tools", emptyList(), DetectTool.class)).build(property -> {
        property.setInfo("Detect Tools Included", "5.0.0");
        property.setHelp("The tools Detect should allow in a comma-separated list. Tools in this list (as long as they are not also in the excluded list) will be allowed to run if all criteria of the tool are met. Exclusion rules always win.", "This property and detect.tools.excluded provide control over which tools Detect runs.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final FilterableEnumListProperty  DETECT_TOOLS_EXCLUDED = new PropertyBuilder<>(new FilterableEnumListProperty("detect.tools.excluded", emptyList(), DetectTool.class)).build(property -> {
        property.setInfo("Detect Tools Excluded", "5.0.0");
        property.setHelp("The tools Detect should not allow, in a comma-separated list. Excluded tools will not be run even if all criteria for the tool is met. Exclusion rules always win.", "This property and detect.tools provide control over which tools Detect runs.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_YARN_PROD_ONLY = new PropertyBuilder<>(new BooleanProperty("detect.yarn.prod.only", false)).build(property -> {
        property.setInfo("Include Yarn Production Dependencies Only", "4.0.0");
        property.setHelp("Set this to true to only scan production dependencies.");
        property.setGroups(DetectGroup.YARN, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
    });

    public static final EnumProperty  LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION = new PropertyBuilder<>(new EnumProperty("logging.level.com.synopsys.integration", LogLevel.INFO, LogLevel.class)).build(property -> {
        property.setInfo("Logging Level", "5.3.0");
        property.setHelp("The logging level of Detect.");
        property.setGroups(DetectGroup.LOGGING, DetectGroup.GLOBAL);
    });

    public static final EnumProperty  LOGGING_LEVEL_DETECT = new PropertyBuilder<>(new EnumProperty("logging.level.detect", LogLevel.INFO, LogLevel.class)).build(property -> {
        property.setInfo("Logging Level Shorthand", "5.5.0");
        property.setHelp("Shorthand for the logging level of detect. Equivalent to setting logging.level.com.synopsys.integration.");
        property.setGroups(DetectGroup.LOGGING, DetectGroup.GLOBAL);
    });

    public static final BooleanProperty  DETECT_WAIT_FOR_RESULTS = new PropertyBuilder<>(new BooleanProperty("detect.wait.for.results", false)).build(property -> {
        property.setInfo("Wait For Results", "5.5.0");
        property.setHelp("If set to true, Detect will wait for Synopsys products until results are available or the detect.report.timeout is exceeded.");
        property.setGroups(DetectGroup.GENERAL, DetectGroup.GLOBAL);
    });

    //#endregion Active Properties

    //#region Deprecated Properties
    static final String POLARIS_CLI_DEPRECATION_MESSAGE = "This property is being removed. Detect will no longer invoke the Polaris CLI.";

    @Deprecated
    public static final StringProperty  DETECT_BITBAKE_REFERENCE_IMPL = new PropertyBuilder<>(new StringProperty("detect.bitbake.reference.impl", "-poky-linux")).build(property -> {
        property.setInfo("Reference implementation", "4.4.0");
        property.setHelp("The reference implementation of the Yocto project. These characters are stripped from the discovered target architecture.");
        property.setGroups(DetectGroup.BITBAKE, DetectGroup.SOURCE_SCAN);
        property.setDeprecated("This property is no longer required and will not be used in the Bitbake Detector.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final LongProperty  DETECT_API_TIMEOUT = new PropertyBuilder<>(new LongProperty("detect.api.timeout", new Long(300000))).build(property -> {
        property.setInfo("Detect Api Timeout", "3.0.0");
        property.setHelp("Timeout for response from Black Duck regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.");
        property.setGroups(DetectGroup.PROJECT_INFO, DetectGroup.PROJECT);
        property.setDeprecated("This property is now deprecated. Please use --detect.report.timeout in the future. NOTE the new property is in SECONDS not MILLISECONDS.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_URL = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.url")).build(property -> {
        property.setInfo("Blackduck Hub Url", "3.0.0");
        property.setHelp("URL of the Hub server.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.url in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final IntegerProperty  BLACKDUCK_HUB_TIMEOUT = new PropertyBuilder<>(new IntegerProperty("blackduck.hub.timeout", 120)).build(property -> {
        property.setInfo("Blackduck Hub Timeout", "3.0.0");
        property.setHelp("The time to wait for rest connections to complete in seconds.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.timeout in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_USERNAME = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.username")).build(property -> {
        property.setInfo("Blackduck Hub Username", "3.0.0");
        property.setHelp("Hub username.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.username in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PASSWORD = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.password")).build(property -> {
        property.setInfo("Blackduck Hub Password", "3.0.0");
        property.setHelp("Hub password.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.password in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_API_TOKEN = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.api.token")).build(property -> {
        property.setInfo("Blackduck Hub Api Token", "3.1.0");
        property.setHelp("Hub API Token.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.api.token in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_HOST = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.host")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Host", "3.0.0");
        property.setHelp("Proxy host.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.host in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_PORT = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.port")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Port", "3.0.0");
        property.setHelp("Proxy port.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.port in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_USERNAME = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.username")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Username", "3.0.0");
        property.setHelp("Proxy username.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.username in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_PASSWORD = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.password")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Password", "3.0.0");
        property.setHelp("Proxy password.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.password in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_NTLM_DOMAIN = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.ntlm.domain")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Ntlm Domain", "3.1.0");
        property.setHelp("NTLM Proxy domain.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.ntlm.domain in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final StringListProperty  BLACKDUCK_HUB_PROXY_IGNORED_HOSTS = new PropertyBuilder<>(new StringListProperty("blackduck.hub.proxy.ignored.hosts", emptyList())).build(property -> {
        property.setInfo("Blackduck Hub Proxy Ignored Hosts", "3.2.0");
        property.setHelp("A comma-separated list of host patterns that should not use the proxy.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.ignored.hosts in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION = new PropertyBuilder<>(new NullableStringProperty("blackduck.hub.proxy.ntlm.workstation")).build(property -> {
        property.setInfo("Blackduck Hub Proxy Ntlm Workstation", "3.1.0");
        property.setHelp("NTLM Proxy workstation.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.PROXY);
        property.setDeprecated("This property is changing. Please use --blackduck.proxy.ntlm.workstation in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  BLACKDUCK_HUB_TRUST_CERT = new PropertyBuilder<>(new BooleanProperty("blackduck.hub.trust.cert", false)).build(property -> {
        property.setInfo("Blackduck Hub Trust Cert", "3.0.0");
        property.setHelp("If true, automatically trusts the certificate for the current run of Detect only.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --blackduck.trust.cert in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  BLACKDUCK_HUB_OFFLINE_MODE = new PropertyBuilder<>(new BooleanProperty("blackduck.hub.offline.mode", false)).build(property -> {
        property.setInfo("Blackduck Hub Offline Mode", "3.0.0");
        property.setHelp("This disables any Hub communication. If true, Detect does not upload BDIO files, does not check policies, and does not download and install the signature scanner.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.OFFLINE);
        property.setDeprecated("This property is changing. Please use --blackduck.offline.mode in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_DISABLE_WITHOUT_HUB = new PropertyBuilder<>(new BooleanProperty("detect.disable.without.hub", false)).build(property -> {
        property.setInfo("Detect Disable Without Hub", "4.0.0");
        property.setHelp("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER);
        property.setDeprecated("This property is changing. Please use --detect.ignore.connection.failures in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_DISABLE_WITHOUT_BLACKDUCK = new PropertyBuilder<>(new BooleanProperty("detect.disable.without.blackduck", false)).build(property -> {
        property.setInfo("Check For Valid Black Duck Connection", "4.2.0");
        property.setHelp("If true, during initialization Detect will check for Black Duck connectivity and exit with status code 0 if it cannot connect.");
        property.setGroups(DetectGroup.BLACKDUCK_SERVER, DetectGroup.BLACKDUCK, DetectGroup.DEFAULT);
        property.setDeprecated("This property is changing. Please use --detect.ignore.connection.failures in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_SUPPRESS_CONFIGURATION_OUTPUT = new PropertyBuilder<>(new BooleanProperty("detect.suppress.configuration.output", false)).build(property -> {
        property.setInfo("Detect Suppress Configuration Output", "3.0.0");
        property.setHelp("If true, the default behavior of printing your configuration properties at startup will be suppressed.");
        property.setGroups(DetectGroup.LOGGING);
        property.setDeprecated("This property is being removed. Configuration can no longer be suppressed individually. Log level can be used.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_SUPPRESS_RESULTS_OUTPUT = new PropertyBuilder<>(new BooleanProperty("detect.suppress.results.output", false)).build(property -> {
        property.setInfo("Detect Suppress Results Output", "3.0.0");
        property.setHelp("If true, the default behavior of printing the Detect Results will be suppressed.");
        property.setGroups(DetectGroup.LOGGING);
        property.setDeprecated("This property is being removed. Results can no longer be suppressed individually. Log level can be used.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_EXCLUDED_BOM_TOOL_TYPES = new PropertyBuilder<>(new NullableStringProperty("detect.excluded.bom.tool.types")).build(property -> {
        property.setInfo("Detect Excluded Bom Tool Types", "3.0.0");
        property.setHelp("By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.SOURCE_SCAN);
        property.setDeprecated("This property is changing. Please use --detect.excluded.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS = new PropertyBuilder<>(new BooleanProperty("detect.bom.tool.search.exclusion.defaults", true)).build(property -> {
        property.setInfo("Detect Bom Tool Search Exclusion Defaults", "3.2.0");
        property.setHelp("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the bom tool search: bin, build, .git, .gradle, node_modules, out, packages, target");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.detector.search.exclusion.defaults in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final StringListProperty  DETECT_BOM_TOOL_SEARCH_EXCLUSION = new PropertyBuilder<>(new StringListProperty("detect.bom.tool.search.exclusion", emptyList())).build(property -> {
        property.setInfo("Detect Bom Tool Search Exclusion", "3.2.0");
        property.setHelp("A comma-separated list of directory names to exclude from the bom tool search.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.detector.search.exclusion in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_INCLUDED_BOM_TOOL_TYPES = new PropertyBuilder<>(new NullableStringProperty("detect.included.bom.tool.types")).build(property -> {
        property.setInfo("Detect Included Bom Tool Types", "3.0.0");
        property.setHelp("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.included.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_PROJECT_BOM_TOOL = new PropertyBuilder<>(new NullableStringProperty("detect.project.bom.tool")).build(property -> {
        property.setInfo("Detect Project Bom Tool", "4.0.0");
        property.setHelp("The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.project.detector in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final IntegerProperty  DETECT_BOM_TOOL_SEARCH_DEPTH = new PropertyBuilder<>(new IntegerProperty("detect.bom.tool.search.depth", 0)).build(property -> {
        property.setInfo("Detect Bom Tool Search Depth", "3.2.0");
        property.setHelp("Depth of subdirectories within the source directory to search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.detector.search.depth in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_REQUIRED_BOM_TOOL_TYPES = new PropertyBuilder<>(new NullableStringProperty("detect.required.bom.tool.types")).build(property -> {
        property.setInfo("Detect Required Bom Tool Types", "4.3.0");
        property.setHelp("If set, Detect will fail if it does not find the bom tool types supplied here.");
        property.setGroups(DetectGroup.DETECTOR, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.required.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_BOM_TOOL_SEARCH_CONTINUE = new PropertyBuilder<>(new BooleanProperty("detect.bom.tool.search.continue", false)).build(property -> {
        property.setInfo("Detect Bom Tool Search Continue", "3.2.0");
        property.setHelp("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. " +
                                                                                                                                                                                         "If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project .");
        property.setGroups(DetectGroup.PATHS, DetectGroup.DETECTOR);
        property.setDeprecated("This property is changing. Please use --detect.detector.search.continue in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_GRADLE_INSPECTOR_REPOSITORY_URL = new PropertyBuilder<>(new NullableStringProperty("detect.gradle.inspector.repository.url")).build(property -> {
        property.setInfo("Detect Gradle Inspector Repository Url", "3.0.0");
        property.setHelp("The respository gradle should use to look for the gradle inspector dependencies.");
        property.setGroups(DetectGroup.GRADLE);
        property.setDeprecated("In the future, the gradle inspector will no longer be downloaded from a custom repository, please use Detect Air Gap instead.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final StringProperty  DETECT_NUGET_INSPECTOR_NAME = new PropertyBuilder<>(new StringProperty("detect.nuget.inspector.name", "IntegrationNugetInspector")).build(property -> {
        property.setInfo("Detect Nuget Inspector Name", "3.0.0");
        property.setHelp("Name of the Nuget Inspector package and the Nuget Inspector exe. (Do not include '.exe'.)", "The nuget inspector (previously) could be hosted on a custom nuget feed. In this case, Detect needed to know the name of the package to pull and the name of the exe file (which has to match). In the future, Detect will only retreive it from Artifactory or from Air Gap so a custom name is no longer supported.");
        property.setGroups(DetectGroup.NUGET);
        property.setDeprecated("In the future, Detect will not look for a custom named inspector.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullablePathProperty  DETECT_NUGET_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.nuget.path")).build(property -> {
        property.setInfo("Detect Nuget Path", "3.0.0");
        property.setHelp("The path to the Nuget executable. Nuget is used to download the classic inspectors nuget package.");
        property.setGroups(DetectGroup.NUGET);
        property.setDeprecated("In the future, Detect will no longer need a nuget executable as it will download the inspector from Artifactory exclusively.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN = new PropertyBuilder<>(new BooleanProperty("detect.hub.signature.scanner.dry.run", false)).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Dry Run", "3.0.0");
        property.setHelp("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.dry.run in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE = new PropertyBuilder<>(new BooleanProperty("detect.hub.signature.scanner.snippet.mode", false)).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Snippet Mode", "3.0.0");
        property.setHelp("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.snippet.mode in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final StringListProperty  DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.hub.signature.scanner.exclusion.patterns", emptyList())).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Exclusion Patterns", "3.0.0");
        property.setHelp("A comma-separated list of values to be used with the Signature Scanner --exclude flag.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.patterns in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final PathListProperty  DETECT_HUB_SIGNATURE_SCANNER_PATHS = new PropertyBuilder<>(new PathListProperty("detect.hub.signature.scanner.paths", emptyList())).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Paths", "3.0.0");
        property.setHelp("These paths and only these paths will be scanned.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.paths in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final StringListProperty  DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = new PropertyBuilder<>(new StringListProperty("detect.hub.signature.scanner.exclusion.name.patterns", Arrays.asList("node_modules"))).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Exclusion Name Patterns", "4.0.0");
        property.setHelp("A comma-separated list of directory name patterns Detect will search for and add to the Signature Scanner --exclude flag values.", "Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner. " +
                                                                                                                                                        "These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.name.patterns in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final IntegerProperty  DETECT_HUB_SIGNATURE_SCANNER_MEMORY = new PropertyBuilder<>(new IntegerProperty("detect.hub.signature.scanner.memory", 4096)).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Memory", "3.0.0");
        property.setHelp("The memory for the scanner to use.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.memory in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_HUB_SIGNATURE_SCANNER_DISABLED =  new PropertyBuilder<>(new BooleanProperty("detect.hub.signature.scanner.disabled", false)).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Disabled", "3.0.0");
        property.setHelp("Set to true to disable the Hub Signature Scanner.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.tools in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.disabled", false)).build(property -> {
        property.setInfo("Detect Blackduck Signature Scanner Disabled", "4.2.0");
        property.setHelp("Set to true to disable the Black Duck Signature Scanner.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.BLACKDUCK);
        property.setDeprecated("This property is changing. Please use --detect.tools in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullablePathProperty  DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.hub.signature.scanner.offline.local.path")).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Offline Local Path", "3.0.0");
        property.setHelp("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.OFFLINE);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.offline.local.path in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullablePathProperty  DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH = new PropertyBuilder<>(new NullablePathProperty("detect.hub.signature.scanner.local.path")).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Local Path", "4.2.0");
        property.setHelp("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.OFFLINE);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.local.path in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_HUB_SIGNATURE_SCANNER_HOST_URL = new PropertyBuilder<>(new NullableStringProperty("detect.hub.signature.scanner.host.url")).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Host Url", "3.0.0");
        property.setHelp("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.host.url in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final IntegerProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = new PropertyBuilder<>(new IntegerProperty("detect.blackduck.signature.scanner.parallel.processors", 1)).build(property -> {
        property.setInfo("Signature Scanner Parallel Processors", "4.2.0");
        property.setHelp("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL);
        property.setCategory(DetectCategory.Advanced);
        property.setDeprecated("This property is changing. Please use --detect.parallel.processors in the future. The --detect.parallel.processors property will take precedence over this property.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final IntegerProperty  DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = new PropertyBuilder<>(new IntegerProperty("detect.hub.signature.scanner.parallel.processors", 1)).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Parallel Processors", "3.0.0");
        property.setHelp("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.parallel.processors in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS = new PropertyBuilder<>(new NullableStringProperty("detect.hub.signature.scanner.arguments")).build(property -> {
        property.setInfo("Detect Hub Signature Scanner Arguments", "4.0.0");
        property.setHelp("Additional arguments to use when running the Hub signature scanner.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER);
        property.setDeprecated("This property is changing. Please use --detect.blackduck.signature.scanner.arguments in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_SWIP_ENABLED = new PropertyBuilder<>(new BooleanProperty("detect.polaris.enabled", false)).build(property -> {
        property.setInfo("Detect Polaris Enabled", "4.4.0");
        property.setHelp("Set to false to disable the Synopsys Polaris Tool.");
        property.setGroups(DetectGroup.POLARIS);
        property.setDeprecated("This property is changing. Please use --detect.tools and POLARIS in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final EnumProperty  LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION = new PropertyBuilder<>(new EnumProperty("logging.level.com.blackducksoftware.integration", LogLevel.INFO, LogLevel.class)).build(property -> {
        property.setInfo("Logging Level", "3.0.0");
        property.setHelp("The logging level of Detect.");
        property.setGroups(DetectGroup.LOGGING, DetectGroup.GLOBAL);
        property.setDeprecated("This property is changing. Please use --logging.level.com.synopsys.integration in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  DETECT_MAVEN_SCOPE = new PropertyBuilder<>(new NullableStringProperty("detect.maven.scope")).build(property -> {
        property.setInfo("Dependency Scope Included", "3.0.0");
        property.setHelp("The name of a Maven scope. Output will be limited to dependencies with this scope.", "If set, Detect will include only dependencies of the given Maven scope.");
        property.setGroups(DetectGroup.MAVEN, DetectGroup.SOURCE_SCAN);
        property.setDeprecated("This property is changing. Please use --detect.maven.included.scope in the future.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final BooleanProperty  DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE = new PropertyBuilder<>(new BooleanProperty("detect.blackduck.signature.scanner.snippet.mode", false)).build(property -> {
        property.setInfo("Snippet Scanning", "4.2.0");
        property.setHelp("If set to true, the signature scanner will, if supported by your Black Duck version, run in snippet scanning mode.");
        property.setGroups(DetectGroup.SIGNATURE_SCANNER, DetectGroup.GLOBAL, DetectGroup.SOURCE_SCAN);
        property.setDeprecated("This property is now deprecated. Please use --detect.blackduck.signature.scanner.snippet.matching in the future. NOTE the new property is one of a particular set of values. You will need to consult the documentation for the Signature Scanner in Black Duck for details.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN);
    });

    @Deprecated
    public static final NullableStringProperty  POLARIS_URL = new PropertyBuilder<>(new NullableStringProperty("polaris.url")).build(property -> {
        property.setInfo("Polaris Url", "4.1.0");
        property.setHelp("The url of your polaris instance.");
        property.setGroups(DetectGroup.POLARIS, DetectGroup.DEFAULT, DetectGroup.GLOBAL);
        property.setDeprecated(POLARIS_CLI_DEPRECATION_MESSAGE, DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final NullableStringProperty  POLARIS_ACCESS_TOKEN = new PropertyBuilder<>(new NullableStringProperty("polaris.access.token")).build(property -> {
        property.setInfo("Polaris Access Token", "5.3.0");
        property.setHelp("The access token for your polaris instance.");
        property.setGroups(DetectGroup.POLARIS, DetectGroup.DEFAULT, DetectGroup.GLOBAL);
        property.setDeprecated(POLARIS_CLI_DEPRECATION_MESSAGE, DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final NullableStringProperty  POLARIS_ARGUMENTS = new PropertyBuilder<>(new NullableStringProperty("polaris.arguments")).build(property -> {
        property.setInfo("Polaris Arguments", "5.3.0");
        property.setHelp("Additional arguments to pass to polaris separated by space. The polaris.command takes precedence.");
        property.setGroups(DetectGroup.POLARIS, DetectGroup.DEFAULT, DetectGroup.SOURCE_SCAN);
        property.setDeprecated(POLARIS_CLI_DEPRECATION_MESSAGE, DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    @Deprecated
    public static final NullableStringProperty  POLARIS_COMMAND = new PropertyBuilder<>(new NullableStringProperty("polaris.command")).build(property -> {
        property.setInfo("Polaris Command", "6.0.0");
        property.setHelp("A replacement command to pass to polaris separated by space. Include the analyze or setup command itself. If specified, polaris.arguments will be ignored and this will take precedence.");
        property.setGroups(DetectGroup.POLARIS, DetectGroup.DEFAULT, DetectGroup.SOURCE_SCAN);
        property.setDeprecated(POLARIS_CLI_DEPRECATION_MESSAGE, DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT);
    });

    public DetectPropertiesJava() throws InstantiationException, IllegalAccessException {}
}
