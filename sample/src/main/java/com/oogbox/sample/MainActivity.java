package com.oogbox.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.data.ORecord;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Users users = new Users(this);

//        ORecordValue value = new ORecordValue();
//        value.put("name", "Hiiii " + new Random().nextInt());
//        int user_id = users.create(value);
//
//        Log.e(">>", users.select() + "");
//
        Mobiles mobiles = new Mobiles(this);
//        ORecordValue mobile = new ORecordValue();
//        mobile.put("name", "Moto G5+");
//        mobile.put("device_type", "android");
//        mobile.put("user_id", user_id);
//        mobiles.create(mobile);

        ORecord record = mobiles.browse(1);
        Log.e(">>", record + "<<");

        Log.e(">>", "DEVICE ? " + record.getEnumValue("device_type"));

        Log.e(">>",record.readMany2One("user_id")+"<<");


//        Log.e(">>", mobiles.select()+"<<");
    }
}
