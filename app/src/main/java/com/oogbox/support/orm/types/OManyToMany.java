package com.oogbox.support.orm.types;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.types.helper.OFieldType;

public class OManyToMany extends OFieldType<OManyToMany> {

    private String baseColumnName, relColumnName, relTableName;

    public OManyToMany(String label, Class<? extends BaseModel> refModel) {
        super(label);
        setRefModel(refModel);
    }

    public OManyToMany baseColumn(String baseColumnName) {
        this.baseColumnName = baseColumnName;
        return this;
    }

    public OManyToMany relColumn(String relColumnName) {
        this.relColumnName = relColumnName;
        return this;
    }

    public OManyToMany relTableName(String relTableName) {
        this.relTableName = relTableName;
        return this;
    }

    public String getBaseColumnName() {
        return baseColumnName;
    }

    public String getRelColumnName() {
        return relColumnName;
    }

    public String getRelTableName() {
        return relTableName;
    }

    @Override
    public String fieldTypeString() {
        return null;
    }
}
