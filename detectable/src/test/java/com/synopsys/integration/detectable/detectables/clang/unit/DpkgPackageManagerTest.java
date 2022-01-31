package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPkgDetailsResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.NotOwnedByAnyPkgException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DpkgPackageManagerTest {

    //TODO: Split into 2 tests - one for VersionResolution and one for FullResolution

    @Test
    public void test() throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("libc6-dev:amd64: /usr/include/stdlib.h\n");
        String pkgMgrOwnedByOutput = sb.toString();

        sb = new StringBuilder();
        sb.append("Package: libc6-dev\n");
        sb.append("Status: install ok installed\n");
        sb.append("Priority: optional\n");
        sb.append("Section: libdevel\n");
        sb.append("Installed-Size: 18812\n");
        sb.append("Maintainer: Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>\n");
        sb.append("Architecture: amd64\n");
        sb.append("Multi-Arch: same\n");
        sb.append("Source: glibc\n");
        sb.append("Version: 2.27-3ubuntu1\n");
        sb.append("Provides: libc-dev\n");
        sb.append("Depends: libc6 (= 2.27-3ubuntu1), libc-dev-bin (= 2.27-3ubuntu1), linux-libc-dev\n");
        sb.append("Suggests: glibc-doc, manpages-dev\n");
        sb.append(
            "Breaks: binutils (<< 2.26), binutils-gold (<< 2.20.1-11), cmake (<< 2.8.4+dfsg.1-5), gcc-4.4 (<< 4.4.6-4), gcc-4.5 (<< 4.5.3-2), gcc-4.6 (<< 4.6.0-12), libhwloc-dev (<< 1.2-3), libjna-java (<< 3.2.7-4), liblouis-dev (<< 2.3.0-2), liblouisxml-dev (<< 2.4.0-2), libperl5.26 (<< 5.26.1-3), make (<< 3.81-8.1), pkg-config (<< 0.26-1)\n");
        sb.append("Conflicts: libc0.1-dev, libc0.3-dev, libc6.1-dev\n");
        sb.append("Description: GNU C Library: Development Libraries and Header Files\n");
        sb.append(" Contains the symlinks, headers, and object files needed to compile\n");
        sb.append(" and link programs which use the standard C library.\n");
        sb.append("Homepage: https://www.gnu.org/software/libc/libc.html\n");
        sb.append("Original-Maintainer: GNU Libc Maintainers <debian-glibc@lists.debian.org>\n");

        String pkgMgrVersionOutput = sb.toString();

        final String packageNameWithArch = "libc6-dev:amd64";
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        Mockito.when(executableRunner.execute(null, "dpkg", Arrays.asList("-s", packageNameWithArch))).thenReturn(new ExecutableOutput(0, pkgMgrVersionOutput, ""));

        DpkgPkgDetailsResolver dpkgVersionResolver = new DpkgPkgDetailsResolver();
        DpkgPackageManagerResolver pkgMgr = new DpkgPackageManagerResolver(dpkgVersionResolver);

        List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().dpkg(), executableRunner, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("libc6-dev", pkgs.get(0).getPackageName());
        assertEquals("2.27-3ubuntu1", pkgs.get(0).getPackageVersion());
        assertEquals("amd64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testEpoch() throws ExecutableRunnerException, NotOwnedByAnyPkgException {

        StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("login:amd64: /usr/include/stdlib.h\n");
        String pkgMgrOwnedByOutput = sb.toString();

        sb = new StringBuilder();

        sb.append("Package: login\n");
        sb.append("Essential: yes\n");
        sb.append("Status: install ok installed\n");
        sb.append("Priority: required\n");
        sb.append("Section: admin\n");
        sb.append("Installed-Size: 1212\n");
        sb.append("Maintainer: Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>\n");
        sb.append("Architecture: amd64\n");
        sb.append("Source: shadow\n");
        sb.append("Version: 1:4.5-1ubuntu1\n");
        sb.append("Replaces: manpages-de (<< 0.5-3), manpages-tr (<< 1.0.5), manpages-zh (<< 1.5.1-1)\n");
        sb.append("Pre-Depends: libaudit1 (>= 1:2.2.1), libc6 (>= 2.14), libpam0g (>= 0.99.7.1), libpam-runtime, libpam-modules (>= 1.1.8-1)\n");
        sb.append("Conflicts: amavisd-new (<< 2.3.3-8), backupninja (<< 0.9.3-5), echolot (<< 2.1.8-4), gnunet (<< 0.7.0c-2), python-4suite (<< 0.99cvs20060405-1)\n");
        sb.append("Conffiles:\n");
        sb.append(" /etc/login.defs 2a8f6cd8e00f54df72dc345a23f9db20\n");
        sb.append(" /etc/pam.d/login 1fd6cb4d4267a68148ee9973510a9d3e\n");
        sb.append(" /etc/pam.d/su ce6dcfda3b190a27a455bb38a45ff34a\n");
        sb.append(" /etc/securetty d0124b1d2fb22d4ac9a91aa02ae6d6db\n");
        sb.append("Description: system login tools\n");
        sb.append(" These tools are required to be able to login and use your system. The\n");
        sb.append(" login program invokes your user shell and enables command execution. The\n");
        sb.append(" newgrp program is used to change your effective group ID (useful for\n");
        sb.append(" workgroup type situations). The su program allows changing your effective\n");
        sb.append(" user ID (useful being able to execute commands as another user).\n");
        sb.append("Homepage: https://github.com/shadow-maint/shadow\n");
        sb.append("Original-Maintainer: Shadow package maintainers <pkg-shadow-devel@lists.alioth.debian.org>\n");

        String pkgMgrVersionOutput = sb.toString();

        final String packageNameWithArch = "login:amd64";
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        Mockito.when(executableRunner.execute(null, "dpkg", Arrays.asList("-s", packageNameWithArch))).thenReturn(new ExecutableOutput(0, pkgMgrVersionOutput, ""));

        DpkgPkgDetailsResolver dpkgVersionResolver = new DpkgPkgDetailsResolver();
        DpkgPackageManagerResolver pkgMgr = new DpkgPackageManagerResolver(dpkgVersionResolver);

        List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().dpkg(), executableRunner, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("login", pkgs.get(0).getPackageName());
        assertEquals("1:4.5-1ubuntu1", pkgs.get(0).getPackageVersion());
        assertEquals("amd64", pkgs.get(0).getPackageArch());
    }

}
