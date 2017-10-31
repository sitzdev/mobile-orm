package com.oogbox.sample.models;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.types.OManyToMany;
import com.oogbox.support.orm.types.OVarchar;

@DataModel("sys.users")
public class Users extends BaseModel {

    OVarchar name = new OVarchar("Name");

    OManyToMany mobile_ids = new OManyToMany("Mobiles", Mobiles.class)
            .baseColumn("user_id")
            .relColumn("mobile_id")
            .relTableName("user_mobiles_rel");

    public Users(Context context) {
        super(context);
    }
}
