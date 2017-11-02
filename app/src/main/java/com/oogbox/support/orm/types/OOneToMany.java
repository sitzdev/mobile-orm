package com.oogbox.support.orm.types;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.types.helper.OFieldType;

public class OOneToMany extends OFieldType<OOneToMany> {

    public OOneToMany(String label, Class<? extends BaseModel> refModel, String refColumn) {
        super(label);
        setRefModel(refModel);
        setRefColumn(refColumn);
    }

    @Override
    public String fieldTypeString() {
        return null;
    }
}
