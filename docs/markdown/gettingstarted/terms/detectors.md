# Detectors

[solution_name] uses detectors to find and extract dependencies from all supported package managers. For example, the Maven detector, which is run by default, executes an mvn dependency:tree command against a Maven project and derives dependency information, which can be sent to [blackduck_product_name]

By default, all detectors are eligible to run. The set of detectors that actually run depends on the files that exist in your project directory. 
