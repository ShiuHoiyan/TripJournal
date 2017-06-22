package example.lushijuan.thetraveldiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chen on 2016/10/12.
 */

public class MyAdapter extends BaseAdapter {
    private List<Diary> data;
    private Context context;

    public MyAdapter(List<Diary> data, Context context) {
        this.data = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        if(data==null)
            return 0;
        return data.size();
    }
    @Override
    public Object getItem(int i) {
        if(data==null)
            return null;
        return data.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup vg) {
        v = LayoutInflater.from(context).inflate(R.layout.diarylist_item, null);
        ImageView photoPlace = (ImageView)v.findViewById(R.id.picture);
        TextView titlePlace = (TextView)v.findViewById(R.id.title);
        TextView datePlace = (TextView)v.findViewById(R.id.date);
        Bitmap picture = functions.generateThumnailFromPicture(data.get(i).getCover(), 80, 80);
        photoPlace.setImageBitmap(picture);
        titlePlace.setText(data.get(i).getTitle());
        datePlace.setText(data.get(i).getDate());
        return v;
    }
}
