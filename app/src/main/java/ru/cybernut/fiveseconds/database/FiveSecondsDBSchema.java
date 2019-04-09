package ru.cybernut.fiveseconds.database;

import android.util.Log;

import ru.cybernut.fiveseconds.FiveSecondsApplication;

import static ru.cybernut.fiveseconds.FiveSecondsApplication.TAG;

public class FiveSecondsDBSchema {

    private static String language;
    static {
        Log.i(TAG, "static initializer: schema");
         language = FiveSecondsApplication.getLanguage();
    }

    public static final class QuestionsTable {

        public static final String NAME =  "questions";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String QUESTION_TEXT = language +"_text";
            public static final String SET_ID = "question_set_id";
        }
    }

    public static final class QuestionSetsTable {

        public static final String NAME = "question_sets";

        public static final class Cols {
            public static final String NAME = language +"_name";
            public static final String TYPE = "type";
            public static final String SOUNDS_LINK = language +"_sounds_link";
            public static final String SOUNDS_LOADED = language +"_sounds_loaded";
        }
    }

    public static final class SoundsTable {

        public static final String NAME = "sounds";

        public static final class Cols {
            public static final String QUESTION_UUID = "question_uuid";
            public static final String FILE_PATH = "file_path";
        }

    }
}
