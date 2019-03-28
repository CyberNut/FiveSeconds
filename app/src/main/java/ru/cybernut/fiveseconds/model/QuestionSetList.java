package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;
import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

public class QuestionSetList {

    private static QuestionSetList questionSetList;
    private SQLiteDatabase database;

    public static QuestionSetList getInstance() {
        if(questionSetList == null) {
            questionSetList = new QuestionSetList();
        }
        return questionSetList;
    }

    private QuestionSetList() {
        this.database = FiveSecondsBaseHelper.getInstance().getWritableDatabase();
    }

    private static ContentValues getContentValues(QuestionSet questionSet) {
        ContentValues values = new ContentValues();
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME, questionSet.getName());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.TYPE, questionSet.getType());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SOUNDS_LINK, questionSet.getSoundsLink());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SOUNDS_LOADED, questionSet.isSoundsLoaded());
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

        try (FiveSecondsCursorWrapper cursorWrapper = queryQuestionSets(null, null)) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                sets.add(cursorWrapper.getQuestionSet());
                cursorWrapper.moveToNext();
            }
        }
        return sets;
    }

    public QuestionSet getQuestionSet(int id) {
        try (FiveSecondsCursorWrapper cursor = queryQuestionSets("_id = ?", new String[]{String.valueOf(id)})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getQuestionSet();
        }
    }

    public void updateQuestionSet(QuestionSet questionSet) {
        String name = questionSet.getName();
        ContentValues contentValues = QuestionSetList.getContentValues(questionSet);
        database.update(FiveSecondsDBSchema.QuestionSetsTable.NAME, contentValues, FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME + " = ?", new String[] {name});
    }
}
