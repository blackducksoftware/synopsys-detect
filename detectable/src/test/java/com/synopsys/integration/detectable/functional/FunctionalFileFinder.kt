package com.synopsys.integration.detectable.functional

import com.synopsys.integration.detectable.detectable.file.FileFinder
import org.apache.commons.io.filefilter.WildcardFileFilter
import java.io.File
import java.io.FileFilter
import java.util.*

class FunctionalFileFinder : FileFinder {
    var files: MutableMap<Int, MutableList<File>> = mutableMapOf()

    fun addFile(file: File, depth: Int) {
        if (!files.containsKey(depth)) {
            files.put(depth, mutableListOf())
        }
        files[depth]!!.add(file)
    }

    override fun findFiles(directoryToSearch: File, filenamePatterns: List<String>, depth: Int, findInsideMatchingDirectories: Boolean): List<File> {
        val found: MutableList<File> = ArrayList()
        for (i in 0..depth) {
            if (files.containsKey(i)) {
                val possibles = files[i]!!;
                for (pattern in filenamePatterns) {
                    val fileFilter: FileFilter = WildcardFileFilter(pattern)
                    for (possible in possibles) {
                        if (fileFilter.accept(possible)) {
                            found.add(possible)
                        }
                    }
                }
            }
        }
        return found
    }
}