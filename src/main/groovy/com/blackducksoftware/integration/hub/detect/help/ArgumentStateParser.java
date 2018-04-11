package com.blackducksoftware.integration.hub.detect.help;

import org.springframework.stereotype.Component;

@Component
public class ArgumentStateParser {

    
    public ArgumentState parseArgs(String[] args) {
        boolean isHelp = checkFirstArgument("-h", "--help", args);
        boolean isHelpDocument = checkFirstArgument("-hdoc", "--helpdocument", args);
        boolean isInteractive = checkFirstArgument("-i", "--interactive", args);
        
        boolean isVerboseHelpMessage = isHelp && checkSecondArgument("-v", "--verbose", args);
        
        String parsedValue = null;
        if (isHelp && !(isVerboseHelpMessage)){
            if (args.length == 2) {
                parsedValue = args[1];
            }
        }
        
        return new ArgumentState(isHelp, isHelpDocument, isInteractive, isVerboseHelpMessage, parsedValue);
    }
    
    private boolean checkFirstArgument(final String command, final String largeCommand, String[] args) {
        return checkArgument(command, largeCommand, 0, args);
    }

    private boolean checkSecondArgument(final String command, final String largeCommand, String[] args) {
        return checkArgument(command, largeCommand, 1, args);
    }
     
    
    private boolean checkArgument(final String command, final String largeCommand, int index, String[] args) {
        if (args.length > index) {
            return (command.equals(args[index]) || largeCommand.equals(args[index]));
        }

        return false;
    }
    
}
