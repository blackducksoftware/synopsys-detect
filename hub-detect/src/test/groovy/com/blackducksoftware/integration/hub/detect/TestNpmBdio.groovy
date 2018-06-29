package com.blackducksoftware.integration.hub.detect

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.detect.bomtool.npm.parse.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.workflow.DetectProjectManager
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.GsonBuilder

class TestNpmBdio {
    public static void main(String[] args) {
        File stdOut = new File('C:\\Users\\jpiscitelli\\Documents\\node\\test\\npmStdOut')
        stdOut.mkdirs()
        stdOut.delete()
        File stdErr = new File('C:\\Users\\jpiscitelli\\Documents\\node\\test\\npmStdErr')
        stdErr.mkdirs()
        stdErr.delete()
        //        Executable npmInstall = new Executable(new File('/Users/ekerwin/crazy_test'), '/usr/local/bin/npm', ['install'])
        //        ProcessBuilder npmInstallProcessBuilder = npmInstall.createProcessBuilder()
        //        Map<String, String> npmInstallEnvironment = npmInstallProcessBuilder.environment()
        //        npmInstallEnvironment.put('PATH', '/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin')
        //        final Process npmInstallProcess = npmInstallProcessBuilder.start()
        //        npmInstallProcess.waitFor()

        Executable executable = new Executable(new File('C:\\Users\\jpiscitelli\\Documents\\node\\test'), 'C:\\Program Files\\nodejs\\npm.cmd', ['ls', '-json'])
        final ProcessBuilder processBuilder = executable.createProcessBuilder().redirectOutput(stdOut).redirectError(stdErr)
        Map<String, String> environment = processBuilder.environment()
        environment.put('PATH', 'C:\\Program Files\\Docker\\Docker\\Resources\\bin;C:\\ProgramData\\Oracle\\Java\\javapath;C:\\Program Files\\Microsoft MPI\\Bin\\;C:\\Program Files (x86)\\Intel\\iCLS Client\\;C:\\Program Files\\Intel\\iCLS Client\\;C:\\Windows\\system32;C:\\Windows;C:\\Windows\\System32\\Wbem;C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\;C:\\Program Files\\Intel\\WiFi\\bin\\;C:\\Program Files\\Common Files\\Intel\\WirelessCommon\\;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\Program Files (x86)\\NVIDIA Corporation\\PhysX\\Common;C:\\Program Files\\Microsoft SQL Server\\130\\Tools\\Binn\\;C:\\Program Files\\dotnet\\;C:\\Program Files\\Anaconda3;C:\\Program Files\\Anaconda3\\Scripts;C:\\Program Files\\Anaconda3\\Library\\bin;C:\\Program Files\\Git\\cmd;C:\\Program Files\\PuTTY\\;C:\\PostgreSQL\\pg96\\bin;C:\\ProgramData\\chocolatey\\bin;C:\\Program Files\\Microsoft SQL Server\\Client SDK\\ODBC\\130\\Tools\\Binn\\;C:\\Program Files (x86)\\Microsoft SQL Server\\130\\Tools\\Binn\\;C:\\Program Files\\Microsoft SQL Server\\130\\DTS\\Binn\\;C:\\Program Files\\nodejs\\;C:\\WINDOWS\\system32;C:\\WINDOWS;C:\\WINDOWS\\System32\\Wbem;C:\\WINDOWS\\System32\\WindowsPowerShell\\v1.0\\;C:\\Program Files (x86)\\sbt\\bin;C:\\Users\\jpiscitelli\\AppData\\Local\\Microsoft\\WindowsApps;C:\\Program Files (x86)\\Microsoft VS Code\\bin;C:\\Users\\jpiscitelli\\AppData\\Roaming\\npm;C:\\Program Files\\Java\\jdk1.8.0_141\\bin;')
        final Process process = processBuilder.start()
        process.waitFor()

        NpmCliDependencyFinder npmCliDependencyFinder = new NpmCliDependencyFinder()
        DetectCodeLocation detectCodeLocation = npmCliDependencyFinder.generateCodeLocation(stdOut, 'C:\\Users\\jpiscitelli\\Documents\\node\\test')

        DetectProject detectProject = new DetectProject()
        detectProject.projectName = 'ek unit test'
        detectProject.projectVersionName = '0.0.1'
        detectProject.addDetectCodeLocation(detectCodeLocation)

        DetectConfiguration detectConfiguration = [getAggregateBomName: '', getProjectCodeLocationPrefix: ''] as DetectConfiguration
        DetectFileFinder fileFinder = new DetectFileFinder()
        DetectFileManager detectFileManager = new DetectFileManager()
        detectFileManager.fileFinder = fileFinder
        detectFileManager.detectConfiguration = detectConfiguration
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper()
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)
        DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioNodeFactory, bdioPropertyHelper)
        IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil()

        DetectProjectManager detectProjectManager = new DetectProjectManager()
        detectProjectManager.detectConfiguration = detectConfiguration
        detectProjectManager.detectFileManager = detectFileManager
        detectProjectManager.bdioPropertyHelper = bdioPropertyHelper
        detectProjectManager.bdioNodeFactory = bdioNodeFactory
        detectProjectManager.dependencyGraphTransformer = dependencyGraphTransformer
        detectProjectManager.integrationEscapeUtil = integrationEscapeUtil
        detectProjectManager.gson = new GsonBuilder().setPrettyPrinting()

        List<File> bdioFiles = detectProjectManager.createBdioFiles(detectProject)
        bdioFiles.each { println it.canonicalPath }
    }
}
