package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
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
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        // INTEGER VALUES REPRESENTING: MALE, FEMALE OR UNKNOWN
        public static final int PET_GENDER_UNKNOWN = 0;
        public static final int PET_GENDER_MALE = 1;
        public static final int PET_GENDER_FEMALE = 2;

        // CONTENT AUTHORITY
        public static final String CONTENT_AUTHORITY = "com.example.android.pets";

        // TO MAKE THIS STRING INTO A URI WE USE THE 'parse' METHOD
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        // TABLE NAME FOR THE PATH SECTION OF THE URI
        public static final String PATH_PETS = "pets";

        // APPENDS THE BASE CONTENT URI(which contains scheme and authority) TO THE PATH SEGMENT
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * Returns whether or not the given gender is {@link #PET_GENDER_UNKNOWN}, {@link #PET_GENDER_MALE},
         * or {@link #PET_GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == PET_GENDER_UNKNOWN || gender == PET_GENDER_MALE || gender == PET_GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }
}
