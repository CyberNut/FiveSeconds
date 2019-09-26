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
        String name = getString(getColumnIndex(ru.cybernut.fiveseconds.database.FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME));
        String type = getString(getColumnIndex(QuestionSetsTable.Cols.TYPE));
        String shopItemId = getString(getColumnIndex(QuestionSetsTable.Cols.SHOP_ITEM_ID));
        Integer owned = getInt(getColumnIndex(QuestionSetsTable.Cols.OWNED));
        String link = getString(getColumnIndex(QuestionSetsTable.Cols.SOUNDS_LINK));
        int soundsLoaded = getInt(getColumnIndex(QuestionSetsTable.Cols.SOUNDS_LOADED));

        return new QuestionSet(name, type, link, soundsLoaded > 0 ? true : false, shopItemId, owned);
    }

    public Sound getSound() {
        String path = getString(getColumnIndex(SoundsTable.Cols.FILE_PATH));
        String questionUUIDString = getString(getColumnIndex(SoundsTable.Cols.QUESTION_UUID));

        Sound sound = new Sound(path, UUID.fromString(questionUUIDString));
        return sound;
    }
}

