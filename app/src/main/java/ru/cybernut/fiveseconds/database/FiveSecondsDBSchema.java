package ru.cybernut.fiveseconds.database;

import ru.cybernut.fiveseconds.FiveSecondsApplication;

public class FiveSecondsDBSchema {

    public static final class QuestionsTable {

        public static final String NAME =  "questions";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String QUESTION_TEXT = FiveSecondsApplication.getLanguage() +"_text";
            public static final String SET_ID = "question_set_id";
        }
    }

    public static final class QuestionSetsTable {

        public static final String NAME = "question_sets";

        public static final class Cols {
            public static final String NAME = FiveSecondsApplication.getLanguage() +"_name";
            public static final String TYPE = "type";
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
