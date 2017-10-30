package com.oogbox.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.data.ORecordValue;
import com.oogbox.support.orm.data.RelationValue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Users users = new Users(this);
        users.delete(null, null);

        ORecordValue value = new ORecordValue();
        value.put("name", "Dharmang Soni");
        value.put("mobile_ids", new RelationValue()
                .replace(2));
//        value.put("mobile_ids", new RelationValue()
//                .append(new ORecordValue()
//                                .add("name", "Moto G5+")
//                                .add("device_type", "android")
//                        , new ORecordValue()
//                                .add("name", "iPhone 7s")
//                                .add("device_type", "ios")));
//
//
        int newId = users.create(value);
//
        Log.e(">>>o2m", users.browse(newId).readOne2Many("mobile_ids") + "<<");

        Log.e(">>", "Alll");
        Mobiles mobiles = new Mobiles(this);
        Log.e(">>", mobiles.select() + "<<");
    }
}
