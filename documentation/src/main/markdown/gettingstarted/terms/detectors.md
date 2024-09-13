# Detectors

The [detect_product_long] Detector tool runs one or more detectors to find and extract dependencies from all supported package managers.

Each package manager ecosystem is assigned a detector type. Each detector type may use multiple methods (detectors) to extract dependencies.

Which detector(s) will run against your project is determined by the detector search process.

For example, the Maven detector, which is run by default, executes an mvn dependency:tree command against a Maven project and derives dependency information, which can be sent to [bd_product_short]

By default, all detectors are eligible to run. The set of detectors that actually run depends on the files that exist in your project directory and whether all detector requirements have been met. 
