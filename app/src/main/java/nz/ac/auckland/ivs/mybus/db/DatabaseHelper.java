package nz.ac.auckland.ivs.mybus.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Luke X. Chang on 19/09/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private SQLiteDatabase mDb;
    private static final String DB_SOURCE = "/assets/";
    private static String DB_PATH;

    public DatabaseHelper(Context context) {
        super(context, DbTableContract.DB_NAME, null, DbTableContract.DB_VERSION);
        this.mContext = context;
        String filesDir = mContext.getFilesDir().getPath();
        DB_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        if (mDb != null) {
            mDb.close();
        }
        super.close();
    }

    public void createDatabase() throws IOException {
        boolean isDbExist = checkDatabase();

        if (!isDbExist) {
            System.out.println("Database does not exist yet!");
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                System.err.println("Error cannot copy database! " + e.getMessage());
            }
        } else {
            System.out.println("Database exists.");
        }
    }

    private boolean checkDatabase() {
//        SQLiteDatabase db = null;
//        try {
//            db = SQLiteDatabase.openDatabase(DB_PATH + DbTableContract.DB_NAME,
//                    null, SQLiteDatabase.OPEN_READONLY);
//            db.close();
//        } catch (SQLiteException e) {
//        }
//
//        return (db != null);

        File dbFile = new File(DB_PATH + DbTableContract.DB_NAME);
        return (dbFile.exists());
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = mContext.getAssets().open(DbTableContract.DB_NAME + ".db");
        OutputStream outputStream = new FileOutputStream(DB_PATH + DbTableContract.DB_NAME);
        System.out.println("Starts writing database!");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void openDatabase() throws SQLiteException {
        mDb = SQLiteDatabase.openDatabase(DB_PATH + DbTableContract.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
//        System.out.println("Database is opened!");
    }

    public long getProfilesCount() {
        String countQuery = "SELECT  * FROM " + DbTableContract.BusTableStops.DB_TABLE_NAME;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DbTableContract.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        long cnt = DatabaseUtils.queryNumEntries(db, DbTableContract.BusTableStops.DB_TABLE_NAME);
        db.close();

//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//
//        if (c.moveToFirst()) {
//            while ( !c.isAfterLast() ) {
//                System.out.println("Table Name=> "+c.getString(0));
//                c.moveToNext();
//            }
//        }
//        c.close();
        return cnt;
    }
}
