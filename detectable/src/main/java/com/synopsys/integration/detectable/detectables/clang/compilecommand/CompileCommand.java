/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import com.synopsys.integration.util.Stringable;

// Loaded from json via Gson
// TODO: Can Gson use setter methods?
public class CompileCommand extends Stringable {
    public String directory = "";
    public String command = "";
    public String[] arguments = new String[] {};
    public String file = "";
}
