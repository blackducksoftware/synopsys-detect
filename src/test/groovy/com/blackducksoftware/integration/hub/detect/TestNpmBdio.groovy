package com.blackducksoftware.integration.hub.detect

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.testutils.DependencyNodeUtil
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

class TestNpmBdio {
    public static void main(String[] args) {
        File stdOut = new File('/Users/ekerwin/working/npmStdOut')
        stdOut.mkdirs()
        stdOut.delete()
        File stdErr = new File('/Users/ekerwin/working/npmStdErr')
        stdErr.mkdirs()
        stdErr.delete()

        Executable executable = new Executable(new File('/Users/ekerwin/crazy_test'), '/usr/local/bin/npm', ['ls', '-json'])
        final ProcessBuilder processBuilder = executable.createProcessBuilder().redirectOutput(stdOut).redirectError(stdErr)
        Map<String, String> environment = processBuilder.environment()
        environment.put('PATH', '/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin')
        final Process process = processBuilder.start()
        process.waitFor()

        NpmCliDependencyFinder npmCliDependencyFinder = new NpmCliDependencyFinder()
        DependencyNode dependencyNode = npmCliDependencyFinder.generateDependencyNode(stdOut)
        DependencyNodeUtil dependencyNodeUtil = new DependencyNodeUtil()
        StringBuilder stringBuilder = new StringBuilder()
        dependencyNodeUtil.buildNodeString(stringBuilder, 0, dependencyNode)
        File output = new File('/Users/ekerwin/working/output.json')
        output.mkdirs()
        output.delete()
        output << stringBuilder.toString()
    }
}
