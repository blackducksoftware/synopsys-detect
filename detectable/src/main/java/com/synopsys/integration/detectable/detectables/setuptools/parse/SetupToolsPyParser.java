package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.python.util.PythonDependencyTransformer;

public class SetupToolsPyParser implements SetupToolsParser {
    
    private TomlParseResult parsedToml;
    
    private List<String> dependencies;
    
    public SetupToolsPyParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
        this.dependencies = new ArrayList<>();
    }
    
    @Override
    public SetupToolsParsedResult parse() throws IOException {
        // Use a name from the toml if we have it. Do not parse names and versions from the setup.py
        // as the project will not always have a string (it could have variables or method calls)
        String tomlProjectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        List<PythonDependency> parsedDirectDependencies = parseDirectDependencies();
        
        return new SetupToolsParsedResult(tomlProjectName, projectVersion, parsedDirectDependencies);
    }
    
    public List<String> load(String setupFile) throws IOException {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            // Read the Python script from the resources directory
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/parse_setup.py")))) {
                String line;
                StringBuilder pythonCode = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    pythonCode.append(line);
                    pythonCode.append("\n");
                }

                // Call the Python script and function
                pyInterp.exec(pythonCode.toString());
                PyObject pyFunc = pyInterp.get("parse_install_requires");
                PyObject pyObject = pyFunc.__call__(new PyString(setupFile));

                if (pyObject instanceof PyList) {
                    PyList pyList = (PyList) pyObject;
                    for (Object obj : pyList) {
                        if (obj instanceof String) {
                            String requirement = (String) obj;
                            dependencies.add(requirement);
                        }
                    }
                }
            }
        }

        return dependencies;
    }
    
    private List<PythonDependency> parseDirectDependencies() {
        List<PythonDependency> results = new LinkedList<>();
        
        PythonDependencyTransformer dependencyTransformer = new PythonDependencyTransformer();

        for (String dependencyLine : dependencies) {            
            PythonDependency dependency = dependencyTransformer.transformLine(dependencyLine);
            
            // If we have a ; in our requirements line then there is a condition on this dependency.
            // We want to know this so we don't consider it a failure later if we try to run pip show
            // on it and we don't find it.
            if (dependencyLine.contains(";")) {
                dependency.setConditional(true);
            }

            if (dependency != null) {
                results.add(dependency);
            }
        }
        
        return results;
    }
}
