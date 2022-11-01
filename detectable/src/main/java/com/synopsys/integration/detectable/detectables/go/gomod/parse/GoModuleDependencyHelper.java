package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;


public class GoModuleDependencyHelper {

 
    public GoModuleDependencyHelper() {
    }

    public List<String> computeDependencies(String main, List<String>directs, HashMap<String, List<String>> whyMap, List<String>graph) throws ExecutableFailedException {
        ArrayList<String> goModGraph = new ArrayList<String>();
        // Correct lines that get mis-interpreted as a direct dependency, given the list of direct deps, requirements graph etc.
        for (String grphLine : graph) {
            boolean containsDirect = false;
            for (String directMod : directs) {
                if (grphLine.startsWith(main) && grphLine.contains(directMod)) {
                    containsDirect = true;
                }
            }
            if (!containsDirect) {
                grphLine = grphLine.replace(main, "xxxxxx");
            }
            String[] splitLine = grphLine.split(" ");
            if (splitLine[0].equals("xxxxxx")) {

                String childModulePath = splitLine[1].replaceAll("@.*", "");
                
                // look up the 'why' results for the module...
                List<String> trackPath = whyMap.get(childModulePath);
                String parent = "";
                if (!trackPath.isEmpty()) {
                    for (String tp : trackPath) {
                        for (String directMod : directs) {
                            if (directMod.contains(tp)) {
                                parent = directMod;
                                break;
                            }
                        }
                    }
                    grphLine = grphLine.replace(splitLine[0], parent);
                }
            }
            if (!goModGraph.contains(grphLine)) {
                goModGraph.add(grphLine);
            }
        }
        return goModGraph;
    }
}
