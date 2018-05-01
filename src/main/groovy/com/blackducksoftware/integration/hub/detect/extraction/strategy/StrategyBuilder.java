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
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class StrategyBuilder<C extends ExtractionContext, E extends Extractor<C>> {

    public Map<Requirement, ExtractionContextAction> requirementActionMap = new HashMap<>();
    public Map<Requirement, ExtractionContextAction> demandActionMap = new HashMap<>();

    public Class<C> extractionContextClass;
    public Class<E> extractorClass;

    private final Set<Strategy> yieldsToStrategies = new HashSet<>();

    public StrategyBuilder(final Class<C> extractionContextClass, final Class<E> extractorClass) {
        this.extractionContextClass = extractionContextClass;
        this.extractorClass = extractorClass;
    }

    public StrategyBuilder<C, E> needsCurrentDirectory(final ExtractionContextAction<C, File> action) {
        this.demands(new CurrentDirectoryRequirement(), action);
        return this;
    }

    public BomToolNeedBuilder needsBomTool(final BomToolType type) {
        return new BomToolNeedBuilder(this, type);
    }

    public FileNeedBuilder needsFile(final String file) {
        return new FileNeedBuilder(this, file);
    }

    public FileListNeedBuilder needsFiles(final String[] filepattern) {
        return new FileListNeedBuilder(this, filepattern);
    }


    public StandardExecutableDemandBuilder demandsStandardExecutable(final StandardExecutableType type) {
        return new StandardExecutableDemandBuilder(this, type);
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
        return new Strategy<>(requirementActionMap, demandActionMap, extractionContextClass, extractorClass, yieldsToStrategies);
    }

}
