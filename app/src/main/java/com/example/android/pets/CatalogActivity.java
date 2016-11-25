/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        // get an instance of PetDbHelper
        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }


    /**
     * When the activity is 'continued'. This will be called after the user leaves then
     * comes back to it.
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // get a reference to the textview
        TextView displayView = (TextView)findViewById(R.id.text_view_pet);


        // Define projection that specifies which columns from the db you will use for this query.
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };

        // finally, use the query() method with the parameters that were defined above
        /*Cursor cursor = db.query(
                PetEntry.TABLE_NAME,                      // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        */

        // Perform a query on the provider using the ContentResolver
        // Use the {@link data.PetContract.PetEntry#CONTENT_URI} to access the pet data
        Cursor cursor = getContentResolver().query(
                PetEntry.CONTENT_URI,   // The content uri of the pets table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

        try {
            // Display the data that was retrieved and stored in the cursor

            displayView.setText("Number of rows in pets(*) database table: " + cursor.getCount());

            int petId = cursor.getColumnIndex(PetEntry._ID);
            int petName = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int petBreed = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int petGender = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int petWeight = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // move the cursor to the first position
            //cursor.moveToFirst();

            // keep moving through cursor until end
            while (cursor.moveToNext()) {
                int petIdValue = cursor.getInt(petId);
                String petNameValue = cursor.getString(petName);
                String petBreedValue = cursor.getString(petBreed);
                int petGenderValue = cursor.getInt(petGender);
                String genderString = getGender(petGenderValue);
                int petWeightValue = cursor.getInt(petWeight);

                displayView.append(" \n" + petIdValue + " " +
                        petNameValue + " " +
                        petBreedValue + " " +
                        genderString + " " +
                        petWeightValue);

            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    public void insertDummyData(){


        // Create a new content value class and insert the dummy data
        // Create a new map of values, where column names are the keys
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_PET_NAME, "Toto");
        contentValues.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.PET_GENDER_MALE);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT, 7);


        Uri returnedUri =getContentResolver().insert(PetEntry.CONTENT_URI, contentValues);

        Toast.makeText(this, "Dummy data inserted into database", Toast.LENGTH_SHORT).show();
    }


    public String getGender(int gender){
        switch(gender){
            case 1:
                return "male";
            case 2:
                return "female";
            default:
                return "unknown";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                displayDatabaseInfo();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
