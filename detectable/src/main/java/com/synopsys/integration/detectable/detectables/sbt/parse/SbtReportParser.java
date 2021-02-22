/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    public SbtReport parseReportFromXml(final Document xmlReport) {
        final Node ivyReport = XmlUtil.getNode(IVY_REPORT_NODE_KEY, xmlReport);
        final Node infoNode = XmlUtil.getNode(INFO_NODE_KEY, ivyReport);
        final Node dependenciesNode = XmlUtil.getNode(DEPENDENCIES_NODE_KEY, ivyReport);
        final List<Node> xmlModules = XmlUtil.getNodeList(MODULE_NODE_KEY, dependenciesNode);

        final String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, infoNode);
        final String module = XmlUtil.getAttribute(MODULE_NODE_KEY, infoNode);
        final String revision = XmlUtil.getAttribute(REVISION_NODE_KEY, infoNode);
        final String configuration = XmlUtil.getAttribute(CONFIGURATION_NODE_KEY, infoNode);
        final List<SbtModule> dependencies = xmlModules.stream().map(this::createModule).collect(Collectors.toList());

        return new SbtReport(organisation, module, revision, configuration, dependencies);
    }

    private SbtModule createModule(final Node xmlModule) {
        final List<Node> xmlRevisions = XmlUtil.getNodeList(REVISION_NODE_KEY, xmlModule);

        final String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlModule);
        final String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, xmlModule);
        final List<SbtRevision> revisions = xmlRevisions.stream().map(this::createRevision).collect(Collectors.toList());

        return new SbtModule(organisation, name, revisions);
    }

    private SbtRevision createRevision(final Node xmlRevision) {
        final List<Node> xmlCallers = XmlUtil.getNodeList(CALLER_NODE_KEY, xmlRevision);

        final String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlRevision);
        final List<SbtCaller> callers = xmlCallers.stream().map(this::createCaller).collect(Collectors.toList());

        return new SbtRevision(name, callers);
    }

    private SbtCaller createCaller(final Node xmlCaller) {
        final String organisation = XmlUtil.getAttribute(ORGANISATION_NODE_KEY, xmlCaller);
        final String name = XmlUtil.getAttribute(NAME_NODE_KEY, xmlCaller);
        final String revision = XmlUtil.getAttribute(CALLER_REVISION_NODE_KEY, xmlCaller);

        return new SbtCaller(organisation, name, revision);
    }

}
