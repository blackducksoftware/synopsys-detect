/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import java.nio.charset.StandardCharsets

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class CpanPackager {
    private final String INSPECTOR = 'cpan-inspector.pl'

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    FindDependenciesParser findDependenciesParser

    @Autowired
    DetectFileManager detectFileManager

    public List<DependencyNode> makeDependencyNodes(File sourceDirectory, String cpanExecutablePath, String cpanmExecutablePath, String perlExecutablePath) {
        List<String> directModuleNames = getDirectModuleNames(sourceDirectory, cpanmExecutablePath)

        def installDepPlugin = new Executable(sourceDirectory, cpanmExecutablePath, ['CPAN::FindDependencies'])
        executableRunner.execute(installDepPlugin)

        String inspectorText = getClass().getResourceAsStream("/${INSPECTOR}").getText(StandardCharsets.UTF_8.name())
        inspectorText = inspectorText.replace('PERL_PATH', perlExecutablePath)
        File outputDirectory = detectFileManager.createDirectory(BomToolType.CPAN)
        File inspectorFile = detectFileManager.createFile(outputDirectory, INSPECTOR)
        inspectorFile.delete()
        inspectorFile << inspectorText

        List<NameVersionNode> nameVersionNodes = []
        directModuleNames.each { moduleName ->
            def findDependencies = new Executable(sourceDirectory, perlExecutablePath, [
                inspectorFile.getAbsolutePath(),
                moduleName
            ])
            String treeText = executableRunner.execute(findDependencies)
            nameVersionNodes += findDependenciesParser.parse(treeText)
        }
    }

    private List<String> getDirectModuleNames(File sourceDirectory, String cpanmExecutablePath) {
        def executable = new Executable(sourceDirectory, cpanmExecutablePath, ['--showdeps', '.'])
        ExecutableOutput executableOutput = executableRunner.execute(executable)
        String lines = executableOutput.getStandardOutput()

        List<String> modules = []
        for(String line : lines.split('\n')) {
            if(!line.trim()) {
                continue
            }
            if(line.contains('-->') || line.contains(' ... ')) {
                continue
            }
            modules += line.split('~')[0].trim()
        }

        modules
    }
}
