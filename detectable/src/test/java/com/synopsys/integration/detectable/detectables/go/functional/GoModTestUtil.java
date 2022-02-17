package com.synopsys.integration.detectable.detectables.go.functional;

import com.synopsys.integration.executable.ExecutableOutput;

public class GoModTestUtil {

    private GoModTestUtil() {}

    public static ExecutableOutput createGoModWhyOutputWithPrefix(String modulePrefix) {
        String output = String.join(System.lineSeparator(),
            "# github.com/gin-gonic/gin",
            "github.com/gin-gonic/gin",
            "",
            "# github.com/davecgh/go-spew",
            modulePrefix + " github.com/davecgh/go-spew)",
            "",
            "# golang.org/x/text",
            "github.com/gin-gonic/gin",
            "golang.org/x/text/language",
            "",
            "# golang.org/x/tools",
            modulePrefix + " golang.org/x/tools)",
            "",
            "# gopkg.in/check.v1",
            modulePrefix + " gopkg.in/check.v1)",
            "",
            "# gopkg.in/yaml.v2",
            modulePrefix + " gopkg.in/yaml.v2)",
            "",
            "# sigs.k8s.io/yaml",
            modulePrefix + " sigs.k8s.io/yaml)"
        );
        return new ExecutableOutput(0, output, "");
    }

}
