package ru.cybernut.fiveseconds.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.*;

public class FiveSecondsBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "fiveSeconds.db";

    public FiveSecondsBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FSTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FSTable.Cols.UUID + ", " +
                FSTable.Cols.QUESTION_TEXT +") "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

