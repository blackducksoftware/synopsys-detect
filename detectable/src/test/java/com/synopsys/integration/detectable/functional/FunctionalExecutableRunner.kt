package com.synopsys.integration.detectable.functional

import com.synopsys.integration.detectable.detectable.executable.Executable
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner
import java.io.File

class FunctionalExecutableRunner : ExecutableRunner {
    override fun execute(workingDirectory: File?, exeCmd: String?, vararg args: String?): ExecutableOutput {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(workingDirectory: File?, exeCmd: String?, args: MutableList<String>?): ExecutableOutput {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(workingDirectory: File?, exeFile: File?, vararg args: String?): ExecutableOutput {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(workingDirectory: File?, exeFile: File?, args: MutableList<String>?): ExecutableOutput {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(executable: Executable?): ExecutableOutput {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}