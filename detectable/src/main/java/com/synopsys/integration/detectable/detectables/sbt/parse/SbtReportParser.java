package com.synopsys.integration.detectable.detectables.sbt.parse;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtCaller;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtModule;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtReport;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtRevision;
import com.synopsys.integration.detectable.util.XmlUtil;

public class SbtReportParser {
    private static final String IVY_REPORT_NODE_KEY = "ivy-report";
    private static final String INFO_NODE_KEY = "info";
    private static final String ORGANISATION_NODE_KEY = "organisation";
    private static final String MODULE_NODE_KEY = "module";
    private static final String REVISION_NODE_KEY = "revision";
    private static final String CONFIGURATION_NODE_KEY = "conf";
    private static final String DEPENDENCIES_NODE_KEY = "dependencies";
    private static final String NAME_NODE_KEY = "name";
    private static final String CALLER_NODE_KEY = "caller";
    private static final String CALLER_REVISION_NODE_KEY = "callerrev";

    public SbtReport parseReportFromXml(Document xmlReport) {
        Node ivyReport = XmlUtil.getNode(IVY_REPORT_NODE_KEY, xmlReport);
        Node infoNode = XmlUtil.getNode(INFO_NODE_KEY, ivyReport);
        Node dependenciesNode = XmlUtil.getNode(DEPENDENCIES_NODE_KEY, ivyReport);
        List<Node> xmlModules = XmlUtil.getNodeList(MODULE_NODE_KEY, dependenciesNode);

        String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, infoNode);
        String module = XmlUtil.getAttribute(MODULE_NODE_KEY, infoNode);
        String revision = XmlUtil.getAttribute(REVISION_NODE_KEY, infoNode);
        String configuration = XmlUtil.getAttribute(CONFIGURATION_NODE_KEY, infoNode);
        List<SbtModule> dependencies = xmlModules.stream().map(this::createModule).collect(Collectors.toList());

        return new SbtReport(organisation, module, revision, configuration, dependencies);
    }

    private SbtModule createModule(Node xmlModule) {
        List<Node> xmlRevisions = XmlUtil.getNodeList(REVISION_NODE_KEY, xmlModule);

        String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlModule);
        String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, xmlModule);
        List<SbtRevision> revisions = xmlRevisions.stream().map(this::createRevision).collect(Collectors.toList());

        return new SbtModule(organisation, name, revisions);
    }

    private SbtRevision createRevision(Node xmlRevision) {
        List<Node> xmlCallers = XmlUtil.getNodeList(CALLER_NODE_KEY, xmlRevision);

        String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlRevision);
        List<SbtCaller> callers = xmlCallers.stream().map(this::createCaller).collect(Collectors.toList());

        return new SbtRevision(name, callers);
    }

    private SbtCaller createCaller(Node xmlCaller) {
        String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, xmlCaller);
        String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlCaller);
        String revision = XmlUtil.getAttribute(CALLER_REVISION_NODE_KEY, xmlCaller);

        return new SbtCaller(organisation, name, revision);
    }

}
