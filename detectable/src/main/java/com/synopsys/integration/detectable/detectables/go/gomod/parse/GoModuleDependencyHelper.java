package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.go.gomod.process.WhyListStructureTransform;


public class GoModuleDependencyHelper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WhyListStructureTransform whyListStructureTransform;
 
    public GoModuleDependencyHelper() {
        this.whyListStructureTransform = new WhyListStructureTransform();
    }

    /**
     * Takes in a List<String> (plus some auxiliary lists) and returns a List<String>
     * @param main - The string name of the main go module
     * @param directs - The obtained list of the main module's direct dependency.
     * @param whyList - A list of all modules with their relationship to the main module
     * @param graph - The list produced by "go mod graph"- the intended "target".
     * @return - the actual dependency list
     */
    public List<String> computeDependencies(String main, List<String>directs, List<String> whyList, List<String>graph) {
        /**
         * takes a string that will be incorrectly computed to be a direct dependency and corrects it, such that a
         * true indirect dependency module will be associated with the module that is the true direct dependency  
         * eg.   "main_module_name indirect_module" ->  "direct_dependency_module indirect_module"  - this will convert the
         * requirements graph to a dependency graph.  True direct dependencies will be left unchanged.
         */
        ArrayList<String> goModGraph = new ArrayList<String>();
        ArrayList<String> correctedDependencies = new ArrayList<String>();

        HashMap<String, List<String>> whyMap = whyListStructureTransform.convertWhyListToWhyMap(whyList);
        // Correct lines that get mis-interpreted as a direct dependency, given the list of direct deps, requirements graph etc.
        for (String grphLine : graph) {
            boolean containsDirect = false;
            for (String directMod : directs) {
                if (grphLine.startsWith(main) && grphLine.contains(directMod)) {
                    containsDirect = true;
                }
            }
            
            // splitting here allows matching with less effort
            String[] splitLine = grphLine.split(" ");
            boolean needsRedux = false;
            if (!containsDirect && splitLine[0].equals(main)) {// anything that falls in here isn't a direct dependency of main
                needsRedux = true;
            }

            // this searches for instances where the main module is apparently referring to itself.  This can
            // step on the indirect dependency making it seem to be direct.
            if (splitLine[0].startsWith(main) && splitLine[0].contains("@")) {
                boolean gotonext = false;
                for (String adep : correctedDependencies) {
                    if (splitLine[1].startsWith(adep)) {
                        gotonext = true;
                        break;
                    }
                }
                if (gotonext) {
                    continue;
                }
            }

            if (needsRedux) {
                // Redo the line to establish the direct reference module to this *indirect* module
                grphLine = this.getProperParentage(grphLine, splitLine, whyMap, directs, correctedDependencies);
            }

            if (!goModGraph.contains(grphLine)) {
                logger.debug(grphLine);
                goModGraph.add(grphLine);
            }
        }
        return goModGraph;
    }

    private String getProperParentage(String grphLine, String[] splitLine, HashMap<String, List<String>> whyMap, List<String> directs, ArrayList<String> correctedDependencies) {
        String childModulePath = splitLine[1].replaceAll("@.*", "");
        correctedDependencies.add(childModulePath); // keep track of ones we've fixed.
        
        // look up the 'why' results for the module...  This will tell us
        // the direct dependency item that pulled this item into the mix.
        List<String> trackPath = whyMap.get(childModulePath);
        if (trackPath != null && !trackPath.isEmpty()) {
            for (String tp : trackPath) {
                String parent = directs.stream()
                        .filter(directMod -> directMod.contains(tp) || tp.contains(directMod.replaceAll("@.*","")))
                        .findFirst()
                        .orElse(null);
                if (parent != null) { // if real direct is found... otherwise do nothing
                    grphLine = grphLine.replace(splitLine[0], parent);
                    break;
                }
            }
        }
        return grphLine;
    }
}
