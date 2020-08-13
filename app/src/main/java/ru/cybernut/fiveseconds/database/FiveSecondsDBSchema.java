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
            public static final String SHOP_ITEM_ID = "shop_item_id";
            public static final String OWNED = "owned";
            public static final String SOUNDS_LINK = FiveSecondsApplication.getLanguage() +"_sounds_link";
            public static final String SOUNDS_LOADED = FiveSecondsApplication.getLanguage() +"_sounds_loaded";
            public static final String SOUNDS_FILES_SIZE = FiveSecondsApplication.getLanguage() +"_sounds_files_size";
        }
    }

    public static final class QuestionsUsageTable {

        public static final String NAME =  "questions_usage";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String COUNT = "count";
        }
    }


}
