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
        QuestionSet questionSet = QuestionSet.getInstance(context);
        questionSet.addQuestion(new Question("Name 3 Mathematical symbols"));
        questionSet.addQuestion(new Question("Name 3 countries you really want to visit"));
        questionSet.addQuestion(new Question("Name 3 dice games"));
        questionSet.addQuestion(new Question("Name 3 comedians"));
        questionSet.addQuestion(new Question("Name 3 talk show hosts"));
        questionSet.addQuestion(new Question("Name 3 breakfast foods"));
        questionSet.addQuestion(new Question("Name 3 deceased actresses"));
        questionSet.addQuestion(new Question("Name 3 movie theatre snacks"));
        questionSet.addQuestion(new Question("Name 3 sports not played with a ball"));
        questionSet.addQuestion(new Question("Name 3 types of jewellery"));
        questionSet.addQuestion(new Question("Name 3 famous TV detectives"));
        questionSet.addQuestion(new Question("Name 3 things to do with water"));
        questionSet.addQuestion(new Question("Name 3 sports played with a ball"));
        questionSet.addQuestion(new Question("Name 3 Pokemon"));
        questionSet.addQuestion(new Question("Name 3 things you can cut"));
        questionSet.addQuestion(new Question("Name 3 TV game or quiz shows"));
        questionSet.addQuestion(new Question("Name 3 apps on your phone"));
        questionSet.addQuestion(new Question("Name 3 airlines"));
        questionSet.addQuestion(new Question("Name 3 things that can cause death"));
        questionSet.addQuestion(new Question("Name 3 children's TV show"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

