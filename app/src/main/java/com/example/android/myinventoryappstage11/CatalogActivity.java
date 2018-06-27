package com.example.android.myinventoryappstage11;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.myinventoryappstage11.data.BookContract;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 1;
    private BookCursorAdapter mCursorAdabter;


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

        ListView bookListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);

        bookListView.setEmptyView(emptyView);

        mCursorAdabter= new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdabter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void  insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, "hello");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, "76");
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY,"7");
        values.put(BookContract.BookEntry.COLUMN_BOOK_TYPE, BookContract.BookEntry.EBOOK);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,"Amazon");
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,"91383849");

        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
        Log.v("CatalogActivity", "New row URI: " + newUri);

    }

    /**
     * Helper method to delete all pets in the database.
     */

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
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
//               insertBook();
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, "hello");
                values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, "76");
                values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY,"7");
                values.put(BookContract.BookEntry.COLUMN_BOOK_TYPE, BookContract.BookEntry.EBOOK);
                values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,"Amazon");
                values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,"91383849");

                Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
                Log.v("CatalogActivity", "New row URI: " + newUri);

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                //getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookContract.BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        mCursorAdabter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdabter.swapCursor(null);
    }
}

