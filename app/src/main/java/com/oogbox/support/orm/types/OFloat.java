package com.oogbox.support.orm.types;

import com.oogbox.support.orm.types.helper.OFieldType;

public class OFloat extends OFieldType<OFloat> {

    public OFloat(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "FLOAT";
    }
}
