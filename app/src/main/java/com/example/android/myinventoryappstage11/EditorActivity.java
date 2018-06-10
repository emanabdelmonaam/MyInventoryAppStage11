package com.example.android.myinventoryappstage11;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.myinventoryappstage11.data.BookContract;
import com.example.android.myinventoryappstage11.data.BookDbHelper;
//import com.example.android.books.data.BookContract.BookEntry;
//import com.example.android.books.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mPriceEditText;

    /** EditText field to enter the pet's weight */
    private EditText mQuantityEditText;

    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mTypeSpinner;

    /**
     * Gender of the pet. The possible valid values are in the PetContract.java file:
     * { PetEntry#GENDER_UNKNOWN}, {PetEntry#GENDER_MALE}, or
     * {PetEntry#GENDER_FEMALE}.
     */
    private int mType = BookContract.BookEntry.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mTypeSpinner = (Spinner) findViewById(R.id.type_of_book);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplir_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone);


        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.book_ebook))) {
                        mType = BookContract.BookEntry.EBOOK;
                    } else if (selection.equals(getString(R.string.book_paper))) {
                        mType = BookContract.BookEntry.PAPER;
                    } else {
                        mType = BookContract.BookEntry.ALL;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = BookContract.BookEntry.ALL;
            }
        });
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceBString = mPriceEditText.getText().toString().trim();
        int priceB = Integer.parseInt(priceBString);

        String quantityBString = mQuantityEditText.getText().toString().trim();
        int quantityB = Integer.parseInt(quantityBString);

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();

        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int supplierPhoneB = Integer.parseInt(supplierPhoneString);


        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceBString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityB);
        values.put(BookContract.BookEntry.COLUMN_BOOK_TYPE, mType);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierPhoneB);

        // Insert a new row for Book in the database, returning the ID of that new row.
        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving Book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Book to database
                insertBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
