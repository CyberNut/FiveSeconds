package ru.cybernut.fiveseconds.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.cybernut.fiveseconds.FiveSecondsApplication;

public class FiveSecondsBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "FiveSecondsBaseHelper";
    private static String DB_PATH;
    private static FiveSecondsBaseHelper instance;
    private static final int VERSION = 2;
    private static final String DB_NAME = "five_seconds.db";

    private SQLiteDatabase mDataBase;
    private static boolean mNeedUpdate = false;

    public static synchronized FiveSecondsBaseHelper getInstance() {
        if(instance == null) {
            instance = new FiveSecondsBaseHelper(FiveSecondsApplication.getAppContext());
        }
        return instance;
    }

    private FiveSecondsBaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        copyDataBase();

        //to force onUpdate
        mDataBase = this.getReadableDatabase();
        if (mNeedUpdate) {
            updateDataBase();
        }
    }

    public void updateDataBase() {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();
            copyDataBase();
            mNeedUpdate = false;
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = FiveSecondsApplication.getAppContext().getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[4096];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  {
        if (newVersion > oldVersion) {
            mNeedUpdate = true;
        }
    }
}

