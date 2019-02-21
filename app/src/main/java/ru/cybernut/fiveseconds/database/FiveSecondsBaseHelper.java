package ru.cybernut.fiveseconds.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionSet;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.*;

public class FiveSecondsBaseHelper extends SQLiteOpenHelper {

    private static FiveSecondsBaseHelper instance;
    private Context context;
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "five_seconds.db";

    public static FiveSecondsBaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new FiveSecondsBaseHelper(context);
        }
        return instance;
    }

    private FiveSecondsBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
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

