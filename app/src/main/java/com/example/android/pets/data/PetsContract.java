package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetsContract {

    public static abstract class PetEntry implements BaseColumns{

        public static final String TABLE_NAME="pets";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_PETS_NAME="name";
        public static final String COLUMN_PETS_BREED="breed";
        public static final String COLUMN_PETS_GENDER="gender";
        public static final String COLUMN_PETS_WEIGHT="weight";

        /**
         * Possible values for the style of the headphone.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }

}
