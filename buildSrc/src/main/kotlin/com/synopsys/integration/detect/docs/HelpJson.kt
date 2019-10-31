package com.synopsys.integration.detect.docs

import java.util.*

//Copied from the main detect library. We may want to figure out a way to factor this out or otherwise generate it here.
class HelpJsonData {
    var exitCodes: List<HelpJsonExitCode> = ArrayList<HelpJsonExitCode>()
    var buildDetectors: List<HelpJsonDetector> = ArrayList<HelpJsonDetector>()
    var buildlessDetectors: List<HelpJsonDetector> = ArrayList<HelpJsonDetector>()
    var options: List<HelpJsonOption> = ArrayList<HelpJsonOption>()
}

class HelpJsonDetector {
    var detectableName = ""
    var detectableDescriptiveName = ""
    var detectableGroup = ""
    var detectorType = ""
    var detectorName = ""
    var detectorDescriptiveName = ""
    var maxDepth = 0
    var nestable = false
    var nestInvisible = false

    var yieldsTo: List<String> = ArrayList()
    var fallbackTo = ""
}

class HelpJsonExitCode {
    var exitCodeKey = ""
    var exitCodeDescription = ""
    var exitCodeValue = 0
}

open class HelpJsonOption {
    var propertyName = ""
    var propertyKey = ""
    var propertyType = ""
    var defaultValue = ""
    var addedInVersion = ""
    var category = ""
    var group = ""
    var superGroup = ""
    var additionalGroups: List<String> = ArrayList()
    var description = ""
    var detailedDescription = ""
    var deprecated = false
    var deprecatedDescription = ""
    var deprecatedFailInVersion = ""
    var deprecatedRemoveInVersion = ""
    var strictValues = false
    var caseSensitiveValues = false
    var hasAcceptableValues = false
    var isCommaSeparatedList = false
    var acceptableValues: List<String> = ArrayList()
    var location = "" //this property is not in the JSON but is set when docs are generated
}