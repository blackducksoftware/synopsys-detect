package com.synopsys.integration.detectable.detectables.buildroot;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParseException;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectables.buildroot.model.Parser;
import com.synopsys.integration.detectable.detectables.buildroot.model.ShowInfoComponent;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BuildrootExtractor {
    public static final Forge forge = new Forge("/", "buildroot");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);
    private final Parser parser = new Parser();
    private final BuildrootDetectableOptions options;
    private final DetectableExecutableRunner executableRunner;
    private final ToolVersionLogger toolVersionLogger;

    public BuildrootExtractor(BuildrootDetectableOptions options, DetectableExecutableRunner executableRunner, ToolVersionLogger toolVersionLogger) {
        this.options = options;
        this.executableRunner = executableRunner;
        this.toolVersionLogger = toolVersionLogger;
    }

    public Extraction extract(ExecutableTarget makeExe, File workingDirectory) throws ExecutableRunnerException, MissingExternalIdException, ExecutableFailedException {
        // log version of make
        toolVersionLogger.log(workingDirectory, makeExe, "-v");

        // log version of buildroot
        toolVersionLogger.log(workingDirectory, makeExe, "print-version");

        String output = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(workingDirectory, makeExe, "show-info")).getStandardOutput();

        Map<String, ShowInfoComponent> components;
        try {
            components = parser.parse(output);
        } catch (JsonParseException e) {
            return Extraction.failure("Unable to parse make show-info output");
        }

        LazyExternalIdDependencyGraphBuilder graph = new LazyExternalIdDependencyGraphBuilder();

        for (ShowInfoComponent component : components.values()) {
            String type = component.getType();
            if (type.equals("rootfs") || type.equals("host") && options.getDependencyTypeFilter().shouldExclude(BuildrootDependencyType.HOST)) {
                logger.trace("Skipping component of type: " + type);
                continue;
            }
            logger.trace("Processing buildroot component: " + component.getName());

            LazyId id = makeLazyId(component);

            if (component.getReverseDependencies().size() == 0) {
                graph.addChildToRoot(id);
            } else {
                for (String reverseDependency : component.getReverseDependencies()) {
                    LazyId reverseDependencyId = makeLazyId(components.get(reverseDependency));
                    graph.addChildWithParent(id, reverseDependencyId);
                }
            }

            Dependency dependency = dependencyFactory.createNameVersionDependency(forge, component.getName(), component.getVersion());
            graph.setDependencyInfo(id, component.getName(), component.getVersion(), dependency.getExternalId());
        }

        return Extraction.success(new CodeLocation(graph.build()));
    }

    private LazyId makeLazyId(ShowInfoComponent component) {
        return LazyId.fromNameAndVersion(component.getName(), component.getVersion());
    }
}
