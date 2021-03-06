package com.example.android.myinventoryappstage11;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.myinventoryappstage11.data.BookContract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import static com.example.android.myinventoryappstage11.data.BookProvider.LOG_TAG;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the Book Data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    //
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
    private Button mQuantityIncrease;
    private Button mQuantityDecrease;
    private Button mCallSupplier;
    private String mType = BookContract.BookEntry.ALL;

    private String mImageUri;
    private ImageView mImageView;
    private static final int SELECT_PHOTO = 100;

    private CheckBox mCheckBoxOne,mCheckBoxTwo;
    private String mCheckBox1,mCheckBox2;


    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

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

    private View.OnClickListener chkListener = new View.OnClickListener(){
        public void onClick(View v){
            if(((CheckBox)v).isChecked()){
               // Toast.makeText(getBaseContext(), "click : " + ((CheckBox) v).getText() + " es seleccionado", Toast.LENGTH_SHORT).show();

            }else{
              //  Toast.makeText(getBaseContext(),"click : " + ((CheckBox)v).getText() + " es deseleccionado",Toast.LENGTH_SHORT).show();
            }
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

            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mQuantityIncrease = (Button) findViewById((R.id.increase_button));
        mQuantityDecrease = (Button) findViewById((R.id.decrease_button));
        mTypeSpinner = (Spinner) findViewById(R.id.type_of_book);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplir_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone);
        mCallSupplier = (Button) findViewById(R.id.call_supplier);
        mImageView =( ImageView)findViewById(R.id.book_image);

        mCheckBoxOne = (CheckBox) findViewById(R.id.checkbox_1);
        mCheckBoxTwo = (CheckBox) findViewById(R.id.checkbox_2);

        mCheckBoxOne.setOnClickListener(chkListener);
        mCheckBoxTwo.setOnClickListener(chkListener);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mQuantityIncrease.setOnTouchListener(mTouchListener);
        mQuantityDecrease.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        mImageView.setOnTouchListener(mTouchListener);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

      //  mCheckBoxOne.setOnTouchListener(mTouchListener);
      //  mCheckBoxTwo.setOnTouchListener(mTouchListener);

        setupSpinner();
        increaseButton();
        decreaseButton();
        callButton();
    }


    private void openGallery(){
        Intent imageIntent;

        if (Build.VERSION.SDK_INT < 19) {
            imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            imageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        imageIntent.setType("image/*");
        startActivityForResult(imageIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri mUriSelectedImage;
                    mUriSelectedImage = data.getData();
                    if (mUriSelectedImage != null) {
                        mImageUri = mUriSelectedImage.toString();
                        mImageView.setImageBitmap(getBitmapFromUri(mUriSelectedImage));
                    }
                }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        // Get the dimensions of the View
        int targetWidth = mImageView.getWidth();
        int targetHeight = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoWidth = bmOptions.outWidth;
            int photoHeight = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void increaseButton() {
        mQuantityIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(mQuantityEditText.getText().toString());
                }
                quantity++;
                mQuantityEditText.setText("" + quantity);

            }
        });
    }

    private void decreaseButton() {
        mQuantityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(mQuantityEditText.getText().toString());
                }
                if (quantity > 0) {
                    quantity--;
                }
                mQuantityEditText.setText("" + quantity);
            }
        });
    }
    private void callButton(){
        mCallSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhoneString = mCallSupplier.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString()));
                startActivity(intent);
            }
        });
    }

    private void setCheckBoxes(){

        final CheckBox mCheckBoxOne = (CheckBox) findViewById(R.id.checkbox_1);
        if (mCheckBoxOne.isChecked()) {
           // final String strRamps =mCheckBoxOne.getText().toString();
        }
        final CheckBox mCheckBoxTwo = (CheckBox) findViewById(R.id.checkbox_2);
        if (mCheckBoxTwo.isChecked()) {
            final String strRamps =mCheckBoxTwo.getText().toString();
        }


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
        String imageString = mImageUri;
        String nameString = mNameEditText.getText().toString().trim();
        String priceBString = mPriceEditText.getText().toString().trim();
        String quantityBString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        String saveCheckOneString = mCheckBoxOne.getText().toString().trim();
        String saveCheckTwoString = mCheckBoxTwo.getText().toString().trim();

        if (mCurrentBookUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceBString)
                && TextUtils.isEmpty(quantityBString)
                && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)
                && TextUtils.isEmpty(imageString)

                && TextUtils.isEmpty(saveCheckOneString)
                && TextUtils.isEmpty(saveCheckTwoString)

                && mType == BookContract.BookEntry.ALL){

            Toast.makeText(this, R.string.you_did_not_add_any_Book,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.wrong_name),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);

        if (TextUtils.isEmpty(priceBString)) {
            Toast.makeText(this, getString(R.string.wrong_price),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceBString);

        if (TextUtils.isEmpty(quantityBString)) {
            Toast.makeText(this, getString(R.string.wrong_quantity),
                    Toast.LENGTH_SHORT).show();
            return;

        }

        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityBString);

        values.put(BookContract.BookEntry.COLUMN_BOOK_TYPE, mType);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.wrong_supplier_name),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.wrong_supplier_phone),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);

        values.put(BookContract.BookEntry.COLUMN_BOOK_IMAGE, imageString);

        values.put(BookContract.BookEntry.COLUMN_BOOK_CHICK_ONE, saveCheckOneString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_CHICK_TWO, saveCheckTwoString);


        // Show a toast message depending on whether or not the insertion was successful
        if (mCurrentBookUri == null) {
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdate = getContentResolver().update(mCurrentBookUri, values,null,null);

            if (rowsUpdate == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
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
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,

                BookContract.BookEntry.COLUMN_BOOK_IMAGE,

                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_TYPE,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,

                BookContract.BookEntry.COLUMN_BOOK_CHICK_ONE,
                BookContract.BookEntry.COLUMN_BOOK_CHICK_TWO
        };


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
            int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int typeColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TYPE);
            int supplierNColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            int checkOneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_CHICK_ONE);
            int checkTwoColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_CHICK_TWO);


            // Extract out the value from the Cursor for the given column index
            String currentImage = cursor.getString(imageColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
            float currentPrice = cursor.getFloat(priceColumnIndex);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            String priceAsString = df.format(currentPrice);
            priceAsString = priceAsString.replace(",", ".");
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentType = cursor.getString(typeColumnIndex);
            String currentSupplierN = cursor.getString(supplierNColumnIndex);
            int currentSupplierP = cursor.getInt(supplierPColumnIndex);

            String currentCheckOne = cursor.getString(checkOneColumnIndex);
            String currentCheckTwo = cursor.getString(checkTwoColumnIndex);


            // Update the views on the screen with the values from the database
            final Uri mImageU = Uri.parse(currentImage);
            mNameEditText.setText(currentName);
            mPriceEditText.setText(Float.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mSupplierNameEditText.setText(currentSupplierN);
            mSupplierPhoneEditText.setText(Integer.toString(currentSupplierP));

            //mCheckBoxOne.setText(currentCheckOne);
            //mCheckBoxTwo.setText(currentCheckTwo);

            mCheckBoxOne.setChecked(true);
            mCheckBoxOne.setChecked(true);


            // if (mCheckBoxOne.isChecked()){
              //  mCheckBoxOne.setChecked(true);
           // }else mCheckBoxOne.setChecked(false);


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

            mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    try {
                        mImageView.setImageBitmap(getBitmapFromUri(mImageU));
                        return true;
                    } finally {
                        mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                }
            });
        }
    }



    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

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
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

}