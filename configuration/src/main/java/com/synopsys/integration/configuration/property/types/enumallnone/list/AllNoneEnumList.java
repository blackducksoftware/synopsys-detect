package com.synopsys.integration.configuration.property.types.enumallnone.list;

import java.util.List;

import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.AllNoneEnum;

public class AllNoneEnumList<B extends Enum<B>> extends AllNoneEnumListBase<AllNoneEnum, B> {
    public AllNoneEnumList(List<ExtendedEnumValue<AllNoneEnum, B>> providedValues, Class<B> enumClass) {
        super(providedValues, enumClass, AllNoneEnum.NONE, AllNoneEnum.ALL);
    }
}