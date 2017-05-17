package com.blackducksoftware.integration.hub.packman

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.packman.packagemanager.GradlePackageManager
import com.blackducksoftware.integration.hub.packman.packagemanager.MavenPackageManager
import com.blackducksoftware.integration.hub.packman.packagemanager.PipPackageManager

class PackageManagerRunnerTest {
    @Test
    public void testFilteringOnePackageManager() {
        def packageManagers = [
            new MavenPackageManager(),
            new GradlePackageManager(),
            new PipPackageManager()
        ]
        def packageManagerTypeOverride = "PIP"

        def packageManagerRunner = new PackageManagerRunner()
        packageManagerRunner.packageManagers = packageManagers
        packageManagerRunner.packageManagerTypeOverride = packageManagerTypeOverride

        Assert.assertEquals(1, packageManagerRunner.filterPackageManagers().size())
        Assert.assertEquals(PackageManagerType.PIP, packageManagerRunner.filterPackageManagers().get(0).getPackageManagerType())
    }

    @Test
    public void testFilteringMultiplePackageManagers() {
        def packageManagers = [
            new MavenPackageManager(),
            new GradlePackageManager(),
            new PipPackageManager()
        ]
        def packageManagerTypeOverride = "MAVEN, GRADLE"

        def packageManagerRunner = new PackageManagerRunner()
        packageManagerRunner.packageManagers = packageManagers
        packageManagerRunner.packageManagerTypeOverride = packageManagerTypeOverride

        def filteredPackageManagers = packageManagerRunner.filterPackageManagers()
        Assert.assertEquals(2, filteredPackageManagers.size())
        final List<PackageManagerType> packageManagerTypes = [
            filteredPackageManagers[0].packageManagerType,
            filteredPackageManagers[1].packageManagerType
        ]
        Assert.assertTrue(packageManagerTypes.contains(PackageManagerType.MAVEN))
        Assert.assertTrue(packageManagerTypes.contains(PackageManagerType.GRADLE))
    }

    @Test
    public void testFilteringNoPackageManagers() {
        def packageManagers = [
            new MavenPackageManager(),
            new GradlePackageManager(),
            new PipPackageManager()
        ]
        def packageManagerTypeOverride = null

        def packageManagerRunner = new PackageManagerRunner()
        packageManagerRunner.packageManagers = packageManagers
        packageManagerRunner.packageManagerTypeOverride = packageManagerTypeOverride

        def filteredPackageManagers = packageManagerRunner.filterPackageManagers()
        Assert.assertEquals(3, filteredPackageManagers.size())
        final List<PackageManagerType> packageManagerTypes = [
            filteredPackageManagers[0].packageManagerType,
            filteredPackageManagers[1].packageManagerType,
            filteredPackageManagers[2].packageManagerType
        ]
        Assert.assertTrue(packageManagerTypes.contains(PackageManagerType.MAVEN))
        Assert.assertTrue(packageManagerTypes.contains(PackageManagerType.GRADLE))
        Assert.assertTrue(packageManagerTypes.contains(PackageManagerType.PIP))
    }
}
