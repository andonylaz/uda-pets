package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Created by monash on 11/24/2016.
 *
 *
 * {@link ContentProvider} for Pets app.
 *
 */

public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /**  PetDbHelper object to be used in class */
    private PetDbHelper mDbHelper;

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        // URIs that sUriMatcher recognises. One is for the entire 'pet's table'
        // the other is for a row in the pet's table
        sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetEntry.PATH_PETS, PETS);
        sUriMatcher.addURI(PetEntry.CONTENT_AUTHORITY, PetEntry.PATH_PETS + "/#", PET_ID);

    }



    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // get a reference to the database object, that is in a readable capacity
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // get the code that is associated with that particular URI, if there is one
        int match = sUriMatcher.match(uri);

        switch (match){

            case PETS:
                // For the PETS code we query the database with the given arguments, not changing
                // anything
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
                break;

            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                selection = PetEntry._ID + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI is not recognised: " + uri);
        }

        return cursor;
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }



    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // get the code linked to the uri passed
        final int match = sUriMatcher.match(uri);

        // depending on the code, execute that particular action
        switch (match){
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }



    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Check that the values entered do not equal null
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        if (breed == null) {
            throw new IllegalArgumentException("Pet requires a breed");
        }
        int gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender > 2 || gender < 0) {
            throw new IllegalArgumentException("Pet requires a valid gender(eg. 0, 1 or 2)");
        }
        int weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        String weightString = Integer.toString(weight);
        // check if weight is negative or is null
        if (weight < 0 || weightString == null){
            throw new IllegalArgumentException("Pet has negative weight or has null value");
        }

        // get a reference to the database object that is writable
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // inserts the data that the ContentValues contains
        long id = database.insert(PetEntry.TABLE_NAME, null, values);



        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }



    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     *
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     *
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        /** MY VERSION OF THIS
        // check if the ContentValue object contains a new name
        boolean isNameEmpty = values.containsKey(PetEntry.COLUMN_PET_NAME);

        // check if the ContentValue object contains a new breed
        boolean isBreedEmpty = values.containsKey(PetEntry.COLUMN_PET_BREED);

        // check if the ContentValue object contains a new name
        boolean isGenderEmpty = values.containsKey(PetEntry.COLUMN_PET_GENDER);

        // check if the ContentValue object contains a new name
        boolean isWeightEmpty = values.containsKey(PetEntry.COLUMN_PET_WEIGHT);

        // if the user doesn't put any data into the database but tries to update it
        // then throw exception
        if ((isNameEmpty && isBreedEmpty && isGenderEmpty && isWeightEmpty) == false){
            throw new IllegalArgumentException("No data has been passed to database");
        }
         */

        // UDACITY'S VERSION

        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }




        // get a writable reference to the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

        // Return the number of rows that were affected
        return rowsUpdated;
    }
}
