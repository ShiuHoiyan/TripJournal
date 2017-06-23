package example.lushijuan.thetraveldiary;

import android.content.Intent;
import android.support.annotation.IntegerRes;

import java.util.Random;

/**
 * Created by chenya on 2016/12/5.
 */

public class Diary {
    private Integer mId;
    private String title;
    private String content;
    private String longitude;
    private String latitude;
    private String date;
    private String weekday;
    private String weather;
    private String cover;

    public Diary(String t, String c, String l1, String l2, String d, String w1, String w2, String c_) {
        Random random = new Random();
        title = t;
        content = c;
        longitude = l1;
        latitude = l2;
        date = d;
        weekday = w1;
        weather = w2;
        cover = c_;
        mId = random.nextInt();
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id_) {
        mId = id_;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDate() {
        return date;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getWeather() {
        return weather;
    }

    public String getCover() {
        return cover;
    }
}
