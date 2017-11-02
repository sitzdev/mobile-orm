package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class OBoolean extends OFieldType<OBoolean> {

    public OBoolean(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "BOOLEAN";
    }
}
