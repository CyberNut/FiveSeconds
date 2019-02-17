package ru.cybernut.fiveseconds.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ru.cybernut.fiveseconds.Question;

import static ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.*;

public class FiveSecondsCursorWrapper extends CursorWrapper {

    public FiveSecondsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Question getQuestion() {
        String uuidString = getString(getColumnIndex(FSTable.Cols.UUID));
        String title = getString(getColumnIndex(FSTable.Cols.QUESTION_TEXT));

        Question question = new Question(UUID.fromString(uuidString));
        return question;
    }
}

