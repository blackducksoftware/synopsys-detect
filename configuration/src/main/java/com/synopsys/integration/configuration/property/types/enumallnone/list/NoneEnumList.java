package com.synopsys.integration.configuration.property.types.enumallnone.list;

import java.util.List;

import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.NoneEnum;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;

public class NoneEnumList<B extends Enum<B>> extends AllNoneEnumListBase<NoneEnum, B> {
    public NoneEnumList(List<ExtendedEnumValue<NoneEnum, B>> providedValues, Class<B> enumClass) {
        super(providedValues, enumClass, NoneEnum.NONE, null);
    }
}