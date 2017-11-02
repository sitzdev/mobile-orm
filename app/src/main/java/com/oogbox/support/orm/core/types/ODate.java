package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class ODate extends OFieldType<ODate> {

    public ODate(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "DATE";
    }
}
