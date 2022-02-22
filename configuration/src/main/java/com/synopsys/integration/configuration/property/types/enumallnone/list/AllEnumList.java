package com.synopsys.integration.configuration.property.types.enumallnone.list;

import java.util.List;

import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.AllEnum;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;

public class AllEnumList<B extends Enum<B>> extends AllNoneEnumListBase<AllEnum, B> {
    public AllEnumList(List<ExtendedEnumValue<AllEnum, B>> providedValues, Class<B> enumClass) {
        super(providedValues, enumClass, null, AllEnum.ALL);
    }
}