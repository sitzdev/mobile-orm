package com.oogbox.support.orm.types;

import com.oogbox.support.orm.types.helper.OFieldType;

public class ODate extends OFieldType<ODate> {

    public ODate(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "DATE";
    }
}
