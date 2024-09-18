package com.blackduck.integration.configuration.property.types.enumallnone.list;

import java.util.List;

import com.blackduck.integration.configuration.property.types.enumallnone.enumeration.AllNoneEnum;
import com.blackduck.integration.configuration.property.types.enumextended.ExtendedEnumValue;

public class AllNoneEnumList<B extends Enum<B>> extends AllNoneEnumListBase<AllNoneEnum, B> {
    public AllNoneEnumList(List<ExtendedEnumValue<AllNoneEnum, B>> providedValues, Class<B> enumClass) {
        super(providedValues, enumClass, AllNoneEnum.NONE, AllNoneEnum.ALL);
    }
}