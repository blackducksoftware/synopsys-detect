import java.nio.charset.StandardCharsets

import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import com.blackducksoftware.integration.gradle.DependencyGatherer

initscript {
<#if airGapLibsPath??>
    println 'Running air gapped from ${airGapLibsPath}'
<#elseif customRepositoryUrl??>
    println 'Running in online mode with url: ${customRepositoryUrl}'
<#else>
    println 'Running in online mode'
</#if>
    repositories {
<#if airGapLibsPath??>
        flatDir {
            dirs '${airGapLibsPath}'
        }
<#elseif customRepositoryUrl??>
        mavenLocal()
        maven {
            name 'UserDefinedRepository'
            url '${customRepositoryUrl}'
        }
<#else>
        mavenLocal()
        mavenCentral()
</#if>
    }

    dependencies {
<#if airGapLibsPath??>
        new File('${airGapLibsPath}').eachFile {
            String fileName = it.name.find('.*\\.jar')?.replace('.jar', '')
            if (fileName) {
                classpath name: fileName 
            }
        }
<#else>
        classpath 'com.blackducksoftware.integration:integration-gradle-inspector:${gradleInspectorVersion}'
</#if>
    }
}

addListener(
    new TaskExecutionListener() {
        boolean executed = false;
        void beforeExecute(Task task) { }
        void afterExecute(Task task, TaskState state) {
            if (executed) {
                return
            } else {
                executed = true
            }

            File outputDirectory = new File(task.project.buildDir, 'blackduck')
            outputDirectory.mkdirs()

            def dependencyGatherer = new DependencyGatherer()
            def rootProject = task.project
            dependencyGatherer.createAllCodeLocationFiles(rootProject, '${excludedProjectNames}', '${includedProjectNames}', '${excludedConfigurationNames}', '${includedConfigurationNames}', outputDirectory)
        }
    }
)