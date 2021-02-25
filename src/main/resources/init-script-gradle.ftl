import java.util.Optional

import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import com.blackducksoftware.integration.gradle.DependencyDataUtil

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
        classpath 'com.synopsys.integration:integration-gradle-inspector:${gradleInspectorVersion}'
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
                DependencyDataUtil dependencyUtil = new DependencyDataUtil()
                dependencyUtil.generateRootProjectMetaData(project, outputDirectoryPath)

                // this will be empty if the project should not be included.
                Optional<File> projectOutputFile = dependencyUtil.getProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectOutputFile.isPresent()) {
                    File projectFile = dependencyUtil.createProjectOutputFile(projectOutputFile.get())

                    // modify the configurations for the dependency task and the output file
                    setConfigurations(dependencyUtil.filterConfigurations(project, excludedConfigurationNames, includedConfigurationNames))
                    setOutputFile(projectFile)
                }
            }

            doLast {
                DependencyDataUtil dependencyUtil = new DependencyDataUtil()

                // this will be empty if this project should not be included.
                Optional<File> projectFile = dependencyUtil.getProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectFile.isPresent()) {
                    dependencyUtil.appendProjectMetadata(project, projectFile.get())
                }
            }
        }
        // this forces the dependencies task to be run which will write the content to the modified output file
        project.gatherDependencies.finalizedBy(project.tasks.getByName('dependencies'))
        project.gatherDependencies
    }
}
