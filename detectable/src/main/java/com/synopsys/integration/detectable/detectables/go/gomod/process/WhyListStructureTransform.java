package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WhyListStructureTransform {
    public WhyListStructureTransform() {
        // default constructor
    }
    
    // this is *specifically* for taking go mod why all output and presenting it in a 
    // more structured form for use later.
    public HashMap<String, List<String>> convertWhyListToWhyMap(List<String> whyList) {
        // executing this command helps produce more accurate results. 
        // we'll run this once... to produce a map of the module to a list of module "why" paths for each one.
        // map is of the form:
        // module_name -> List<string>(dependency path)
        String key = "";
        List<String> shortList = new LinkedList<String>();
        HashMap<String, List<String>> moduleToHierarchyList = new HashMap<String, List<String>>();

        // we know the module name already, but the line that starts with a "#"
        // tells us that we're hitting a new hierarchy chain so we'll use that
        // to enter the item into the map, and start a new list for the next module.
        for (String whyModuleLine : whyList) {
            if (whyModuleLine.length() <= 0) {
                continue; // DONT include blank lines from the output in the list!
            }
            if (whyModuleLine.startsWith("#")) {
                if (!key.equals("")) {
                    moduleToHierarchyList.put(key, shortList);
                }
                key = whyModuleLine.substring(2);
                shortList = new LinkedList<String>();
            } else {
                shortList.add(whyModuleLine);
            }
        }
        moduleToHierarchyList.put(key, shortList);

        return moduleToHierarchyList;

    }
}
