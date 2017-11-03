# Mobile ORM for Android

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)

Easy, Developer Friendly and fully customizable ORM for Android Databases works with Data Mapping between models (tables). 

Easily manage relation between tables and browse records for ManyToOne, ManyToMany and OneToMany without writing query or making object of related model (table).

DOWNLOAD
========

Android Gradle:

*Android Studio 3.0+*
```gradle
implementation 'com.oogbox.support:mobile-orm:1.0.0'
```

*Older Android Studio*

```gradle
compile 'com.oogbox.support:mobile-orm:1.0.0'
```

FEATURES
=======

- Auto Database Creation. (No worry for create statements)
- Different data types support; Basic and relation types (ManyToOne, ManyToMany and OneToMany)
- Easy record browsing for relation records
- Content Provider implimentation (Minor setup and all done). Mainly for sync-adapters and services
- `DataResolver` for better query management with Content Provider
- Quick Relation record create support (Directly create ManyToOne, OneToMany or ManyToMany records for main record creation)
- and much more...

GETTING STARTED
===========

Before start to use ``mobile-orm`` you required to set some basic configuration.

**Registering Meta in AndroidManifest.xml**

You can init ``MobileORMConfig`` with key ``DATABASE_CONFIG`` and path of your class

```xml
  <application ....>
  ....
  		<meta-data android:name="DATABASE_CONFIG"
  				   android:value="com.my.application.db.DatabaseConfig"/>
  ...
  </application>
```

Here, ``com.my.application.db.DatabaseConfig`` is class with your database configuration which extends ``MobileORMConfig`` class.

```java
public class DatabaseConfig extends MobileORMConfig {

    public DatabaseConfig(Context context) {
        super(context);
    }

    @Override
    public String getDatabaseName() {
        return "MobileORMSample.db";
    }

    @Override
    public String authority() {
        return getContext().getString(R.string.db_authority);
    }
}
```

Reason behind making Config class and adding path to meta-data?

Yes, because in some case when you need to create database based on user properties such as, different database for different user. That can be handled from this configuration.

``authority()`` is optional if you need to use ``ContentProvider`` mechanism.

**Changing Database version:**

```xml
  <application ....>
  ....
  		<meta-data android:name="DATABASE_VERSION"
  				   android:value="2"/>
  ...
  </application>
```

That's it. You have configured your ``MobileORM``.

What's next ?
=======

1. Creating Model (Table)
2. Register model in configuration
3. Data types (Fields)
4. CRUD Operations
5. Working Relation records (ManyToOne, OneToMany, ManyToMany)
6. Setting up for Content provider
7. Working with ``DataResolver``

Give Feedback
=========

- Please report bug or isssues https://github.com/oogbox/mobile-orm/issues
- or write us on hello@oogbox.com
- Follow us on Twitter: @oogbox