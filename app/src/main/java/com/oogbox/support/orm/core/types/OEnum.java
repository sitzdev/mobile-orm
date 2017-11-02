package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class OEnum extends OFieldType<OEnum> {

    public OEnum(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "VARCHAR";
    }
}
