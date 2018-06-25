package com.example.android.myinventoryappstage11;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.myinventoryappstage11.data.BookContract;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the Book Data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the Book's
     */
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Spinner mTypeSpinner;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private int mType = BookContract.BookEntry.ALL;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    private int quantity;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {

            // This is a new pet, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_pet));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else { //other stuff }

            setTitle(getString(R.string.editor_activity_title_edit_pet));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null,  this);

        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mTypeSpinner = (Spinner) findViewById(R.id.type_of_book);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplir_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
       // if(savedInstanceState != null){
         //   mBookHasChanged = savedInstanceState.getBoolean("mBookHasChanged");
        //}
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        final ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
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
    private void saveBook() {
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

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceBString)
                && TextUtils.isEmpty(quantityBString)
                && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)
                && mType == BookContract.BookEntry.ALL) {
           // Toast.makeText(this, R.string.you_did_not_add_any_Book,
             //       Toast.LENGTH_SHORT).show();
            return;
        } else {

            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceB);
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityB);
            values.put(BookContract.BookEntry.COLUMN_BOOK_TYPE, mType);
            values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneB);

            // Show a toast message depending on whether or not the insertion was successful
            if (mCurrentBookUri == null) {

                // Insert a new pet into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // Save Book to database
                saveBook();

                // Exit activity
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:

               // Pop up confirmation dialog for deletion
               showDeleteConfirmationDialog();
            return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                OnClickListener discardButtonClickListener =
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */

    @Override
    public void onBackPressed() {

        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        OnClickListener discardButtonClickListener =
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_TYPE,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in

            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int typeColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TYPE);
            int supplierNColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentType = cursor.getString(typeColumnIndex);
            String currentSupplierN = cursor.getString(supplierNColumnIndex);
            int currentSupplierP = cursor.getInt(supplierPColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(currentName);
            mPriceEditText.setText(Integer.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mSupplierNameEditText.setText(currentSupplierN);
            mSupplierPhoneEditText.setText(Integer.toString(currentSupplierP));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (mType) {
                case BookContract.BookEntry.EBOOK:
                    mTypeSpinner.setSelection(1);
                    break;
                case BookContract.BookEntry.PAPER:
                    mTypeSpinner.setSelection(2);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {


        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mTypeSpinner.setSelection(0); // Select "all"
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

        /**
         * Show a dialog that warns the user there are unsaved changes that will be lost
         * if they continue leaving the editor.
         *
         * @param discardButtonClickListener is the click listener for what to do when
         *                                   the user confirms they want to discard their changes
         */

        private void showUnsavedChangesDialog(
                DialogInterface.OnClickListener discardButtonClickListener) {

            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the book.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void showDeleteConfirmationDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
