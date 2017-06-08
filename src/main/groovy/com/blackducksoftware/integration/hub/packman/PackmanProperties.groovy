package com.blackducksoftware.integration.hub.packman

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
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

    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true")
    @Value('${packman.cleanup.bdio.files}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="URL of the Hub server")
    @Value('${packman.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120")
    @Value('${packman.hub.timeout}')
    Integer hubTimeout

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
    Integer hubProxyPort

    @ValueDescription(description="Proxy username")
    @Value('${packman.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password")
    @Value('${packman.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true the Hub https certificate will be automatically imported", defaultValue="false")
    @Value('${packman.hub.auto.import.cert}')
    Boolean hubAutoImportCertificate

    @ValueDescription(description = "Source paths to inspect")
    @Value('${packman.source.paths}')
    String[] sourcePaths

    @ValueDescription(description = "Output path")
    @Value('${packman.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue="10")
    @Value('${packman.search.depth}')
    Integer searchDepth

    @ValueDescription(description = "Specify which tools to use")
    @Value('${packman.bom.tool.type.override}')
    String bomToolTypeOverride

    @ValueDescription(description = "Hub project name")
    @Value('${packman.project.name}')
    String projectName

    @ValueDescription(description = "Hub project version")
    @Value('${packman.project.version}')
    String projectVersionName

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="0.0.6")
    @Value('${packman.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", defaultValue="dependencies")
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

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector")
    @Value('${packman.nuget.inspector.name}')
    String inspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="0.0.3-alpha")
    @Value('${packman.nuget.inspector.version}')
    String inspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude")
    @Value('${packman.nuget.excluded.modules}')
    String inspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false")
    @Value('${packman.nuget.ignore.failure}')
    Boolean inspectorIgnoreFailure

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom", defaultValue="true")
    @Value('${packman.maven.aggregate}')
    Boolean mavenAggregateBom

    @ValueDescription(description="The name of the dependency scope to include")
    @Value('${packman.maven.scope}')
    String mavenScope

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${packman.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable")
    @Value('${packman.maven.path}')
    String mavenPath

    @ValueDescription(description="If true all nuget projects will be aggregated into a single bom", defaultValue="false")
    @Value('${packman.nuget.aggregate}')
    Boolean nugetAggregateBom

    @ValueDescription(description="The path of the Nuget executable")
    @Value('${packman.nuget.path}')
    String nugetPath

    @ValueDescription(description="If true creates a temporary Python virtual environment", defaultValue="true")
    @Value('${packman.pip.createVirtualEnv}')
    Boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path", defaultValue="false")
    @Value('${packman.pip.pip3}')
    Boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable")
    @Value('${packman.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable")
    @Value('${packman.pip.path}')
    String pipPath

    @ValueDescription(description="Path of the GoDep executable")
    @Value('${packman.godep.path}')
    String godepPath

    @ValueDescription(description="If true all Go results will be aggregated into a single bom", defaultValue="true")
    @Value('${packman.go.aggregate}')
    Boolean goAggregate

    @PostConstruct
    void init() {
        setDefaultValues()
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

    private void setDefaultValues(){
        this.getClass().declaredFields.each { field ->
            if (field.isAnnotationPresent(ValueDescription.class)) {
                String defaultValue = ''
                final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class)
                defaultValue = valueDescription.defaultValue()
                field.setAccessible(true);
                Object fieldValue = field.get(this)
                if (defaultValue?.trim()) {
                    try {
                        Class type = field.getType()
                        if (String.class.equals(type) && StringUtils.isBlank(fieldValue)) {
                            field.set(this, defaultValue);
                        } else if (Integer.class.equals(type) && fieldValue == null) {
                            field.set(this, NumberUtils.toInt(defaultValue));
                        } else if (Boolean.class.equals(type) && fieldValue == null) {
                            field.set(this, Boolean.parseBoolean(defaultValue));
                        }
                    } catch (final IllegalAccessException e) {
                        logger.error(String.format("Could not set defaultValue on field %s with %s: %s", field.getName(), defaultValue, e.getMessage()));
                    }
                }
            }
        }
    }
}
