package ru.cybernut.fiveseconds.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.cybernut.fiveseconds.database.FiveSecondsBaseHelper;
import ru.cybernut.fiveseconds.database.FiveSecondsCursorWrapper;
import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.QuestionsTable;

public class QuestionList {

    private static QuestionList questionList;
    private FiveSecondsBaseHelper fiveSecondsBaseHelper;

    public static QuestionList getInstance() {
        if(questionList == null) {
            questionList = new QuestionList();
        }
        return questionList;
    }

    private QuestionList() {
        fiveSecondsBaseHelper = FiveSecondsBaseHelper.getInstance();
    }

    private static ContentValues getContentValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(QuestionsTable.Cols.UUID, question.getId().toString());
        values.put(QuestionsTable.Cols.QUESTION_TEXT, question.getText());
        return values;
    }

    private FiveSecondsCursorWrapper queryQuestions(String whereClause, String[] whereArgs) {
        SQLiteDatabase database = fiveSecondsBaseHelper.getReadableDatabase();
        Cursor cursor = database.query(
                QuestionsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FiveSecondsCursorWrapper(cursor);
    }

    private FiveSecondsCursorWrapper queryQuestionsWithNumbersOfUsage(String whereClause, String[] whereArgs) {
        SQLiteDatabase database = fiveSecondsBaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT question._id, question.uuid, question.ru_text, question.question_set_id, ifnull(usage.count, 0) FROM questions question LEFT JOIN questions_usage usage ON question.uuid = usage.uuid", whereArgs);
//        Cursor cursor = database.query(
//                QuestionsTable.NAME,
//                null,
//                whereClause,
//                whereArgs,
//                null,
//                null,
//                null
//        );
        return new FiveSecondsCursorWrapper(cursor);
    }


    public List<String> getRandomIdList(int numberOfQuestions, ArrayList<Integer> questionSetIdList) {
        int recievedQuestion = 0;
        int count;
        ArrayList<Integer> fullIDList = new ArrayList<>();
        ArrayList<String> randomIDList = new ArrayList<>();
        Random random = new Random();
        String whereClause = "";
        String[] whereArgs = new String[questionSetIdList.size()];

        if(questionSetIdList.size() > 0) {
            for (int i = 0; i < questionSetIdList.size(); i++) {
                if(whereClause.length() > 0) {
                    whereClause = whereClause + " OR question_set_id = ? ";
                } else {
                    whereClause = " question_set_id = ? ";
                }
                whereArgs[i] = questionSetIdList.get(i).toString();
            }
        }

        //get number of records in DB
        FiveSecondsCursorWrapper cursor = queryQuestions(whereClause, whereArgs);
        count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                fullIDList.add(cursor.getInt(0));
            }
            int min = Math.min(count, numberOfQuestions);
            while (recievedQuestion < min) {
                int randomValue = random.nextInt(count);
                int tempId = fullIDList.get(randomValue);
                if (!randomIDList.contains(tempId)) {
                    cursor = queryQuestions("_id = ?", new String[] {String.valueOf(tempId)});
                    if(cursor.moveToFirst()) {
                        String uuidString = cursor.getString(cursor.getColumnIndex(QuestionsTable.Cols.UUID));
                        randomIDList.add(uuidString);
                        recievedQuestion++;
                        count--;
                        fullIDList.remove(randomValue);
                    }
                }
            }
        }
        return randomIDList;
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        try (FiveSecondsCursorWrapper cursorWrapper = queryQuestionsWithNumbersOfUsage(null, null)) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                Question tempQuestion = cursorWrapper.getQuestion();
                //tempQuestion.setNumberOfUses(getNumberOfUsage(tempQuestion));
                questions.add(tempQuestion);
                cursorWrapper.moveToNext();
            }
        }
        return questions;
    }

//    public void addQuestion(Question question) {
//        ContentValues values = QuestionList.getContentValues(question);
//        database.insert(QuestionsTable.NAME, null, values);
//    }
//
//    public void updateQuestion(Question question) {
//        String uuidString = question.getId().toString();
//        ContentValues contentValues = QuestionList.getContentValues(question);
//        database.update(QuestionsTable.NAME, contentValues, QuestionsTable.Cols.UUID + " = ?", new String[] {uuidString});
//    }
//
//    public void deleteQuestion(Question question) {
//        String uuidString = question.getId().toString();
//        database.delete(QuestionsTable.NAME, QuestionsTable.Cols.UUID + " = ?", new String[] {uuidString});
//    }

    private int getNumberOfUsage(Question question) {
        SQLiteDatabase database = fiveSecondsBaseHelper.getReadableDatabase();
        String[] whereArgs = new String[] {question.getId().toString()};

        Cursor cursor = database.query(
                FiveSecondsDBSchema.QuestionsUsageTable.NAME,
                new String[] {FiveSecondsDBSchema.QuestionsUsageTable.Cols.COUNT},
                "uuid = ?",
                whereArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return 0;
    }

    public void increaseNumberOfUsage(String uuid) {
        SQLiteDatabase database = fiveSecondsBaseHelper.getReadableDatabase();
        Question temp = getQuestion(uuid);
        int count = getNumberOfUsage(temp);
        count++;
        ContentValues values = new ContentValues();
        values.put(FiveSecondsDBSchema.QuestionsUsageTable.Cols.UUID, uuid);
        values.put(FiveSecondsDBSchema.QuestionsUsageTable.Cols.COUNT, count);
        database.replace(FiveSecondsDBSchema.QuestionsUsageTable.NAME, null, values);
    }

    public Question getQuestion(String id){
        try (FiveSecondsCursorWrapper cursor = queryQuestions(QuestionsTable.Cols.UUID + " = ?", new String[]{id})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getQuestion();
        }
    }
}
