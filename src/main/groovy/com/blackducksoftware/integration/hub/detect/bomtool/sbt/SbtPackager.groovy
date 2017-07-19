/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration

import groovy.util.slurpersupport.GPathResult

public class SbtReport {
    String organisation;
    String module;
    String revision;
    String configuration;

    List<SbtModule> dependencies;
}

public class SbtModule {
    String organisation;
    String name;

    List<SbtRevision> revisions;
}

public class SbtRevision {
    String name;

    List<SbtCaller> callers;
}
public class SbtCaller {
    String callerOrganisation;
    String callerName;
    String callerRevision;
}

public class SbtNodeCallers {
    DependencyNode node;
    List<SbtCaller> callers;
}

public class SbtConfigTree {
    String configuration;
    DependencyNode rootNode;
}

@Component
public class SbtPackager {

    private final Logger logger = LoggerFactory.getLogger(SbtPackager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    public List<SbtConfigTree> makeDependencyNodes(List<File> files){
        //def xmls = files.collect { file -> new XmlSlurper().parseText(file.text) }
        //return makeDependencyNodesFromXML(xmls);
        return files.collect { file ->
            def text = file.text;
            def xml = new XmlSlurper().parseText(file.text)
            def report = convertXml(xml)
            def node = convertSbtReportManual(report)
            node
        }
    }

    public SbtReport convertXml(GPathResult xmlReport) {
        SbtReport report = new SbtReport();
        report.organisation = xmlReport.info.@organisation.toString();
        report.module = xmlReport.info.@module.toString();
        report.revision = xmlReport.info.@revision.toString();
        report.configuration = xmlReport.info.@conf.toString();
        report.dependencies = new ArrayList<SbtModule>();

        xmlReport.dependencies.module.each{xmlModule ->
            SbtModule module = new SbtModule();
            module.name = xmlModule.@name.toString()
            module.organisation = xmlModule.@organisation.toString()
            module.revisions = new ArrayList<SbtRevision>();
            report.dependencies.add(module);

            xmlModule.revision.each{  xmlRevision ->
                SbtRevision revision = new SbtRevision();
                revision.name = xmlRevision.@name.toString()
                revision.callers = new ArrayList<SbtCaller>();
                module.revisions.add(revision);

                xmlRevision.caller.each { xmlCaller ->
                    SbtCaller caller = new SbtCaller();
                    caller.callerOrganisation = xmlCaller.@organisation.toString()
                    caller.callerName = xmlCaller.@name.toString()
                    caller.callerRevision = xmlCaller.@callerrev.toString()
                    revision.callers.add(caller)
                }
            }
        }

        report
    }

    public SbtConfigTree convertSbtReport(SbtReport report) {

        def rootId = new MavenExternalId(report.organisation, report.module, report.revision);
        DependencyNode root = new DependencyNode(report.module, report.revision, rootId );

        def builder = new DependencyNodeBuilder(root);

        report.dependencies.each{ module ->
            module.revisions.each {revision ->

                def id = new MavenExternalId(module.organisation, module.name, revision.name);
                def node = new DependencyNode(module.name, revision.name, id)

                List<DependencyNode> children = new ArrayList<DependencyNode>();
                revision.callers.each {caller ->
                    def childId = new MavenExternalId(caller.callerOrganisation, caller.callerName, caller.callerRevision);
                    def childNode = new DependencyNode(caller.callerName, caller.callerRevision, childId)
                    children.add(childNode)
                }

                builder.addChildNodeWithParents(node, children)
            }
        }

        def config = new SbtConfigTree();
        config.rootNode = root;
        config.configuration = report.configuration;
        config

    }
    public SbtConfigTree convertSbtReportManual(SbtReport report) {
        //the problem is child --> parent
        def rootId = new MavenExternalId(report.organisation, report.module, report.revision);
        DependencyNode root = new DependencyNode(report.module, report.revision, rootId );

        List<DependencyNode> cache = new ArrayList<DependencyNode>();
        cache.add(root);

        List<SbtNodeCallers> toResolve = new ArrayList<SbtNodeCallers>();

        report.dependencies.each{ module ->
            module.revisions.each {revision ->

                def id = new MavenExternalId(module.organisation, module.name, revision.name);
                def node = new DependencyNode(module.name, revision.name, id)
                cache.add(node)

                SbtNodeCallers caller = new SbtNodeCallers();
                caller.node = node;
                caller.callers = revision.callers;
                toResolve.add(caller)

            }
        }

        toResolve.each { nodeCaller ->
            nodeCaller.callers.each { caller ->
                def fnd = false;
                cache.each { node ->
                    def nodeId = node.externalId as MavenExternalId;
                    if (node.name == caller.callerName && node.version == caller.callerRevision && nodeId.group == caller.callerOrganisation){
                        node.children.add(nodeCaller.node)
                        if (fnd) logger.error("Found duplicate callers for same name, version and revision:${caller.callerName}, ${caller.callerRevision}, ${caller.callerOrganisation}");
                        fnd = true;
                    }
                }
                if (!fnd) {
                    if (fnd) logger.error("Found no caller name, version and revision:${caller.callerName}, ${caller.callerRevision}, ${caller.callerOrganisation}");
                }
            }
        }

        def config = new SbtConfigTree();
        config.rootNode = root;
        config.configuration = report.configuration;
        config
    }

    /* root object
     <ivy-report version="1.0">
     <info
     organisation="com.twosigma"
     module="flint_2.11"
     revision="0.2.0-SNAPSHOT"
     conf="compile"
     confs="compile, runtime, test, provided, optional, compile-internal, runtime-internal, test-internal, plugin, sources, docs, pom, scala-tool, scoveragePlugin"
     date="20170711152604"/>
     */

    //dependencies are in a flat list
    //connected by callers
    /*<caller organisation="com.twosigma"
     name="flint_2.11"
     conf="compile"
     rev="1.3.0"
     rev-constraint-default="1.3.0"
     rev-constraint-dynamic="1.3.0"
     callerrev="0.2.0-SNAPSHOT"/>*/

    public void buildChildren(DependencyNode node, GPathResult report, List<DependencyNode> cache){
        def eid = node.externalId as MavenExternalId;
        report.dependencies.module.each{module ->
            module.revision.each{  revision ->
                def isDependency = false;
                revision.caller.each { caller ->
                    if (caller.@name.toString() == eid.name && caller.@callerrev.toString() == eid.version && caller.@organisation.toString() == eid.group){
                        isDependency = true;
                    }
                }
                if (isDependency){
                    def fndNode;
                    cache.each { cachedNode ->
                        def ceid = cachedNode.externalId;
                        if (module.@name.toString() == ceid.name && revision.@name.toString() == ceid.version && module.@organisation.toString() == ceid.group){
                            fndNode = cachedNode;
                        }
                    }
                    if (fndNode != null){
                        node.children.add(fndNode);
                    }else{
                        logger.error("Uh oh spudoodio!");
                    }

                }
            }
        }

    }

    public List<DependencyNode> makeDependencyNodesFromXML(List<GPathResult> reports) {

        reports.collect { report ->
            List<DependencyNode> cache = new ArrayList<DependencyNode>();

            def org = report.info.@organisation.toString();
            def md = report.info.@module.toString();
            def rv = report.info.@revision.toString();
            def id = new MavenExternalId(org, md, rv);
            DependencyNode root = new DependencyNode(report.info.@module.toString(), report.info.@revision.toString(), id );
            cache.add(root);

            report.dependencies.module.each{module ->
                module.revision.each{  revision ->
                    def node = new DependencyNode(module.@name.toString(), revision.@name.toString(),
                            new MavenExternalId(module.@organisation.toString(),
                            module.@name.toString(), revision.@name.toString())
                            )
                    cache.add(node)
                }
            }
            cache.each { node ->
                buildChildren(node, report, cache)
            }

            root
        }

    }
}