package com.example.android.myinventoryappstage11;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryappstage11.data.BookContract;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Inflate a list item view using the layout specified in list_item.xml
        View emptyListItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return emptyListItem;
        //return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
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
    public void bindView(View view, final Context context, Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));
        final Uri currentUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, Long.parseLong(currentId));

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView typeTextView = (TextView) view.findViewById(R.id.type);
        TextView saNamTextView = (TextView) view.findViewById(R.id.supplier_n);
        TextView saPhoneTextView = (TextView) view.findViewById(R.id.supplier_p);


        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        int typeColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TYPE);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);


        // Read the pet attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        String bookType = cursor.getString(typeColumnIndex);
        String bookSupplierN = cursor.getString(supplierNameColumnIndex);
        String bookSupplierP = cursor.getString(supplierPhoneColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);
        typeTextView.setText(bookType);
        saNamTextView.setText(bookSupplierN);
        saPhoneTextView.setText(bookSupplierP);

        final int itemQuantity =Integer.parseInt(bookQuantity);

        Button buyButton = view.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemQuantity > 0) {
                    ContentValues newValues = new ContentValues();

                    int newItemQuantity = itemQuantity - 1;

                    newValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, newItemQuantity);

                    int newQuantity = context.getContentResolver().update(currentUri, newValues, null, null);

                    if (newQuantity == 0)
                        Toast.makeText(context, R.string.wrong_quantity, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, R.string.wrong_quantity, Toast.LENGTH_SHORT).show();
            }
        });
    }
}