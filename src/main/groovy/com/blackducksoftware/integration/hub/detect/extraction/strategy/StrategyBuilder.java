package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.BomToolRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.CurrentDirectoryRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileListRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StringRequirement;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class StrategyBuilder<C extends ExtractionContext, E extends Extractor<C>> {

    private String name;
    private BomToolType bomToolType;

    public Map<Requirement, ExtractionContextAction> requirementActionMap = new HashMap<>();
    public Map<Requirement, ExtractionContextAction> demandActionMap = new HashMap<>();

    public Class<C> extractionContextClass;
    public Class<E> extractorClass;

    private final Set<Strategy> yieldsToStrategies = new HashSet<>();

    private boolean nestable = false;
    private int maxDepth = Integer.MAX_VALUE;

    public StrategyBuilder(final Class<C> extractionContextClass, final Class<E> extractorClass) {
        this.extractionContextClass = extractionContextClass;
        this.extractorClass = extractorClass;
    }

    public StrategyBuilder<C, E> named(final String name, final BomToolType bomToolType){
        this.name = name;
        this.bomToolType = bomToolType;
        return this.needsBomTool2(bomToolType).noop();
    }

    public StrategyBuilder<C, E> needsCurrentDirectory(final ExtractionContextAction<C, File> action) {
        this.demands(new CurrentDirectoryRequirement(), action);
        return this;
    }

    public BomToolNeedBuilder needsBomTool2(final BomToolType type) {
        return new BomToolNeedBuilder(this, type);
    }

    public FileNeedBuilder needsFile(final String file) {
        return new FileNeedBuilder(this, file);
    }

    public StringNeedBuilder needsString(final String value) {
        return new StringNeedBuilder(this, value);
    }


    public FileListNeedBuilder needsFiles(final String filepattern) {
        return new FileListNeedBuilder(this, new String[] { filepattern });
    }

    public FileListNeedBuilder needsFiles(final String[] filepattern) {
        return new FileListNeedBuilder(this, filepattern);
    }


    public StandardExecutableDemandBuilder demandsStandardExecutable(final StandardExecutableType type) {
        return new StandardExecutableDemandBuilder(this, type);
    }

    public <T> DemandBuilder<T> demands(final Requirement<T> requirement) {
        return new DemandBuilder<>(this, requirement);
    }

    public <V> StrategyBuilder<C, E> demands(final Requirement<V> requirement, final ExtractionContextAction<C, V> action) {
        demandActionMap.put(requirement, action);
        return this;
    }

    public <V> StrategyBuilder<C, E> needs(final Requirement<V> requirement, final ExtractionContextAction<C, V> action) {
        requirementActionMap.put(requirement, action);
        return this;
    }

    public StrategyBuilder<C, E> yieldsTo(final Strategy strategy) {
        yieldsToStrategies.add(strategy);
        return this;
    }

    public StrategyBuilder<C, E> nestable() {
        return nestable(true);
    }

    public StrategyBuilder<C, E> nestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public StrategyBuilder<C, E> maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public class DemandBuilder<T> {
        private final StrategyBuilder<C, E> parent;
        private final Requirement<T> requirement;
        public DemandBuilder(final StrategyBuilder<C, E> parent, final Requirement<T> requirement) {
            this.parent = parent;
            this.requirement = requirement;
        }

        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, T> action) {
            parent.demands(requirement, action);
            return parent;
        }
        public StrategyBuilder<C, E> noop() {
            return as((noop1, noop2) -> {});
        }
    }

    public class StandardExecutableDemandBuilder {
        private final StrategyBuilder<C, E> parent;
        public StandardExecutableType type;
        public StandardExecutableDemandBuilder(final StrategyBuilder<C, E> parent, final StandardExecutableType type) {
            this.type = type;
            this.parent = parent;

        }
        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, File> action) {
            final StandardExecutableRequirement requirement = new StandardExecutableRequirement();
            requirement.executableType = this.type;
            parent.demands(requirement, action);
            return parent;
        }
        public StrategyBuilder<C, E> noop() {
            return as((noop1, noop2) -> {});
        }
    }

    public class StringNeedBuilder {
        private final StrategyBuilder<C, E> parent;
        public String value;
        public String description = null;
        public StringNeedBuilder(final StrategyBuilder<C, E> parent, final String  value) {
            this.value =  value;
            this.parent = parent;
        }

        public StringNeedBuilder failWith(final String description) {
            this.description = description;
            return this;
        }

        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, String> action) {
            final StringRequirement requirement = new StringRequirement();
            requirement. value = this.value;
            requirement.failedDescriptionOverride = description;
            parent.needs(requirement, action);
            return parent;
        }
    }

    public class FileNeedBuilder {
        private final StrategyBuilder<C, E> parent;
        public String filename;
        public FileNeedBuilder(final StrategyBuilder<C, E> parent, final String filename) {
            this.filename = filename;
            this.parent = parent;
        }
        public StrategyBuilder<C, E> noop() {
            return as((noop1, noop2) -> {});
        }
        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, File> action) {
            final FileRequirement requirement = new FileRequirement();
            requirement.filename = this.filename;
            parent.needs(requirement, action);
            return parent;
        }
    }

    public class FileListNeedBuilder {
        private final StrategyBuilder<C, E> parent;
        public String[] filepatterns;
        public FileListNeedBuilder(final StrategyBuilder<C, E> parent, final String[] filepatterns) {
            this.filepatterns = filepatterns;
            this.parent = parent;
        }
        public StrategyBuilder<C, E> noop() {
            return as((noop1, noop2) -> {});
        }
        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, List<File>> action) {
            final FileListRequirement requirement = new FileListRequirement();
            requirement.filepatterns = this.filepatterns;
            parent.needs(requirement, action);
            return parent;
        }
    }

    public class BomToolNeedBuilder {
        private final StrategyBuilder<C, E> parent;
        public BomToolType type;
        public BomToolNeedBuilder(final StrategyBuilder<C, E> parent, final BomToolType type) {
            this.type = type;
            this.parent = parent;
        }
        public StrategyBuilder<C, E> noop() {
            return as((noop1, noop2) -> {});
        }
        public StrategyBuilder<C, E> as(final ExtractionContextAction<C, BomToolType> action) {
            final BomToolRequirement requirement = new BomToolRequirement();
            requirement.type = this.type;
            parent.needs(requirement, action);
            return parent;
        }
    }


    public Strategy<C,E> build() {
        final StrategySearchOptions searchOptions = new StrategySearchOptions(maxDepth, nestable);
        return new Strategy<>(name, bomToolType, requirementActionMap, demandActionMap, extractionContextClass, extractorClass, yieldsToStrategies, searchOptions);
    }

}
