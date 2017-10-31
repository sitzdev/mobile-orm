package com.oogbox.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.data.ORecord;
import com.oogbox.support.orm.data.ORecordValue;
import com.oogbox.support.orm.data.RelationValue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Users users = new Users(this);
        users.delete(null, null);
        ORecordValue values = new ORecordValue();
        values.put("name", "DDS");
        values.put("mobile_ids", new RelationValue()
                .replace(3, 4));
        int id = users.create(values);

        ORecord user = users.browse(id);

        Log.e(">>", user + "<<");

        Log.e(">>", user.readMany2Many("mobile_ids") + "<<");

        Log.e(">>", "TOTAL devices");
        Log.e(">", new Mobiles(this).select() + "<<");
    }
}
