package com.blackducksoftware.integration.hub.detect.extraction.bucket;

import java.io.File;
import java.util.List;

public class MutableBucket extends Bucket {

    public void addFiles(final String key, final List<File> file) {
        values.put(key,  file);
    }

    public void addFile(final String key, final File file) {
        values.put(key,  file);
    }

    public void addInspector(final String key, final File file) {
        values.put(key,  file);
    }

    public void addExecutable(final String key, final String file) {
        values.put(key,  file);
    }

    public void addBucket(final Bucket bucket) {
        for (final String key : bucket.keySet()) {
            values.put(key, bucket.getKey(key));
        }
    }

}
