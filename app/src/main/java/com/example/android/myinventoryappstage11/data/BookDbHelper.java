package com.example.android.myinventoryappstage11.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.myinventoryappstage11.data.BookContract.BookEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "book.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 4;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the Book table
        String SQL_CREATE_BOOKS_TABLE = " CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ( "
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0 , "
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER ,"
                + BookEntry.COLUMN_BOOK_TYPE + " TEXT ,"
                + BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT ,"
                + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " INTEGER ,"
                + BookEntry.COLUMN_BOOK_IMAGE + " TEXT NOT NULL ,"
                + BookEntry.COLUMN_BOOK_CHICK_ONE + " Text ,"
                + BookEntry.COLUMN_BOOK_CHICK_TWO + " Text "

                +");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}