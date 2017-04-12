package com.blackducksoftware.integration.hub.packman.parser

class Package {
	public String packageName;
	public String packageVersion;
	public List<Package> dependencies = [];

	public Package() {
	}

	public Package(String packageName, String packageVersion) {
		this.packageName = packageName;
		this.packageVersion = packageVersion
	}
}
