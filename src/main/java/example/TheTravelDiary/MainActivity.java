package example.lushijuan.thetraveldiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.E;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final DiaryService dbService = new DiaryService(this);
    private Button stepButton, mapButton, picButton, writeButton;
    private ListView listView;
    final List<Diary> data = new ArrayList<>();
    final MyAdapter adapter = new MyAdapter(data, MainActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        onClickListener();
        refreshListView();

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("button", "insert");
                intent.setClass(MainActivity.this, WriteDiaryActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("button", "update");
                intent.setClass(MainActivity.this, WriteDiaryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", data.get(position).getId());
                Log.i("id in main", data.get(position).getId()+"");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("删除联系人").create();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            int pos;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                alert.setMessage("确定删除这篇日记?").setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.remove(pos);
                                adapter.notifyDataSetChanged();
                                dbService.delete(data.get(pos).getId());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            }
        });


    }
    private void refreshListView() {
        DiaryService service = new DiaryService(this);
        Cursor cursor = service.getAllDiaries();
        cursor.moveToLast();
        data.clear();
        try{
            Diary temp = new Diary(cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            temp.setId(cursor.getInt(1));
            data.add(temp);
        }catch (Exception e) {

        }
        while(cursor.moveToPrevious()) {
            Diary temp1 = new Diary(cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            temp1.setId(cursor.getInt(1));
            data.add(temp1);
        }
        listView = (ListView) findViewById(R.id.diaryList);
        listView.setAdapter(adapter);
        cursor.close();
    }

    public void findViews() {
        writeButton = (Button)findViewById(R.id.writeDiaryButton);
        stepButton = (Button)findViewById(R.id.stepButton);
        mapButton = (Button)findViewById(R.id.mapButton);
        picButton = (Button)findViewById(R.id.picButton);
    }

    public void onClickListener() {
        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MyStepCount.class);
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MyMap.class);
                startActivity(intent);
            }
        });

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MyPicture.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }
}
