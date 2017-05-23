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
package com.blackducksoftware.integration.hub.packman.util.commands

class Command {
    final Executable executable

    final String[] args

    Command(final Executable executable, final String... args) {
        this.executable = executable
        this.args = args
    }

    Command(final Executable executable, final List<String> args) {
        this.executable = executable
        this.args = args.toArray()
    }
}
