package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;
import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

public class QuestionSetList {

    private static final String TAG = "QuestionSetList";
    private static QuestionSetList questionSetList;
    private FiveSecondsBaseHelper fiveSecondsBaseHelper;

    public static QuestionSetList getInstance() {
        if(questionSetList == null) {
            questionSetList = new QuestionSetList();
        }
        return questionSetList;
    }

    private QuestionSetList() {
        fiveSecondsBaseHelper =  FiveSecondsBaseHelper.getInstance();
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
        SQLiteDatabase database = fiveSecondsBaseHelper.getReadableDatabase();
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

    public boolean setOwned(String itemId, int value) {
        try (FiveSecondsCursorWrapper cursorWrapper = queryQuestionSets("shop_item_id = ?", new String[]{String.valueOf(itemId)})) {
            if(cursorWrapper.moveToFirst()) {
                while (!cursorWrapper.isAfterLast()) {
                    QuestionSet temp = cursorWrapper.getQuestionSet();
                    temp.setOwned(value);
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
        SQLiteDatabase database = fiveSecondsBaseHelper.getWritableDatabase();
        String name = questionSet.getName();
        ContentValues contentValues = QuestionSetList.getContentValues(questionSet);
        database.beginTransaction();
        try {
            database.update(FiveSecondsDBSchema.QuestionSetsTable.NAME, contentValues, FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME + " = ?", new String[] {name});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update QuestionSet in database");
        } finally {
            database.endTransaction();
        }
    }
}
