package example.lushijuan.thetraveldiary;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by lushijuan on 16/12/3.
 */
public class functions {

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
        if (outWidth/width > outHeight/height)
            op.inSampleSize = outHeight/height;
        else op.inSampleSize = outWidth/width;
        op.inJustDecodeBounds = false;
        Bitmap bitmap =  BitmapFactory.decodeFile(pathName, op);
        if (bitmap != null)
            return Bitmap.createBitmap(bitmap, 0, 0, width, height);
        else return bitmap;


    }




    static public Uri saveImage(Context context, Bitmap bmp) {
        Uri uri = null;
                    try {

                        ContentResolver cr = context.getContentResolver();

                        String url = MediaStore.Images.Media.insertImage(cr, bmp, "", "");
                        Log.v("url1", url);
                        uri  = Uri.parse(url);
                        Log.v("uri", uri.toString());
//
                        Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
//
                        if (!TextUtils.isEmpty(url)) {
                            String filePath = getFilePathByContentResolver(context, uri);
                            scanFile(context,filePath);



                        }

                    }catch(Exception e){

                        e.printStackTrace();
                        Toast.makeText(context, "can't save the image, it is to big", Toast.LENGTH_SHORT).show();

                    }
        return uri;


    }
    public static void scanFile(Context mContext,String path) {
        MediaScannerConnection.scanFile(mContext, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    static public void SharePhoto(Context context, Bitmap bmp) {
        ContentResolver cr = context.getContentResolver();
        String url = MediaStore.Images.Media.insertImage(cr, bmp, "", "");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(url);
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, file);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "share"));
    }

    static private  String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath  = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    static public LatLng convertLocation(double latitude, double longitude) {
        CoordinateConverter mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(new LatLng(latitude, longitude));
        return mConverter.convert();
    }

    }
