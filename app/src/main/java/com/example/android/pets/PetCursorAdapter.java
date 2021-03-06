package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by monash on 11/28/2016.
 */


    /**
     * {@link PetCursorAdapter} is an adapter for a list or grid view
     * that uses a {@link Cursor} of pet data as its data source. This adapter knows
     * how to create list items for each row of pet data in the {@link Cursor}.
     */
    public class PetCursorAdapter extends CursorAdapter {

        /**
         * Constructs a new {@link PetCursorAdapter}.
         *
         * @param context The context
         * @param c       The cursor from which to get the data.
         */
        public PetCursorAdapter(Context context, Cursor c) {
            super(context, c, 0 /* flags */);
        }

        /**
         *
          The newView method is used to inflate a new view and return it,
          you don't bind any data to the view at this point.
         *
         * @param context app context
         * @param cursor  The cursor from which to get the data. The cursor is already
         *                moved to the correct position.
         * @param parent  The parent to which the new view is attached to
         * @return the newly created list item view.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        /**
         * This method binds the pet data (in the current row pointed to by cursor) to the given
         * list item layout. For example, the name for the current pet can be set on the name TextView
         * in the list item layout.
         *
         * @param view    Existing view, returned earlier by newView() method
         * @param context app context
         * @param cursor  The cursor from which to get the data. The cursor is already moved to the
         *                correct row.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // get references to the text views that will display the data
            TextView petName = (TextView) view.findViewById(R.id.name);
            TextView petBreed = (TextView) view.findViewById(R.id.summary);

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String breed = cursor.getString(cursor.getColumnIndexOrThrow("breed"));

            // Populate fields with extracted properties
            petName.setText(name);
            petBreed.setText(breed);

        }
    }

