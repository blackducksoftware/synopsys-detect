package com.blackducksoftware.integration.hub.packman

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

import com.blackducksoftware.integration.hub.boss.exception.BossException
import com.blackducksoftware.integration.hub.packman.help.ValueDescription

@Component
class PackmanProperties {
    private final Logger logger = LoggerFactory.getLogger(PackmanProperties.class)

    static final String DOCKER_PROPERTY_PREFIX = 'packman.docker.passthrough.'

    @Autowired
    ConfigurableEnvironment configurableEnvironment

    Set<String> additionalDockerPropertyNames = new HashSet<>()

    @ValueDescription(description="If true the bdio files will be deleted after upload")
    @Value('${packman.cleanup.bdio.files}')
    String cleanupBdioFiles

    @ValueDescription(description="URL of the Hub server")
    @Value('${packman.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete")
    @Value('${packman.hub.timeout}')
    String hubTimeout

    @ValueDescription(description="Hub username")
    @Value('${packman.hub.username}')
    String hubUsername

    @ValueDescription(description="Hub password")
    @Value('${packman.hub.password}')
    String hubPassword

    @ValueDescription(description="Proxy host")
    @Value('${packman.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(description="Proxy port")
    @Value('${packman.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(description="Proxy username")
    @Value('${packman.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password")
    @Value('${packman.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true the Hub https certificate will be automatically imported")
    @Value('${packman.hub.auto.import.cert}')
    String hubAutoImportCertificate

    @ValueDescription(description = "Source paths to inspect")
    @Value('${packman.source.paths}')
    String[] sourcePaths

    @ValueDescription(description = "Output path")
    @Value('${packman.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.")
    @Value('${packman.search.depth}')
    String searchDepth

    @ValueDescription(description = "Specify which tools to use")
    @Value('${packman.bom.tool.type.override}')
    String bomToolTypeOverride

    @ValueDescription(description = "Hub project name")
    @Value('${packman.project.name}')
    String projectName

    @ValueDescription(description = "Hub project version")
    @Value('${packman.project.version}')
    String projectVersionName

    @ValueDescription(description="Version of the Gradle Inspector")
    @Value('${packman.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command")
    @Value('${packman.gradle.build.command}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude")
    @Value('${packman.gradle.excluded.configurations}')
    String excludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include")
    @Value('${packman.gradle.included.configurations}')
    String includedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude")
    @Value('${packman.gradle.excluded.projects}')
    String excludedProjectNames

    @ValueDescription(description="The names of the projects to include")
    @Value('${packman.gradle.included.projects}')
    String includedProjectNames

    @ValueDescription(description="Name of the Nuget Inspector")
    @Value('${packman.nuget.inspector.name}')
    String inspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector")
    @Value('${packman.nuget.inspector.version}')
    String inspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude")
    @Value('${packman.nuget.excluded.modules}')
    String inspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.")
    @Value('${packman.nuget.ignore.failure}')
    boolean inspectorIgnoreFailure

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom")
    @Value('${packman.maven.aggregate}')
    boolean mavenAggregateBom

    @ValueDescription(description="The name of the dependency scope to include")
    @Value('${packman.maven.scope}')
    String mavenScope

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${packman.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable")
    @Value('${packman.maven.path}')
    String mavenPath

    @ValueDescription(description="If true all nuget projects will be aggregated into a single bom")
    @Value('${packman.nuget.aggregate}')
    boolean nugetAggregateBom

    @ValueDescription(description="The path of the Nuget executable")
    @Value('${packman.nuget.path}')
    String nugetPath

    @ValueDescription(description="If true creates a temporary Python virtual environment")
    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path")
    @Value('${packman.pip.pip3}')
    boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable")
    @Value('${packman.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable")
    @Value('${packman.pip.path}')
    String pipPath

    @ValueDescription(description="Path of the GoDep executable")
    @Value('${packman.godep.path}')
    String godepPath

    @ValueDescription(description="If true all Go results will be aggregated into a single bom")
    @Value('${packman.go.aggregate}')
    boolean goAggregate

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
            throw new BossException("The output directory ${outputDirectoryPath} does not exist. The system property 'user.home' will be used by default, but the output directory must exist.")
        }

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
