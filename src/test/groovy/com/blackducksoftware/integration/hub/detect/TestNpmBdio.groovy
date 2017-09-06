package com.blackducksoftware.integration.hub.detect

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.GsonBuilder

class TestNpmBdio {
    public static void main(String[] args) {
        File stdOut = new File('/Users/ekerwin/working/npmStdOut')
        stdOut.mkdirs()
        stdOut.delete()
        File stdErr = new File('/Users/ekerwin/working/npmStdErr')
        stdErr.mkdirs()
        stdErr.delete()
        //        Executable npmInstall = new Executable(new File('/Users/ekerwin/crazy_test'), '/usr/local/bin/npm', ['install'])
        //        ProcessBuilder npmInstallProcessBuilder = npmInstall.createProcessBuilder()
        //        Map<String, String> npmInstallEnvironment = npmInstallProcessBuilder.environment()
        //        npmInstallEnvironment.put('PATH', '/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin')
        //        final Process npmInstallProcess = npmInstallProcessBuilder.start()
        //        npmInstallProcess.waitFor()

        Executable executable = new Executable(new File('/Users/ekerwin/crazy_test'), '/usr/local/bin/npm', ['ls', '-json'])
        final ProcessBuilder processBuilder = executable.createProcessBuilder().redirectOutput(stdOut).redirectError(stdErr)
        Map<String, String> environment = processBuilder.environment()
        environment.put('PATH', '/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin')
        final Process process = processBuilder.start()
        process.waitFor()

        NpmCliDependencyFinder npmCliDependencyFinder = new NpmCliDependencyFinder()
        DependencyNode dependencyNode = npmCliDependencyFinder.generateDependencyNode(stdOut)
        DetectCodeLocation detectCodeLocation = new DetectCodeLocation(BomToolType.NPM, '/Users/ekerwin/crazy_test', dependencyNode)

        DetectProject detectProject = new DetectProject()
        detectProject.projectName = 'ek unit test'
        detectProject.projectVersionName = '0.0.1'
        detectProject.addDetectCodeLocation(detectCodeLocation)

        DetectConfiguration detectConfiguration = [getAggregateBomName: '', getProjectCodeLocationPrefix: ''] as DetectConfiguration
        FileFinder fileFinder = new FileFinder()
        DetectFileManager detectFileManager = new DetectFileManager()
        detectFileManager.fileFinder = fileFinder
        detectFileManager.detectConfiguration = detectConfiguration
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper()
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)
        DependencyNodeTransformer dependencyNodeTransformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper)
        IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil()

        DetectProjectManager detectProjectManager = new DetectProjectManager()
        detectProjectManager.detectConfiguration = detectConfiguration
        detectProjectManager.detectFileManager = detectFileManager
        detectProjectManager.bdioPropertyHelper = bdioPropertyHelper
        detectProjectManager.bdioNodeFactory = bdioNodeFactory
        detectProjectManager.dependencyNodeTransformer = dependencyNodeTransformer
        detectProjectManager.integrationEscapeUtil = integrationEscapeUtil
        detectProjectManager.gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ExternalId.class, new ExternalIdTypeAdapter()).create()

        List<File> bdioFiles = detectProjectManager.createBdioFiles(detectProject)
        bdioFiles.each { println it.canonicalPath }
    }
}
