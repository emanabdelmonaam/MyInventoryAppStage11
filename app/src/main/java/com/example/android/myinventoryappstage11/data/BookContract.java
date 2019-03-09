package com.example.android.myinventoryappstage11.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.content.ContentResolver;

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final  String CONTENT_AUTHORITY ="com.example.android.myinventoryappstage11";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS ="books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BOOKS);


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** Name of database table*/
        public final static String TABLE_NAME = "Books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME ="name";
        public final static String COLUMN_BOOK_PRICE ="price";
        public final static String COLUMN_BOOK_QUANTITY ="quantity";
        public final static String COLUMN_BOOK_TYPE ="book_type";
        public final static String COLUMN_BOOK_SUPPLIER_NAME ="supplierName";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE ="supplierPhone";
        public final static String COLUMN_BOOK_IMAGE = "image";


        public final static String COLUMN_BOOK_CHICK_ONE ="chick_one";
        public final static String COLUMN_BOOK_CHICK_TWO ="chick_two";
        /**
         * Possible values for the type of the book.
         */
        public static final String ALL ="All" ;
        public static final String EBOOK = "EBook";
        public static final String PAPER = "paper";

        public static boolean isValidType(String book_type) {
            if (book_type == ALL || book_type == EBOOK || book_type == PAPER) {

                return true;
            }
            return false;
        }

    }

}