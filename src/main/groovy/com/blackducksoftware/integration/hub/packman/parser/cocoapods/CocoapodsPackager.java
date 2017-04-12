package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.packman.parser.model.Package;
import com.blackducksoftware.integration.hub.packman.parser.model.Packager;

public class CocoapodsPackager implements Packager {

	private InputStream podfileStream;
	private InputStream podlockStream;

	public CocoapodsPackager(InputStream podfileStream, InputStream podlockStream) {
		this.podfileStream = podfileStream;
		this.podlockStream = podlockStream;
	}

	@Override
	public Package makePackage() {
		Package cocoapodsPackage = new Package();
		cocoapodsPackage.name = "dummy-name";
		cocoapodsPackage.version = "1.0.0";

		PodLockParser podLockParser = new PodLockParser();
		PodLock podLock = podLockParser.parse(podlockStream);
		cocoapodsPackage.dependencies = getDependencies(podLock);

		return cocoapodsPackage;
	}

	public List<Package> getDependencies(PodLock podLock) {
		List<Package> dependencies = new ArrayList<Package>();

		return dependencies;
	}

}
