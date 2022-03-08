package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.io.File;
import java.util.List;

public class PwdOutputParser {
    public File deriveBuildDirectory(List<String> pwdOutputLines) {
        return new File(pwdOutputLines.get(pwdOutputLines.size() - 1).trim());
    }
}
