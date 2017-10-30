package com.oogbox.support.orm.types;

import com.oogbox.support.orm.types.helper.OFieldType;

public class OText extends OFieldType<OText> {

    public OText(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "TEXT";
    }
}
