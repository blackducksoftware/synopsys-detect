import java.util.Optional

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import com.synopsys.integration.util.ExcludedIncludedFilter
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter
import com.synopsys.integration.util.IntegrationEscapeUtil

initscript {
    repositories {
<#if airGapLibsPath?has_content>
        flatDir {
            dirs '${airGapLibsPath}'
        }
<#else>
        mavenLocal()
        maven {
            name 'GradleInspectorRepository'
            url '${customRepositoryUrl}'
        }
</#if>
    }

    dependencies {
<#if airGapLibsPath?has_content>
        new File('${airGapLibsPath}').eachFile {
            String fileName = it.name.find('.*\\.jar')?.replace('.jar', '')
            if (fileName) {
                classpath name: fileName
            }
        }
<#else>
        classpath 'com.synopsys.integration:integration-common:24.2.1'
</#if>
    }
}

gradle.allprojects {
    // add a new task to each project to start the process of getting the dependencies
    task gatherDependencies(type: DefaultTask) {
        doLast {
            println "Gathering dependencies for " + project.name
        }
    }
    afterEvaluate { project ->
        // after a project has been evaluated modify the dependencies task for that project to output to a specific file.
        project.tasks.getByName('dependencies') {
            ext {
                excludedProjectNames = '${excludedProjectNames}'
                includedProjectNames = '${includedProjectNames}'
                excludedConfigurationNames = '${excludedConfigurationNames}'
                includedConfigurationNames = '${includedConfigurationNames}'
                outputDirectoryPath = System.getProperty('GRADLEEXTRACTIONDIR')
            }
            doFirst {
                generateRootProjectMetaData(project, outputDirectoryPath)

                // this will be empty if the project should not be included.
                Optional<File> projectOutputFile = findProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectOutputFile.isPresent()) {
                    File projectFile = createProjectOutputFile(projectOutputFile.get())

                    // modify the configurations for the dependency task and the output file
                    setConfigurations(filterConfigurations(project, excludedConfigurationNames, includedConfigurationNames))
                    setOutputFile(projectFile)
                }
            }

            doLast {
                // this will be empty if this project should not be included.
                Optional<File> projectFile = findProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectFile.isPresent()) {
                    appendProjectMetadata(project, projectFile.get())
                }
            }
        }
        // this forces the dependencies task to be run which will write the content to the modified output file
        project.gatherDependencies.finalizedBy(project.tasks.getByName('dependencies'))
        project.gatherDependencies
    }
}

// ## START methods invoked by tasks above
<#noparse>
def generateRootProjectMetaData(Project project, String outputDirectoryPath) {
    File outputDirectory = createTaskOutputDirectory(outputDirectoryPath)
    outputDirectory.mkdirs()

    Project rootProject = project.gradle.rootProject;
    /* if the current project is the root project then generate the file containing
       the meta data for the root project otherwise ignore.
     */
    if (project.name.equals(rootProject.name)) {
        File rootOutputFile = new File(outputDirectory, 'rootProjectMetadata.txt');
        String rootProjectPath = rootProject.getProjectDir().getCanonicalPath()
        String rootProjectGroup = rootProject.group.toString()
        String rootProjectName = rootProject.name.toString()
        String rootProjectVersionName = rootProject.version.toString()

        def rootProjectMetadataPieces = []
        rootProjectMetadataPieces.add('DETECT META DATA START')
        rootProjectMetadataPieces.add("rootProjectPath:${rootProjectPath}")
        rootProjectMetadataPieces.add("rootProjectGroup:${rootProjectGroup}")
        rootProjectMetadataPieces.add("rootProjectName:${rootProjectName}")
        rootProjectMetadataPieces.add("rootProjectVersion:${rootProjectVersionName}")
        rootProjectMetadataPieces.add('DETECT META DATA END')
        rootOutputFile << rootProjectMetadataPieces.join('\n')
    }
}

def findProjectOutputFile(Project project, String outputDirectoryPath, String excludedProjectNames, String includedProjectNames) {
    Optional<File> projectOutputFile = Optional.empty()
    ExcludedIncludedFilter projectFilter = ExcludedIncludedWildcardFilter.fromCommaSeparatedStrings(excludedProjectNames, includedProjectNames)
    // check if the project should be included.  If it should be included then return the file that will contain dependency data
    if (projectFilter.shouldInclude(project.name)) {
        File outputDirectory = createTaskOutputDirectory(outputDirectoryPath)
        String name = project.name.toString()

        String nameForFile = new IntegrationEscapeUtil().replaceWithUnderscore(name)
        File outputFile = new File(outputDirectory, "${nameForFile}_dependencyGraph.txt")
        projectOutputFile = Optional.of(outputFile)
    }
    projectOutputFile
}

def filterConfigurations(Project project, String excludedConfigurationNames, String includedConfigurationNames) {
    ExcludedIncludedFilter configurationFilter = ExcludedIncludedWildcardFilter.fromCommaSeparatedStrings(excludedConfigurationNames, includedConfigurationNames)
    Set<Configuration> filteredConfigurationSet = new TreeSet<Configuration>(new Comparator<Configuration>() {
        public int compare(Configuration conf1, Configuration conf2) {
            return conf1.getName().compareTo(conf2.getName());
        }
    })
    for (Configuration configuration : project.configurations) {
        if (configurationFilter.shouldInclude(configuration.name)) {
            filteredConfigurationSet.add(configuration)
        }
    }

    filteredConfigurationSet
}

def appendProjectMetadata(Project project, File projectOutputFile) {
    Project rootProject = project.gradle.rootProject;
    String rootProjectGroup = rootProject.group.toString()
    String rootProjectName = rootProject.name.toString()
    String rootProjectVersionName = rootProject.version.toString()
    String group = project.group.toString()
    String name = project.name.toString()
    String version = project.version.toString()

    def metaDataPieces = []
    metaDataPieces.add('')
    metaDataPieces.add('DETECT META DATA START')
    metaDataPieces.add("rootProjectPath:${rootProject.getProjectDir().getCanonicalPath()}")
    metaDataPieces.add("rootProjectGroup:${rootProjectGroup}")
    metaDataPieces.add("rootProjectName:${rootProjectName}")
    metaDataPieces.add("rootProjectVersion:${rootProjectVersionName}")
    metaDataPieces.add("projectPath:${project.getProjectDir().getCanonicalPath()}")
    metaDataPieces.add("projectGroup:${group}")
    metaDataPieces.add("projectName:${name}")
    metaDataPieces.add("projectVersion:${version}")
    metaDataPieces.add('DETECT META DATA END')
    metaDataPieces.add('')

    projectOutputFile << metaDataPieces.join('\n')
}

def createProjectOutputFile(File projectFile) {
    if (projectFile.exists()) {
        projectFile.delete()
    }

    projectFile.createNewFile()
    projectFile
}

def createTaskOutputDirectory(String outputDirectoryPath) {
    File outputDirectory = new File(outputDirectoryPath)
    outputDirectory.mkdirs()

    outputDirectory
}
</#noparse>
// ## END methods invoked by tasks above
