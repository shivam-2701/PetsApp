package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


import com.example.android.pets.data.PetsContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="shelter.db";
    private static final int VERSION=1;
    public PetDbHelper(@Nullable Context context) {
        super(context,DATABASE_NAME ,null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetEntry.TABLE_NAME+" ("+
                PetEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+PetEntry.COLUMN_PETS_NAME+
                " TEXT NOT NULL,"+PetEntry.COLUMN_PETS_BREED+" TEXT, " + PetEntry.COLUMN_PETS_GENDER+" INTEGER NOT NULL, "+
                PetEntry.COLUMN_PETS_WEIGHT+" INTEGER NOT NULL DEFAULT 0 );";
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
