package com.oogbox.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.support.orm.data.ORecordValue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Users users = new Users(this);
//
//        ORecordValue value = new ORecordValue();
//        value.put("name", "Hiiii " + new Random().nextInt());
//
//        RelationValue mobiles = new RelationValue();
//        mobiles.append(new ORecordValue()
//                        .add("name", "Lenovo K5 Note")
//                        .add("device_type", "Android"),
//                new ORecordValue()
//                        .add("name", "iPhone 5s")
//                        .add("device_type", "ios"));
//        value.put("mobile_ids", mobiles);
//        int user_id = users.create(value);
//        Log.e(">>", "Recent created - >>>>>>>>>> ##" + user_id);
////
//        Log.e(">>", users.select() + "");
//
        Mobiles mobiles = new Mobiles(this);
        mobiles.delete(null, null);
        ORecordValue mobile = new ORecordValue();
        mobile.put("name", "Moto G5+");
        mobile.put("device_type", "android");
        mobile.put("user_id", new ORecordValue()
                .add("name", "Dharmang Soni"));
        mobiles.create(mobile);

//        ORecord record = mobiles.browse(1);
//
//        Log.e(">>BROWSE", record + "<<");
//
//        Log.e(">>ENUM", "DEVICE ? " + record.getEnumValue("device_type"));
//
//        Log.e(">>M2O", record.readMany2One("user_id") + "<<");


//        ORecord user = users.browse(user_id);
//        Log.e(">O2M", user.readOne2Many("mobile_ids") + "<<");

        Log.e(">>", mobiles.select()+"<<");
    }
}
