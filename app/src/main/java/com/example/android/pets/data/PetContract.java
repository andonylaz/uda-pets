package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by monash on 11/22/2016.
 */

public final class PetContract {

    /**
     *   To prevent someone from accidentally instantiating the contract class,
     *   make the constructor private.
     */
    private PetContract() {}

    /* Inner class that defines the table contents */
    public static class PetEntry implements BaseColumns {

        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BREED = "breed";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_WEIGHT = "weight";

        // INTEGER VALUES REPRESENTING: MALE, FEMALE OR UNKNOWN
        public static final int PET_GENDER_UNKNOWN = 0;
        public static final int PET_GENDER_MALE = 1;
        public static final int PET_GENDER_FEMALE = 2;
    }
}
