package com.vtec.j1tth4.vtecpos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by j1tth4 on 4/29/15.
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "vtecpos.db";

    private String mDatabasePath;

    private static DatabaseHelper sInstance = null;

    public static synchronized DatabaseHelper getInstance(Context c){
        if(sInstance == null){
            sInstance = new DatabaseHelper(c.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);

        mDatabasePath = Utils.getVtecAssetPath() + File.separator +
                VtecPosApplication.VTEC_DATABASE_DIR + File.separator + DATABASE_NAME;

        if(!isDataBaseExist()){
            try {
                copyDatabase(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyDatabase(Context c) throws IOException {
        InputStream mInput = c.getAssets().open(DATABASE_NAME);
        OutputStream mOutput = new FileOutputStream(mDatabasePath);
        byte[] mBuffer = new byte[1024];
        int mLength;
        try {
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
        }finally {
            if(mOutput != null) {
                mOutput.flush();
                mOutput.close();
            }
            if(mInput != null) {
                mInput.close();
            }
        }
    }

    private boolean isDataBaseExist() {
        File vtecAssetPath = Utils.getVtecAssetPath();
        File dbPath = new File(vtecAssetPath + File.separator + VtecPosApplication.VTEC_DATABASE_DIR);
        if (!dbPath.exists())
            dbPath.mkdir();
        File dbFile = new File(dbPath + File.separator + DATABASE_NAME);
        return dbFile.exists();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(mDatabasePath, null,
                SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(mDatabasePath, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }
}
