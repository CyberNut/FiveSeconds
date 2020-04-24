package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;
import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

public class QuestionSetList {

    private static final String TAG = "QuestionSetList";
    private static QuestionSetList questionSetList;
    private SQLiteDatabase database;

    public static QuestionSetList getInstance() {
        if(questionSetList == null) {
            questionSetList = new QuestionSetList();
        }
        return questionSetList;
    }

    private QuestionSetList() {
        FiveSecondsBaseHelper fiveSecondsBaseHelper = FiveSecondsBaseHelper.getInstance();
        try {
            fiveSecondsBaseHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            this.database = fiveSecondsBaseHelper.getInstance().getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
    }

    private static ContentValues getContentValues(QuestionSet questionSet) {
        ContentValues values = new ContentValues();
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME, questionSet.getName());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.TYPE, questionSet.getType());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SHOP_ITEM_ID, questionSet.getShopItemId());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.OWNED, questionSet.getOwned());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SOUNDS_LINK, questionSet.getSoundsLink());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SOUNDS_LOADED, questionSet.isSoundsLoaded());
        values.put(FiveSecondsDBSchema.QuestionSetsTable.Cols.SOUNDS_FILES_SIZE, questionSet.getSoundsFilesSize());
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
            if(cursorWrapper.moveToFirst()) {
                while (!cursorWrapper.isAfterLast()) {
                    sets.add(cursorWrapper.getQuestionSet());
                    cursorWrapper.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "getQuestionSets: " + e.getMessage());
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

    public boolean setOwned(String itemId) {
        try (FiveSecondsCursorWrapper cursorWrapper = queryQuestionSets("shop_item_id = ?", new String[]{String.valueOf(itemId)})) {
            if(cursorWrapper.moveToFirst()) {
                while (!cursorWrapper.isAfterLast()) {
                    QuestionSet temp = cursorWrapper.getQuestionSet();
                    temp.setOwned(1133);
                    updateQuestionSet(temp);
                    cursorWrapper.moveToNext();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void updateQuestionSet(QuestionSet questionSet) {
        String name = questionSet.getName();
        ContentValues contentValues = QuestionSetList.getContentValues(questionSet);
        database.update(FiveSecondsDBSchema.QuestionSetsTable.NAME, contentValues, FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME + " = ?", new String[] {name});
    }
}
