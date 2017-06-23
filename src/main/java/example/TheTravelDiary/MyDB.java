package example.lushijuan.thetraveldiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chen on 2016/11/15.
 */
public class MyDB extends SQLiteOpenHelper{
    private static final String DB_NAME = "diary.db";
    private  static  final String TABLE_NAME = "StepRecord";
    private static final int DB_VERSION = 1;
    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                int version) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("in", "create");
        String CREATE_TABLE = "CREATE TABLE if not exists "+ TABLE_NAME +"(_id INTEGER PRIMARY KEY, date TEXT, step TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il){}

    public void update2DB(MyDB mydb, String date, String step) {
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("step", step);
        String whereClause = "date=\'"+date+"\'";
        db.update("StepRecord", cv, whereClause, null);
        db.close();
    }
    public void delete2DB(MyDB mydb, String date) {
        SQLiteDatabase db = mydb.getWritableDatabase();
        db.delete("StepRecord", "date=\'"+date+"\'",null);
        db.close();
    }
    public void insert2DB(MyDB mydb, String date, String step) {
        SQLiteDatabase db = mydb.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("step", step);
        db.insert("StepRecord", null, cv);
        db.close();
    }

}
