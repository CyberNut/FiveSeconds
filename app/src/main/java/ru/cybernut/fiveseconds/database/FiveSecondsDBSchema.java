package ru.cybernut.fiveseconds.database;

import java.util.Arrays;
import java.util.Locale;

public class FiveSecondsDBSchema {

    private static final String defaultLanguage = "en";
    private static final String[] supportLanguages = new String[] {"en", "ru", "es"};

    private static String getLocale() {
        String currentLanguage = Locale.getDefault().getLanguage();
        if( Arrays.binarySearch(supportLanguages, currentLanguage) > 0) {
            return currentLanguage;
        } else {
            return defaultLanguage;
        }
    }

    public static final class QuestionsTable {

        public static final String NAME = getLocale() + "_questions";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String QUESTION_TEXT = "text";
            public static final String SET_ID = "question_set_id";
        }
    }

    public static final class QuestionSetsTable {

        public static final String NAME = "question_sets";

        public static final class Cols {
            public static final String NAME = "name";
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
