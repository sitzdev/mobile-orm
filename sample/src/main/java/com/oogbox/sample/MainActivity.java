package com.oogbox.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.DataModels;
import com.oogbox.support.orm.utils.MetaReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(">>", MetaReader.getDatabaseName(this) + "<<");
        Log.e(">>", MetaReader.getDatabaseVersion(this) + "<<");


        Users users = (Users) DataModels.get(this, "system.users");
        Log.e(">>", users + "<<");

//        Log.e(">>", users.getModel("res.partner") + "");

        Log.e(">>leads->> ", DataModels.get(this, "crm.lead") + " <<");
    }
}
