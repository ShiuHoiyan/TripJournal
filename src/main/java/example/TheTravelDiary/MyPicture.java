package example.lushijuan.thetraveldiary;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MyPicture extends AppCompatActivity {

    private Button  done, pickColor;
    private ImageView image;
    private String path;
    private View outPic;
    private Handler mHandler;
    private Runnable mRunnable;
    private ColorPickerView colorPickerDisk=null;
    private AlertDialog.Builder builder;
    private View view1;
    private Dialog dialog;
    private Uri uri;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_picture);

        findViews();

        newThread();

        onClickListener();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("result", resultCode+"");
        if (resultCode != 0) {
        if (requestCode == 2) {
            Uri uri = data.getData();
            Toast.makeText(this, uri+"", Toast.LENGTH_SHORT).show();
            if (uri != null) {
                path = getRealFilePath(this, uri);
                Log.v("path", path);
                Bitmap photo = functions.generateThumnailFromPicture(path, 0, 0);
                image.setImageBitmap(photo);

            }
            //to do find the path of pic by uri
            Log.v("uri", uri.toString());
            if (uri == null) {
                //use bundle to get data
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                    //spath :生成图片取个名字和路径包含类型
                    image.setImageBitmap(photo);

                } else {
                    Toast.makeText(getApplicationContext(), "err****", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            super.onActivityResult(requestCode, resultCode, data);


        }
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://example.lushijuan.thetraveldiary/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://example.lushijuan.thetraveldiary/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void findViews() {
        image = (ImageView) findViewById(R.id.image);
        outPic = findViewById(R.id.outPic);
        done = (Button)findViewById(R.id.done);
        pickColor = (Button)findViewById(R.id.pickColor);
        uri = null;
    }

    public void onClickListener() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(mRunnable);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, 2);
            }
        });

        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater factory = LayoutInflater.from(MyPicture.this);
                view1 = factory.inflate(R.layout.dialog_layout, null);
                builder = new AlertDialog.Builder(MyPicture.this);
                builder.setView(view1);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog = builder.create();
                colorPickerDisk=(ColorPickerView)view1.findViewById(R.id.colorPickerDisk);
                colorPickerDisk.setOnColorChangedListennerD(new ColorPickerView.OnColorChangedListenerD() {

                    @Override
                    public void onColorChanged(int color, String hexStrColor) {
                        // TODO Auto-generated method stub
//                        Drawable drawable = getDrawable(R.drawable.out);
                        outPic.setBackgroundColor(color);

                    }
                });


                dialog.show();
            }
        });

    }


    void newThread() {
        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                outPic.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                outPic.layout(0, 0, outPic.getMeasuredWidth(), outPic.getMeasuredHeight());
                outPic.buildDrawingCache();
//                outPic.buildDrawingCache();
                Bitmap bitmap;
                try {
                    bitmap = outPic.getDrawingCache();
                    uri = functions.saveImage(MyPicture.this, bitmap);
                    Intent intent = new Intent("TheStringUsedAsStaticFilter");
                    intent.putExtra("uri",uri.toString());
                    sendBroadcast(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };
    }

}
