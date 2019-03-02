package ru.cybernut.fiveseconds.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.Sound;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.*;

public class FiveSecondsCursorWrapper extends CursorWrapper {

    public FiveSecondsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Question getQuestion() {
        String uuidString = getString(getColumnIndex(QuestionsTable.Cols.UUID));
        String title = getString(getColumnIndex(QuestionsTable.Cols.QUESTION_TEXT));

        Question question = new Question(UUID.fromString(uuidString));
        question.setText(title);
        return question;
    }

    public QuestionSet getQuestionSet() {
        String name = getString(getColumnIndex(QuestionSetsTable.Cols.NAME));
        String type = getString(getColumnIndex(QuestionSetsTable.Cols.TYPE));

        QuestionSet questionSet = new QuestionSet(name, type);
        return questionSet;
    }

    public Sound getSound() {
        String path = getString(getColumnIndex(SoundsTable.Cols.FILE_PATH));
        String questionUUIDString = getString(getColumnIndex(SoundsTable.Cols.QUESTION_UUID));

        Sound sound = new Sound(path, UUID.fromString(questionUUIDString));
        return sound;
    }
}

