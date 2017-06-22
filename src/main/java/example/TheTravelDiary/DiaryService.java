package example.lushijuan.thetraveldiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by chenya on 2016/12/5.
 */

public class DiaryService {
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;

    public DiaryService(Context context) {
        dbHelper = new DBHelper(context);
    }

    // 保存日记
    public void save(Diary diary) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "insert into diary(mId,title,content,longitude,latitude,date,weekday,weather,cover) values(?,?,?,?,?,?,?,?,?)";
        sqLiteDatabase.execSQL(sql, new Object[] { diary.getId(), diary.getTitle(), diary.getContent(), diary.getLongitude(),
                diary.getLatitude(), diary.getDate(), diary.getWeekday(), diary.getWeather(), diary.getCover()});
    }

    // 更新日记
    public void update(Diary diary){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "update diary set title=?,content=?,longitude=?,latitude=?,date=?,weekDay=?,weather=?, cover=? where mId=?";
        sqLiteDatabase.execSQL(sql, new Object[] { diary.getTitle(), diary.getContent(), diary.getLongitude(),
                diary.getLatitude(), diary.getDate(), diary.getWeekday(), diary.getWeather(), diary.getCover(), diary.getId()});
    }

    // 根据ID删除日记
    public void delete(Integer id) {
        sqLiteDatabase = dbHelper.getWritableDatabase();// 得到的是同一个数据库实例
        sqLiteDatabase.execSQL("delete from diary where mId=?",new Object[]{id});
    }

    // 获取所有日记
    public Cursor getAllDiaries(){
        sqLiteDatabase=dbHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from diary", null);
        return cursor;
    }
    public Diary find(Integer id) {
        Diary diary = null;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        // 得到游标，最多只有一条数据
        Cursor cursor = sqLiteDatabase.rawQuery(
                "select * from diary where mId=?",
                new String[] { id.toString() });
        // 如果移动成功就代表存在
        if (cursor.moveToFirst()) {
            // 只能根据列的索引来获得相应的字段值
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String weekday = cursor.getString(cursor.getColumnIndex("weekday"));
            String weather = cursor.getString(cursor.getColumnIndex("weather"));
            String cover = cursor.getString(cursor.getColumnIndex("cover"));
            diary = new Diary(title, content, longitude, latitude, date, weekday, weather, cover);
        }
        return diary;
    }

}
