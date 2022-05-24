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
        //Set notification URI on the cursor,
        // so we know what content URI the cursor was created for,
        // If the data at this URI changes, then we know we need to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
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
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // TODO: Insert a new pet into the pets database table with the given ContentValues

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it\
        checkValues(values);

        SQLiteDatabase db =mdbHelper.getWritableDatabase();
       long id = db.insert(PetsContract.PetEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }



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
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        checkUpdateValues(values);
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db =mdbHelper.getWritableDatabase();
        int updateCount =db.update(PetsContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return updateCount;

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int deleteCount=0;
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args

                 deleteCount =database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return deleteCount;

            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                deleteCount =database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return deleteCount;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    void checkUpdateValues(ContentValues values){
        if (values.containsKey(PetsContract.PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetsContract.PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetsContract.PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetsContract.PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

    }


    //Sanity checks the passed values
    void checkValues(ContentValues values){
        Integer gender = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_GENDER);
        String name = values.getAsString(PetsContract.PetEntry.COLUMN_PET_NAME);
        Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
        if(name==null){
            throw new IllegalArgumentException("Pet name cannot be null");
        }
        if(gender == null || !PetsContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Gender did not match");
        }
        if(weight!=null && weight<0){
            throw new IllegalArgumentException("Weight cannot be negative");
        }

    }
}