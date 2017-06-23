package example.lushijuan.thetraveldiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chen on 2016/10/12.
 */

public class MyStepCountAdapter extends BaseAdapter {
    private List<StepCount> data;
    private Context context;

    public MyStepCountAdapter(List<StepCount> data, Context context) {
        this.data = data;
        this.context = context;
    }
    public void setItemList(List list) {
        data = list;
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
        v = LayoutInflater.from(context).inflate(R.layout.item, null);
        TextView date = (TextView)v.findViewById(R.id.listDate);
        TextView step = (TextView) v.findViewById(R.id.listStepCount);
        date.setText(data.get(i).date);
        step.setText(data.get(i).step);
        return v;
    }
}
