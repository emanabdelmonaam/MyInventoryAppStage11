package com.example.android.myinventoryappstage11.data;

import android.provider.BaseColumns;

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        public final static String TABLE_NAME = "Books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME ="name";
        public final static String COLUMN_BOOK_PRICE ="price";
        public final static String COLUMN_BOOK_QUANTITY ="quantity";
        public final static String COLUMN_BOOK_SUPPLIER_NAME ="supplierName";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE ="supplierPhone";
        public final static String COLUMN_BOOK_TYPE = "book type";

        /**
         * Possible values for the type of the book.
         */
        public static final int ALL = 0;
        public static final int EBOOK = 1;
        public static final int PAPER = 2;
    }

}