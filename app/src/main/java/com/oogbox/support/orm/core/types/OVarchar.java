package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class OVarchar extends OFieldType<OVarchar> {

    public OVarchar(String label) {
        super(label);
        setSize(64);
    }

    @Override
    public String fieldTypeString() {
        return "VARCHAR";
    }
}
