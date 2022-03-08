package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;

public interface DetectFontLocator {
    String DEFAULT_FONT_FILE_NAME_REGULAR = "NotoSansCJKtc-Regular.ttf";
    String CUSTOM_FONT_FILE_DIR_NAME_REGULAR = "custom-regular";
    String DEFAULT_FONT_FILE_NAME_BOLD = "NotoSansCJKtc-Bold.ttf";
    String CUSTOM_FONT_FILE_DIR_NAME_BOLD = "custom-bold";
    String TTF_FILE_EXTENSION = ".ttf";

    File locateRegularFontFile() throws DetectUserFriendlyException;

    File locateBoldFontFile() throws DetectUserFriendlyException;
}
