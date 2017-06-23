package example.lushijuan.thetraveldiary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by lushijuan on 16/12/7.
 */
public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("TheStringUsedAsStaticFilter")) {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.yes);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("点击查看")
                    .setSmallIcon(R.mipmap.yes)
                    .setLargeIcon(bm)
                    .setAutoCancel(true);

            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("uri")));
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
            builder.setContentIntent(mPendingIntent);

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }
}

