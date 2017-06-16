package example.lushijuan.thetraveldiary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by chen on 2016/10/26.
 */
public class Widget extends AppWidgetProvider {
    private int id;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent clickInt = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, clickInt, 0);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        rv.setOnClickPendingIntent(R.id.widget_image, pi);
        appWidgetManager.updateAppWidget(appWidgetIds, rv);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        Bundle bundle = intent.getBundleExtra("bundle");
        if(intent.getAction().equals("com.example.chenya.Receiver")) {
            String uri = bundle.getString("uri");
            String title = bundle.getString("title");
            String date = bundle.getString("date");
            id = bundle.getInt("id");
            //转uri
            Toast.makeText(context, uri+"", Toast.LENGTH_SHORT).show();
            Log.v("uri", uri.toString());
            Bitmap bitmap = generateThumnailFromPicture(uri, 80, 80);
            rv.setTextViewText(R.id.widget_title, title);
            rv.setTextViewText(R.id.widget_date, date);
            rv.setImageViewBitmap(R.id.widget_image, bitmap);
//            rv.setImageViewResource(R.id.widget_image, R.drawable.defaultpic);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context,Widget.class);
            appWidgetManager.updateAppWidget(componentName, rv);
        }
    }
    static public Bitmap generateThumnailFromPicture(String pathName, int width, int height) {

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, op);
        int outWidth = op.outWidth;
        int outHeight = op.outHeight;
        if (width == 0 || height == 0) {

            if (outWidth*outHeight > 1024*1024) {

//        width = 80;
//        height = 45;
                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (outWidth > outHeight && outWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (op.outWidth / ww);
                } else if (outWidth < outHeight && outHeight > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (op.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                op.inSampleSize = be;//设置缩放比例
                op.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(pathName, op);
            }
            else return BitmapFactory.decodeFile(pathName);
        }
        op.inSampleSize =(outWidth/width+outHeight/height)/2;
        op.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, op);

    }
}
