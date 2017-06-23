package example.lushijuan.thetraveldiary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyStepCount extends AppCompatActivity implements Handler.Callback{
    private ListView mListView;
    private TextView tTextView;
    private TextView uTextView;
    private Handler delayHandler;
    final MyDB mydb = new MyDB(this,"diary.db",null,1);
    private int isProcess = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_step_count);
        mListView = (ListView)findViewById(R.id.stepShow);
        tTextView = (TextView)findViewById(R.id.todayStep);
        uTextView = (TextView)findViewById(R.id.updateTime);
        if(StepCountModeDispatcher.isSupportStepCountSensor(this)) {
            delayHandler = new Handler(this);
            setupService();
        } else {
            tTextView.setText("您的手机不支持计步功能");
        }
    }

    private boolean isBind = false;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Messenger messenger;
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_SERVICE_TO_CLIENT:
                tTextView.setText(msg.getData().getInt("step")+"");
                uTextView.setText("最后更新时间/"+msg.getData().getString("time"));
                break;
        }
        return false;
    }

    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
        Log.i("setup", isBind+"service");
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_CLIENT_TO_SERVICE);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void updateListView() {
        Toast.makeText(this, "updateList", Toast.LENGTH_SHORT).show();
        List<StepCount> data = new ArrayList<StepCount>();
        final SQLiteDatabase db =  mydb.getWritableDatabase();
        final Cursor cursor = db.query("StepRecord", new String[]{"date", "step"}, null, null, null, null, null, null);
        cursor.moveToLast();
        while(cursor.moveToPrevious()){
            StepCount temp1 = new StepCount();
            temp1.date = cursor.getString(0);
            temp1.step = cursor.getString(1);
            data.add(temp1);
        }
        db.close();
        MyStepAdapter adapter = new MyStepAdapter(data, MyStepCount.this);
        mListView.setAdapter(adapter);
    }



}
