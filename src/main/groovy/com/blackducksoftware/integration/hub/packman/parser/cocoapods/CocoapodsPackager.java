package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.packman.parser.model.Package;
import com.blackducksoftware.integration.hub.packman.parser.model.Packager;

public class CocoapodsPackager implements Packager {

    private final InputStream podfileStream;

    private final InputStream podlockStream;

    public CocoapodsPackager(final InputStream podfileStream, final InputStream podlockStream) {
        this.podfileStream = podfileStream;
        this.podlockStream = podlockStream;
    }

    @Override
    public List<Package> makePackages() {
        final List<Package> packages = new ArrayList<>();

        final PodfileParser podfileParser = new PodfileParser();
        final Podfile podfile = podfileParser.parse(podfileStream);

        final PodLockParser podLockParser = new PodLockParser();
        final PodLock podLock = podLockParser.parse(podlockStream);

        final Map<String, Package> allPods = getDependencies(podLock);

        for (final Package target : podfile.targets) {
            final List<Package> targetDependencies = new ArrayList<>();
            for (final Package dep : target.dependencies) {
                targetDependencies.add(allPods.get(dep.externalId.name));
            }
            target.dependencies = targetDependencies;
            target.forge = Forge.cocoapods;
            packages.add(target);
        }

        return packages;
    }

    public Map<String, Package> getDependencies(final PodLock podLock) {
        final Map<String, Package> allPods = new HashMap<>();
        for (final Package pod : podLock.pods) {
            allPods.put(pod.externalId.name, pod);
        }

        // Fix pods dependencies
        for (final Entry<String, Package> pod : allPods.entrySet()) {
            final List<Package> pod_deps = new ArrayList<>();
            for (final Package dependency : pod.getValue().dependencies) {
                pod_deps.add(allPods.get(dependency.externalId.name));
            }
            pod.getValue().dependencies = pod_deps;
        }
        return allPods;
    }

}
