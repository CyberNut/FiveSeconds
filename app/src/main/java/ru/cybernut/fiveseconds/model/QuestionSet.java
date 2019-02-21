package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.FSTable;

public class QuestionSet {

    private static QuestionSet questionSet;
    private Context context;
    private SQLiteDatabase database;

    public static QuestionSet getInstance(Context context) {
        if(questionSet == null) {
            questionSet = new QuestionSet(context);
        }
        return questionSet;
    }

    private QuestionSet(Context context) {
        this.context = context;
        this.database = FiveSecondsBaseHelper.getInstance(this.context).getWritableDatabase();
    }

    private static ContentValues getContentValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(FSTable.Cols.UUID, question.getId().toString());
        values.put(FSTable.Cols.QUESTION_TEXT, question.getText());
        return values;
    }

    private FiveSecondsCursorWrapper queryQuestions(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                FSTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FiveSecondsCursorWrapper(cursor);
    }

    public List<String> getRandomIdList(int numberOfQuestions) {
        int recievedQuestion = 0;
        int count = 0;
        ArrayList<Integer> fullIDList = new ArrayList<>();
        ArrayList<String> randomIDList = new ArrayList<>();
        Random random = new Random();
        //get number of records in DB
        FiveSecondsCursorWrapper cursor = queryQuestions(null, null);
        count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                fullIDList.add(cursor.getInt(0));
            }
            int min = Math.min(count, numberOfQuestions);
            while (recievedQuestion < min) {
                int tempId = fullIDList.get(random.nextInt(count));
                if (!randomIDList.contains(tempId)) {
                    cursor = queryQuestions("_id = ?", new String[] {String.valueOf(tempId)});
                    if(cursor.moveToFirst()) {
                        String uuidString = cursor.getString(cursor.getColumnIndex(FSTable.Cols.UUID));
                        randomIDList.add(uuidString);
                        recievedQuestion++;
                    }
                }
            }
        }
        return randomIDList;
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        FiveSecondsCursorWrapper cursorWrapper = queryQuestions(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                questions.add(cursorWrapper.getQuestion());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return questions;
    }

    public void addQuestion(Question question) {
        ContentValues values = QuestionSet.getContentValues(question);
        database.insert(FSTable.NAME, null, values);
    }

    public void updateQuestion(Question question) {
        String uuidString = question.getId().toString();
        ContentValues contentValues = QuestionSet.getContentValues(question);
        database.update(FSTable.NAME, contentValues, FSTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    public void deleteQuestion(Question question) {
        String uuidString = question.getId().toString();
        database.delete(FSTable.NAME, FSTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    public Question getQuestion(String id){
        FiveSecondsCursorWrapper cursor = queryQuestions(FSTable.Cols.UUID + " = ?", new String[] {id});
        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getQuestion();
        } finally {
            cursor.close();
        }
    }
}
