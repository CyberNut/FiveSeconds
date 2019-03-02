package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;
import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

public class QuestionSetList {

    private static QuestionSetList questionSetList;
    private Context context;
    private SQLiteDatabase database;

    public static QuestionSetList getInstance(Context context) {
        if(questionSetList == null) {
            questionSetList = new QuestionSetList(context);
        }
        return questionSetList;
    }

    private QuestionSetList(Context context) {
        this.context = context;
        this.database = FiveSecondsBaseHelper.getInstance(this.context).getWritableDatabase();
    }

    private static ContentValues getContentValues(QuestionSet questionSet) {
        ContentValues values = new ContentValues();
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME, questionSet.getName());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.TYPE, questionSet.getType());
        return values;
    }

    private FiveSecondsCursorWrapper queryQuestionSets(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                FiveSecondsDBSchema.QuestionSetsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FiveSecondsCursorWrapper(cursor);
    }

    public List<QuestionSet> getQuestionSets() {
        List<QuestionSet> sets = new ArrayList<>();

        FiveSecondsCursorWrapper cursorWrapper = queryQuestionSets(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                sets.add(cursorWrapper.getQuestionSet());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return sets;
    }


}
