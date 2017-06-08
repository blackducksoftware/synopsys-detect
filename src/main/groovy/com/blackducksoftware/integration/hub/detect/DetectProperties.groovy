package com.blackducksoftware.integration.hub.detect

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.detect.help.ValueDescription

@Component
class DetectProperties {
    private final Logger logger = LoggerFactory.getLogger(DetectProperties.class)

    static final String DOCKER_PROPERTY_PREFIX = 'detect.docker.passthrough.'

    @Autowired
    ConfigurableEnvironment configurableEnvironment

    Set<String> additionalDockerPropertyNames = new HashSet<>()

    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true")
    @Value('${detect.cleanup.bdio.files}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="URL of the Hub server")
    @Value('${detect.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120")
    @Value('${detect.hub.timeout}')
    Integer hubTimeout

    @ValueDescription(description="Hub username")
    @Value('${detect.hub.username}')
    String hubUsername

    @ValueDescription(description="Hub password")
    @Value('${detect.hub.password}')
    String hubPassword

    @ValueDescription(description="Proxy host")
    @Value('${detect.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(description="Proxy port")
    @Value('${detect.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(description="Proxy username")
    @Value('${detect.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password")
    @Value('${detect.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true the Hub https certificate will be automatically imported", defaultValue="false")
    @Value('${detect.hub.auto.import.cert}')
    Boolean hubAutoImportCertificate

    @ValueDescription(description = "Source paths to inspect")
    @Value('${detect.source.paths}')
    String[] sourcePaths

    @ValueDescription(description = "Output path")
    @Value('${detect.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue="10")
    @Value('${detect.search.depth}')
    Integer searchDepth

    @ValueDescription(description = "Specify which tools to use")
    @Value('${detect.bom.tool.type.override}')
    String bomToolTypeOverride

    @ValueDescription(description = "Hub project name")
    @Value('${detect.project.name}')
    String projectName

    @ValueDescription(description = "Hub project version")
    @Value('${detect.project.version}')
    String projectVersionName

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="0.0.6")
    @Value('${detect.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", defaultValue="dependencies")
    @Value('${detect.gradle.build.command}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude")
    @Value('${detect.gradle.excluded.configurations}')
    String excludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include")
    @Value('${detect.gradle.included.configurations}')
    String includedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude")
    @Value('${detect.gradle.excluded.projects}')
    String excludedProjectNames

    @ValueDescription(description="The names of the projects to include")
    @Value('${detect.gradle.included.projects}')
    String includedProjectNames

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector")
    @Value('${detect.nuget.inspector.name}')
    String inspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="0.0.3-alpha")
    @Value('${detect.nuget.inspector.version}')
    String inspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude")
    @Value('${detect.nuget.excluded.modules}')
    String inspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false")
    @Value('${detect.nuget.ignore.failure}')
    Boolean inspectorIgnoreFailure

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom", defaultValue="true")
    @Value('${detect.maven.aggregate}')
    Boolean mavenAggregateBom

    @ValueDescription(description="The name of the dependency scope to include")
    @Value('${detect.maven.scope}')
    String mavenScope

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${detect.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable")
    @Value('${detect.maven.path}')
    String mavenPath

    @ValueDescription(description="If true all nuget projects will be aggregated into a single bom", defaultValue="false")
    @Value('${detect.nuget.aggregate}')
    Boolean nugetAggregateBom

    @ValueDescription(description="The path of the Nuget executable")
    @Value('${detect.nuget.path}')
    String nugetPath

    @ValueDescription(description="If true creates a temporary Python virtual environment", defaultValue="true")
    @Value('${detect.pip.create.virtual.env}')
    Boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path", defaultValue="false")
    @Value('${detect.pip.pip3}')
    Boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable")
    @Value('${detect.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable")
    @Value('${detect.pip.path}')
    String pipPath

    @ValueDescription(description="The path to a user's virtual environment")
    @Value('${detect.pip.virtualEnv.path}')
    String virtualEnvPath

    @ValueDescription(description="The path of the requirements.txt file")
    @Value('${detect.pip.requirements.path}')
    String requirementsFilePath

    @ValueDescription(description="Path of the GoDep executable")
    @Value('${detect.godep.path}')
    String godepPath

    @ValueDescription(description="If true all Go results will be aggregated into a single bom", defaultValue="true")
    @Value('${detect.go.aggregate}')
    Boolean goAggregate

    @PostConstruct
    void init() {
        if (sourcePaths == null || sourcePaths.length == 0) {
            sourcePaths = [
                System.getProperty('user.dir')
            ] as String[]
        }

        if (StringUtils.isBlank(outputDirectoryPath)) {
            outputDirectoryPath = System.getProperty('user.home') + File.separator + 'blackduck'
        }

        File outputDirectory = new File(outputDirectoryPath)
        outputDirectory.mkdirs()
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new DetectException("The output directory ${outputDirectoryPath} does not exist. The system property 'user.home' will be used by default, but the output directory must exist.")
        }
        outputDirectoryPath = outputDirectoryPath.trim()

        // Nuget
        inspectorPackageName = inspectorPackageName.trim()
        inspectorPackageVersion = inspectorPackageVersion.trim()

        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources()
        mutablePropertySources.each { propertySource ->
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource
                enumerablePropertySource.propertyNames.each { propertyName ->
                    if (propertyName && propertyName.startsWith(DOCKER_PROPERTY_PREFIX)) {
                        additionalDockerPropertyNames.add(propertyName)
                    }
                }
            }
        }
    }

    public String getDetectProperty(String key) {
        configurableEnvironment.getProperty(key)
    }
}
