package com.synopsys.integration.detectable.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class MockFileFinder implements FileFinder {

    Map<Integer, List<File>> files = new HashMap<>();

    public static MockFileFinder withFile(File file) {
        MockFileFinder finder = new MockFileFinder();
        finder.addFile(file, 0);
        return finder;
    }

    public static MockFileFinder withFileNamed(String name) {
        MockFileFinder finder = new MockFileFinder();
        finder.addFileNamed(name, 0);
        return finder;
    }

    public void addFile(File file, int depth) {
        if (!files.containsKey(depth)) {
            files.put(depth, new ArrayList<>());
        }
        files.get(depth).add(file);
    }

    public void addFileNamed(String name, int depth) {
        if (!files.containsKey(depth)) {
            files.put(depth, new ArrayList<>());
        }
        files.get(depth).add(new File(name));
    }

    @Override
    public List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns, final int depth) {
        List<File> found = new ArrayList<>();
        for (int i = 0; i <= depth; i++) {
            if (files.containsKey(i)) {
                List<File> possibles = files.get(i);
                for (String pattern : filenamePatterns) {
                    FileFilter fileFilter = new WildcardFileFilter(pattern);
                    for (File possible : possibles) {
                        if (fileFilter.accept(possible)) {
                            found.add(possible);
                        }
                    }

                }
            }
        }
        return found;
    }
}
