package example.lushijuan.thetraveldiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by chen on 2016/12/7.
 */

public class function {
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
