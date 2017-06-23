package example.lushijuan.thetraveldiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chenya on 2016/12/5.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "diary.db";
    private static final String TABLE_NAME_diary = "diary";
    private static final String TABLE_NAME_steprecord= "StepRecord";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME_diary
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, mId INTEGER, title TEXT, content TEXT, "
                + "longitude TEXT, latitude TEXT, date TEXT, weekday TEXt, weather TEXt, cover TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        String CREATE_TABLE1 = "CREATE TABLE if not exists "+ TABLE_NAME_steprecord +"(_id INTEGER PRIMARY KEY, date TEXT, step TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
