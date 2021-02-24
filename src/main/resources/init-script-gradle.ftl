import java.util.Optional

import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import com.blackducksoftware.integration.gradle.DependencyGathererUtil

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
    task gatherDependencies(type: DefaultTask) {
        doLast {
            println "Gathering dependencies for " + project.name
        }
    }
    afterEvaluate { project ->
        project.tasks.getByName('dependencies') {
            ext {
                excludedProjectNames = '${excludedProjectNames}'
                includedProjectNames = '${includedProjectNames}'
                excludedConfigurationNames = '${excludedConfigurationNames}'
                includedConfigurationNames = '${includedConfigurationNames}'
                outputDirectoryPath = System.getProperty('GRADLEEXTRACTIONDIR')
            }
            doFirst {
                DependencyGathererUtil dependencyUtil = new DependencyGathererUtil()
                dependencyUtil.generateRootProjectMetaData(project, outputDirectoryPath)
                setConfigurations(dependencyUtil.filterConfigurations(project, excludedConfigurationNames, includedConfigurationNames))
                Optional<File> projectOutputFile = dependencyUtil.getProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectOutputFile.isPresent()) {
                    File projectFile = dependencyUtil.createProjectOutputFile(projectOutputFile.get())
                    setOutputFile(projectFile)
                }
            }

            doLast {
                DependencyGathererUtil dependencyUtil = new DependencyGathererUtil()
                Optional<File> projectFile = dependencyUtil.getProjectOutputFile(project, outputDirectoryPath, excludedProjectNames, includedProjectNames)
                if(projectFile.isPresent()) {
                    dependencyUtil.createProjectMetadata(project, projectFile.get())
                }
            }
        }
        project.gatherDependencies.finalizedBy(project.tasks.getByName('dependencies'))
        project.gatherDependencies
    }
}
