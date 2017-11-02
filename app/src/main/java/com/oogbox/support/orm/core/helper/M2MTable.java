package com.oogbox.support.orm.core.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.core.data.ORecord;
import com.oogbox.support.orm.core.data.ORecordValue;
import com.oogbox.support.orm.core.types.OManyToMany;
import com.oogbox.support.orm.core.types.helper.OFieldType;

import java.util.ArrayList;
import java.util.List;

public class M2MTable extends BaseModel {

    private BaseModel baseModel;
    private OManyToMany baseColumn;
    private BaseModel relModel;

    public M2MTable(Context context, BaseModel baseModel, OFieldType baseColumn) {
        super(context);
        this.baseModel = baseModel;
        this.baseColumn = (OManyToMany) baseColumn;
        this.relModel = createModel(baseColumn.getRefModel());
    }

    @Override
    public String getTableName() {
        return getRelTableName();
    }

    public String getBaseColumnName() {
        return baseColumn.getBaseColumnName() != null ?
                baseColumn.getBaseColumnName() : baseModel.getTableName() + "_id";
    }

    public String getRelColumnName() {
        return baseColumn.getRelColumnName() != null ?
                baseColumn.getRelColumnName() : relModel.getTableName() + "_id";
    }

    public String getRelTableName() {
        return baseColumn.getRelTableName() != null ?
                baseColumn.getRelTableName() : baseModel.getTableName() + "_" +
                relModel.getTableName() + "_rel";
    }

    public String createStatement() {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS ").append(getRelTableName());
        sql.append(" ( ");

        // Base Column
        sql.append(getBaseColumnName()).append(" INTEGER ");
        sql.append("REFERENCES ").append(baseModel.getTableName())
                .append(" ON DELETE CASCADE,");

        // Rel column
        sql.append(getRelColumnName()).append(" INTEGER ");
        sql.append("REFERENCES ").append(relModel.getTableName())
                .append(" ON DELETE CASCADE");

        sql.append(")");

        return sql.toString();
    }

    public List<ORecord> readAll(Integer baseRecordId) {
        List<ORecord> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String relSQL = "SELECT " + getRelColumnName() + " FROM " + getRelTableName() + " WHERE " +
                getBaseColumnName() + " = " + baseRecordId;
        Cursor cr = db.query(relModel.getTableName(), null, "_id in (" + relSQL + ")", null,
                null, null, null);
        if (cr.moveToFirst()) {
            do {
                ORecord record = ORecord.fromCursor(cr);
                record.setModel(relModel);
                records.add(record);
            } while (cr.moveToNext());
        }
        cr.close();
        db.close();
        return records;
    }

    public int insert(int baseId, int relId) {
        ORecordValue value = new ORecordValue();
        value.put(getBaseColumnName(), baseId);
        value.put(getRelColumnName(), relId);
        return create(value);
    }

    public int insert(int baseId, ORecordValue relRecord) {
        return insert(baseId, relModel.create(relRecord));
    }

    public void removeRelRecord(Integer relId) {
        relModel.delete(relId);
    }

    public void removeAllRelation(int baseId) {
        delete(getBaseColumnName() + " = ?", new String[]{baseId + ""});
    }
}

