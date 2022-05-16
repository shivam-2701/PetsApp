package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    public PetDbHelper mdbHelper;
    /**
     * Initialize the provider and the database helper object.
     */

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

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PetEntry.TABLE_NAME ,PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PetEntry.TABLE_NAME +"/#" ,PET_ID);


    }
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mdbHelper =new PetDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
//       SQLiteDatabase db = mdbHelper.getWritableDatabase();
//        Cursor cursor = db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
//        return cursor;
        SQLiteDatabase db =mdbHelper.getReadableDatabase();
        int match= sUriMatcher.match(uri);
        Cursor cursor;
        switch(match){
            case PETS: cursor =db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null, sortOrder);
                break;
            case PET_ID:
                selection = PetsContract.PetEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor =db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null, sortOrder);
                break;
          default:
                throw new IllegalArgumentException("Cannot query unknown uri "+ uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}