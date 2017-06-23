package example.lushijuan.thetraveldiary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.helpers.LocatorImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteDiaryActivity extends AppCompatActivity {
    private ImageView datePickerImv, share;
    private TextView date, weekday, longitude, latitude;
    private EditText weather, title, content;
    private Button save, cancel, insertImg;
    private Calendar calendar = Calendar.getInstance();
    private DiaryService diaryService;
    private LocationManager locationManager;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_diary);
        findView();  // 获取view
        getLonLat();  // 获取当前位置的经纬度
        calendar.setTime(new Date(System.currentTimeMillis()));
        date.setText(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH));
        weekday.setText(WeekTransform(calendar.get(Calendar.DAY_OF_WEEK) - 1));
        // 分享和日期选择设置监听函数
        share.setOnClickListener(new ShareText());
        datePickerImv.setOnClickListener(new dataPick());

        diaryService = new DiaryService(WriteDiaryActivity.this);

        // 如果是更新要显示旧日记
        final Intent intent2 = this.getIntent();
        final String msg = this.getIntent().getStringExtra("button");
        if(msg.equals("update")) {
            final Bundle bundle = intent2.getExtras();
            Integer idOfDiary = bundle.getInt("id");
            Diary diarySeleted = diaryService.find(idOfDiary);
            if (bundle != null) {
                title.setText(diarySeleted.getTitle());
                content.setText(diarySeleted.getContent());
                contentTransform(diarySeleted.getContent());
                longitude.setText(diarySeleted.getLongitude());
                latitude.setText(diarySeleted.getLatitude());
                date.setText(diarySeleted.getDate());
                weekday.setText(diarySeleted.getWeekday());
                weather.setText(diarySeleted.getWeekday());
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealSaveButton(intent2, msg);
            }
        });
        cancel.setOnClickListener(new cancelBtnListener());  // 取消按钮的监听事件
        insertImg.setOnClickListener(new insertImgListener());  // 插入图片按钮的监听事件
    }

    public void contentTransform(String contentOriginal) {
        String newContent = "<html><head></head><body>"+content+"</body></html>";
        content.setText(Html.fromHtml(newContent, imageGetter, null));
    }

    public Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            Drawable drawable = null;

            drawable = Drawable.createFromPath(source); //显示本地图片
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                    .getIntrinsicHeight());
            return drawable;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == 1) {
            ContentResolver resolver = getContentResolver();
            // 获得图片的uri
            Uri originalUri = intent.getData();
            Bitmap bitmap = null;
            try {
                Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                bitmap = Bitmap.createScaledBitmap(originalBitmap, 500, 650, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                //String path = originalUri.getPath();
                String path = getPath(getApplicationContext(), originalUri);
                ImageSpan span = new ImageSpan(this, bitmap);
                String tempUrl = "<img src=\"" + path + "\" />";
                SpannableString ss = new SpannableString(tempUrl);
                ss.setSpan(span, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                Editable et = content.getText();
                int start = content.getSelectionStart();
                et.insert(start, ss);
                content.setText(et);
                content.setSelection(start + ss.length());
            } else {
                Toast.makeText(WriteDiaryActivity.this, "获取图片失败", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void findView() {
        datePickerImv = (ImageView) findViewById(R.id.dataPickerImv);
        share = (ImageView) findViewById(R.id.share);
        date = (TextView) findViewById(R.id.date);
        weekday = (TextView) findViewById(R.id.weekday);
        weather = (EditText) findViewById(R.id.weather);
        title = (EditText) findViewById(R.id.diaryTitle);
        content = (EditText) findViewById(R.id.diaryContent);
        longitude = (TextView) findViewById(R.id.diaryLongitude);
        latitude = (TextView) findViewById(R.id.diaryLatitude);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        insertImg = (Button) findViewById(R.id.insertImg);
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    // 插入图片按钮的监听事件
    public class insertImgListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT <19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }

    // 设置日历选择的监听事件
    public class dataPick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    date.setText(year + "/" + (month+1) + "/" + dayOfMonth);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    weekday.setText(WeekTransform(calendar.get(Calendar.DAY_OF_WEEK)));
                }
            };
            Dialog dialog = new DatePickerDialog(WriteDiaryActivity.this, dateListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
    }

    // 保存按钮事件的处理
    public void dealSaveButton(Intent intent, String msg) {
        String cover_path = "";
        Intent intent1 = new Intent("com.example.chenya.Receiver");
        Bundle bundle1 = new Bundle();
        intent1.putExtra("bundle", bundle1);
        bundle1.putString("title", title.getText().toString());
        bundle1.putString("date", date.getText().toString());
        if (msg.equals("insert")) {  // 新建日记
            cover_path = findCoverPath();
            Log.i("cover_path_final", cover_path);
            Diary diary = new Diary(title.getText().toString(), content.getText().toString(),
                    longitude.getText().toString(), latitude.getText().toString(),
                    date.getText().toString(), weekday.getText().toString(), weather.getText().toString(), cover_path);
            bundle1.putInt("id", diary.getId());
            diaryService = new DiaryService(WriteDiaryActivity.this);
            diaryService.save(diary);
        } else if (msg.equals("update")) {  // 更新日记
            final Bundle bundle = intent.getExtras();
            cover_path = findCoverPath();
            Diary diary = new Diary(title.getText().toString(), content.getText().toString(),
                    longitude.getText().toString(), latitude.getText().toString(),
                    date.getText().toString(), weekday.getText().toString(), weather.getText().toString(), cover_path);
            diary.setId(bundle.getInt("id"));
            bundle1.putInt("id", diary.getId());
            diaryService = new DiaryService(WriteDiaryActivity.this);
            diaryService.update(diary);
        }
        bundle1.putString("uri", cover_path);
        sendBroadcast(intent1);

        Intent intent2 = new Intent(WriteDiaryActivity.this, MainActivity.class);
        startActivity(intent2);
    }

    public String findCoverPath() {
        String Reg ="<img\\s*([\\w]+=(\"|\')([^\"\']*)(\"|\')\\s*)*/>";
        String content_ = content.getText().toString();
        Pattern p = Pattern.compile(Reg);
        Matcher m = p.matcher(content_);
        boolean isFirst = true;
        String cover_path="";
        while (m.find()) {
            if (isFirst)
                cover_path = m.group(1);
            isFirst = false;
        }
        if (!cover_path.isEmpty()) {
            int len = cover_path.length();
            cover_path = cover_path.substring(5, len-2);
        }
        return cover_path;
    }

    // 取消按钮时间的处理
    public class cancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(WriteDiaryActivity.this, MainActivity.class));
            finish();
        }
    }

    //分享文字至所有第三方软件
    public class ShareText implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            String str = "#" + title.getText().toString() + "#\n" + content.getText().toString();
            intent.putExtra(Intent.EXTRA_TEXT, str);
            intent.setType("text/plain");
            //设置分享列表的标题，并且每次都显示分享列表
            startActivity(Intent.createChooser(intent, "分享到"));
        }
    }

    // 位置变化的监听
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };

    public void showLocation(Location location) {
        longitude.setText("经度："+location.getLongitude());
        latitude.setText("纬度："+location.getLatitude());
    }

    // 获取当前位置的经纬度
    public void getLonLat() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//无海拔要求
        criteria.setBearingRequired(false);//无方位要求
        criteria.setCostAllowed(true);//允许产生资费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        // 获取最佳服务对象
        locationProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            showLocation(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
    }

    public String WeekTransform(int i) {
        String str = "";
        switch (i) {
            case 1:
                str = "周日";
                break;
            case 2:
                str = "周一";
                break;
            case 3:
                str = "周二";
                break;
            case 4:
                str = "周三";
                break;
            case 5:
                str = "周四";
                break;
            case 6:
                str = "周五";
                break;
            case 7:
                str = "周六";
                break;
            default:
                break;
        }
        return str;
    }
}
