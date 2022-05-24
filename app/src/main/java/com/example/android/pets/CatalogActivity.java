package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetsContract.PetEntry;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    String [] mprojection ={PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED};
    PetCursorAdapter mpetCursorAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });
        ListView listView = (ListView)findViewById(R.id.list);


        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent =new Intent(CatalogActivity.this,EditorActivity.class);
            intent.setData(ContentUris.withAppendedId(PetEntry.CONTENT_URI, id));
            startActivity(intent);
        });
        mpetCursorAdapter =new PetCursorAdapter(this,null);
        getSupportLoaderManager().initLoader(0, null, this);
        listView.setAdapter(mpetCursorAdapter);


    }


    @Override
    protected void onStart() {
        super.onStart();


    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void insertPet() {


        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newRowId = getContentResolver().insert(PetEntry.CONTENT_URI,values);



    }
    //Test

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
            // Respond to a click on the "Insert dummy com.example.android.pets.data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case 0:
                return new CursorLoader(this,PetEntry.CONTENT_URI,mprojection,null,null,null);
                default:
                throw new IllegalArgumentException("Wrong Id IN LOADER");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.i("INFO","LoaderCalled");
            mpetCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mpetCursorAdapter.swapCursor(null);
    }
}