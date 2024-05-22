package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.configuration.property.types.enumallnone.list.AllNoneEnumCollection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcludeIncludeEnumFilter<T extends Enum<T>> {
    private final AllNoneEnumCollection<T> excluded;
    private final AllNoneEnumCollection<T> included;
    private final Map<T, Set<String>> scanTypeEvidenceMap;

    public ExcludeIncludeEnumFilter(AllNoneEnumCollection<T> excluded, AllNoneEnumCollection<T> included, Map<T, Set<String>> scanTypeEvidenceMap) {
        this.excluded = excluded;
        this.included = included;
        this.scanTypeEvidenceMap = scanTypeEvidenceMap;
    }
    
    public ExcludeIncludeEnumFilter(AllNoneEnumCollection<T> excluded, AllNoneEnumCollection<T> included) {
        this.excluded = excluded;
        this.included = included;
        this.scanTypeEvidenceMap = Collections.EMPTY_MAP;
    }

    private boolean willExclude(T value) {
        if (excluded.containsAll()) {
            return true;
        } else if (excluded.containsNone()) {
            return false;
        } else {
            return excluded.containsValue(value);
        }
    }

    private boolean willInclude(T value) {
        if (included.isEmpty() && scanTypeEvidenceMap.isEmpty()) {
            return true;
        } else if (included.containsAll()) {
            return true;
        } else if (included.containsNone() && scanTypeEvidenceMap.isEmpty()) {
            return false;
        } else {
            return included.containsValue(value) || scanTypeEvidenceMap.containsKey(value);
        }
    }

    public boolean shouldInclude(T value) {
        if (willExclude(value)) {
            return false;
        } else {
            return willInclude(value);
        }
    }
    
    public boolean includeSpecified() {
        return !included.isEmpty() && !included.containsAll() && !included.containsNone();
    }
}