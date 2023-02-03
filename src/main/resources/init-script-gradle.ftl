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
        classpath 'com.synopsys.integration:integration-common:26.0.4'
</#if>
    }
}

ExcludedIncludedFilter projectNameFilter = ExcludedIncludedWildcardFilter.fromCommaSeparatedStrings('${excludedProjectNames}', '${includedProjectNames}')
ExcludedIncludedFilter projectPathFilter = ExcludedIncludedWildcardFilter.fromCommaSeparatedStrings('${excludedProjectPaths}', '${includedProjectPaths}')
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

                if(projectNameFilter.shouldInclude(project.name) && projectPathFilter.shouldInclude(project.path)) {
                    def dependencyTask = project.tasks.getByName('dependencies')
                    File projectOutputFile = findProjectOutputFile(project, outputDirectoryPath)
                    File projectFile = createProjectOutputFile(projectOutputFile)

                    if(dependencyTask.metaClass.respondsTo(dependencyTask, "setConfigurations")) {
                        println "Updating configurations for task"
                        // modify the configurations for the dependency task
                        setConfigurations(filterConfigurations(project, excludedConfigurationNames, includedConfigurationNames))

                    } else {
                        println "Could not find method 'setConfigurations'"
                    }

                    if(dependencyTask.metaClass.respondsTo(dependencyTask,"setOutputFile")) {
                        println "Updating output file for task to "+projectFile.getAbsolutePath()
                        // modify the output file
                        setOutputFile(projectFile)
                    } else {
                        println "Could not find method 'setOutputFile'"
                    }
                } else {
                    println "Excluding from results subproject: " + project.path
                }
            }

            doLast {
                if(projectNameFilter.shouldInclude(project.name) && projectPathFilter.shouldInclude(project.path)) {
                    File projectFile = findProjectOutputFile(project, outputDirectoryPath)
                    appendProjectMetadata(project, projectFile)
                }
            }
        }
        // this forces the dependencies task to be run which will write the content to the modified output file
        project.gatherDependencies.finalizedBy(project.tasks.getByName('dependencies'))
        project.gatherDependencies
    }
}

// ## START methods invoked by tasks above
<#-- Do not parse with Freemarker because Groovy variable replacement in template strings is the same as Freemarker template syntax. -->
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
        String rootProjectDirectory = rootProject.getProjectDir().getCanonicalPath()
        String rootProjectPath = rootProject.path.toString()
        String rootProjectGroup = rootProject.group.toString()
        String rootProjectName = rootProject.name.toString()
        String rootProjectVersionName = rootProject.version.toString()

        def rootProjectMetadataPieces = []
        rootProjectMetadataPieces.add('DETECT META DATA START')
        rootProjectMetadataPieces.add("rootProjectDirectory:${rootProjectDirectory}")
        rootProjectMetadataPieces.add("rootProjectPath:${rootProjectPath}")
        rootProjectMetadataPieces.add("rootProjectGroup:${rootProjectGroup}")
        rootProjectMetadataPieces.add("rootProjectName:${rootProjectName}")
        rootProjectMetadataPieces.add("rootProjectVersion:${rootProjectVersionName}")
        rootProjectMetadataPieces.add('DETECT META DATA END')
        rootOutputFile << rootProjectMetadataPieces.join('\n')
    }
}

def findProjectOutputFile(Project project, String outputDirectoryPath) {
    File outputDirectory = createTaskOutputDirectory(outputDirectoryPath)
    String name = project.toString()

    String nameForFile = new IntegrationEscapeUtil().replaceWithUnderscore(name)
    File outputFile = new File(outputDirectory, "${nameForFile}_dependencyGraph.txt")

    outputFile
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
    String rootProjectPath = rootProject.path.toString()
    String group = project.group.toString()
    String name = project.name.toString()
    String version = project.version.toString()
    String path = project.path.toString()

    def metaDataPieces = []
    metaDataPieces.add('')
    metaDataPieces.add('DETECT META DATA START')
    metaDataPieces.add("rootProjectDirectory:${rootProject.getProjectDir().getCanonicalPath()}")
    metaDataPieces.add("rootProjectGroup:${rootProjectGroup}")
    metaDataPieces.add("rootProjectPath:${rootProjectPath}")
    metaDataPieces.add("rootProjectName:${rootProjectName}")
    metaDataPieces.add("rootProjectVersion:${rootProjectVersionName}")
    metaDataPieces.add("projectDirectory:${project.getProjectDir().getCanonicalPath()}")
    metaDataPieces.add("projectGroup:${group}")
    metaDataPieces.add("projectName:${name}")
    metaDataPieces.add("projectVersion:${version}")
    metaDataPieces.add("projectPath:${path}")
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
