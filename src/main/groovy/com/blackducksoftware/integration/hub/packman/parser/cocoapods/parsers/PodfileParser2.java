/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Pod;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podfile;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.PodfileTarget;

public class PodfileParser2 extends StreamParser<Podfile> {

    final Pattern TARGET_REGEX = Pattern.compile("[ |\\t]*target[ |\\t]+('|\")(.*)\\1[ |\\t]*do.*");

    final Pattern ABSTRACT_TARGET_REGEX = Pattern.compile("[ |\\t]*abstract_target[ |\\t]+('|\")(.*)\\1[ |\\t]*do.*");

    final Pattern TARGET_END_REGEX = Pattern.compile("[ |\\t]*end[ |\\t]*");

    final Pattern POD_REGEX = Pattern.compile("[ |\\t]*pod\\s*?('|\")(.*?)\\1(.*)");

    @Override
    public Podfile parse(final BufferedReader bufferedReader) {
        Podfile podfile = new Podfile();

        final Stack<PodfileTarget> targetStack = new Stack<>();

        final PodfileTarget rootTarget = new PodfileTarget("ROOT");
        rootTarget.isAbstract = true;
        targetStack.push(rootTarget);

        final List<PodfileTarget> targets = new ArrayList<>();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher targetMatcher = TARGET_REGEX.matcher(line);
                final Matcher abstractTargetMatcher = ABSTRACT_TARGET_REGEX.matcher(line);
                final Matcher podMatcher = POD_REGEX.matcher(line);
                final Matcher targetEndMatcher = TARGET_END_REGEX.matcher(line);

                line = processSingleLineComments(line, CocoapodsPackager.COMMENTS);

                if (StringUtils.isBlank(line)) {

                } else if (targetMatcher.matches()) {
                    final PodfileTarget target = new PodfileTarget(targetMatcher.group(2));
                    targetStack.push(target);
                } else if (abstractTargetMatcher.matches()) {
                    final PodfileTarget target = new PodfileTarget(abstractTargetMatcher.group(2));
                    target.isAbstract = true;
                    targetStack.push(target);
                } else if (podMatcher.matches()) {
                    final Pod pod = new Pod(podMatcher.group(2));
                    pod.otherInfo = podMatcher.group(3);
                    targetStack.peek().pods.add(pod);
                } else if (targetEndMatcher.matches() && targetStack.peek() != rootTarget) {
                    final PodfileTarget target = targetStack.pop();
                    target.parent = targetStack.peek();
                    if (!target.isAbstract) {
                        targets.add(target);
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            podfile = null;
        } catch (final NullPointerException e) {
            podfile = null;
        }

        for (final PodfileTarget target : targets) {
            final List<Pod> targetPods = getPods(target);

            final String name = "";
            final DependencyNode project = new DependencyNode(name, null, null, new ArrayList<>());

        }

        return podfile;
    }

    private List<Pod> getPods(final PodfileTarget target) {
        final List<Pod> pods = new ArrayList<>();
        if (target.parent != null) {
            pods.addAll(getPods(target.parent));
        }
        return pods;
    }
}
