package com.blackducksoftware.integration.hub.detect.extraction.bucket;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings (value="unchecked")
public class Bucket {

    protected Map<String, Object> values = new HashMap<>();

    public boolean containsKey(final String key) {
        return values.containsKey(key);
    }

    public List<File> getFiles(final String key) {
        try {
            return (List<File>) values.get(key);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile(final String key) {
        return (File) values.get(key);
    }

    public File getInspector(final String key) {
        return (File) values.get(key);
    }

    public String getExecutable(final String key) {
        return (String) values.get(key);
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public Object getKey(final String key) {
        return values.get(key);
    }

}
