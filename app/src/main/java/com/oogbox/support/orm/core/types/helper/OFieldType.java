package com.oogbox.support.orm.core.types.helper;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.core.annotation.DataModel;

import java.util.HashMap;

public abstract class OFieldType<T> {

    private String fieldLabel = "Unknown";
    private String fieldName = "unknown";
    private int fieldSize = -1;
    private Boolean primaryKey = false;
    private Boolean autoIncrement = false;
    private Object defValue = null;
    private HashMap<String, String> enumMap = new HashMap<>();
    private Class<? extends BaseModel> refModel;
    private String refColumn;

    public OFieldType(String label) {
        fieldLabel = label;
    }

    public abstract String fieldTypeString();

    public T setRefModel(Class<? extends BaseModel> refModel) {
        this.refModel = refModel;
        return (T) this;
    }

    public T setRefColumn(String refColumn) {
        this.refColumn = refColumn;
        return (T) this;
    }

    public T setName(String name) {
        fieldName = name;
        return (T) this;
    }

    public T setLabel(String label) {
        fieldLabel = label;
        return (T) this;
    }

    public T setSize(int size) {
        fieldSize = size;
        return (T) this;
    }

    public T primaryKey() {
        primaryKey = true;
        return (T) this;
    }

    public T autoIncrement() {
        autoIncrement = true;
        return (T) this;
    }

    public T setDefault(Object defValue) {
        this.defValue = defValue;
        return (T) this;
    }

    public T addEnum(String key, String value) {
        enumMap.put(key, value);
        return (T) this;
    }

    public String getLabel() {
        return fieldLabel;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getSize() {
        return fieldSize;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public Object getDefaultValue() {
        return defValue;
    }

    public Class<? extends BaseModel> getRefModel() {
        return refModel;
    }

    public String getRefColumn() {
        return refColumn;
    }

    public HashMap<String, String> getEnumMap() {
        return enumMap;
    }

    public String getEnumVal(String key) {
        if (enumMap.containsKey(key)) {
            return enumMap.get(key);
        }
        return null;
    }

    public String getRefModelTableName() {
        DataModel dataModel = getRefModel().getAnnotation(DataModel.class);
        if (dataModel != null) {
            return dataModel.value().replaceAll("\\.", "_");
        }
        return getRefModel().getName().toLowerCase();
    }
}
