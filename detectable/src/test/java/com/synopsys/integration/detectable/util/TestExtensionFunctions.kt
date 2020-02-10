package com.synopsys.integration.detectable.util

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class TestExtensionFunctions {
    companion object {
        fun String.toInputStream(encoding: Charset = StandardCharsets.UTF_8): InputStream {
            return IOUtils.toInputStream(this, encoding)
        }
    }
}