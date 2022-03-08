package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.exception.IntegrationException;

public class PearRequiredOnlyTest {

    @Test
    public void TestParse() throws IntegrationException {
        List<String> pearList = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package           Version State",
            "Archive_Tar       1.4.3   stable",
            "Auth_SASL         1.1.0   stable",
            "Config            1.10.12 stable",
            "Console_Getopt    1.4.1   stable",
            "HTML_Template_IT  1.3.0   stable",
            "MIME_Type         1.4.1   stable",
            "Net_SMTP          1.8.0   stable",
            "Net_Socket        1.2.2   stable",
            "PEAR              1.10.5  stable",
            "PEAR_Frontend_Gtk 0.4.0   beta",
            "PEAR_Frontend_Web 0.7.5   beta",
            "Structures_Graph  1.1.1   stable",
            "XML_Util          1.4.2   stable"
        );

        List<String> pearPackageDependencies = Arrays.asList(
            "Dependencies for Net_SMTP",
            "=========================",
            "Required? Type           Name            Versioning           Group",
            "Yes       Php                             (version >= 5.4.0)",
            "Yes       Pear Installer                  (version >= 1.10.1)",
            "Yes       Package        pear/Net_Socket  (version >= 1.0.7)",
            "No        Package        pear/Auth_SASL   (version >= 1.0.5)"
        );

        ExternalIdFactory factory = new ExternalIdFactory();
        Map<String, String> dependencyNameVersionMap = new PearListParser().parse(pearList);
        List<PackageDependency> packageDependencies = new PearPackageDependenciesParser().parse(pearPackageDependencies);
        DependencyGraph dependencyGraph = new PearDependencyGraphTransformer(factory, EnumListFilter.excludeNone()).buildDependencyGraph(
            dependencyNameVersionMap,
            packageDependencies
        );

        Assertions.assertTrue(
            dependencyGraph.hasDependency(factory.createNameVersionExternalId(Forge.PEAR, "Auth_SASL", "1.1.0")),
            "Must have Auth_SASL even though it was not a required dependency."
        );
    }
}
