package example.lushijuan.thetraveldiary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 此Demo用来说明点聚合功能
 */
public class MyMap extends Activity implements OnMapLoadedCallback {

    TextureMapView mMapView;
    BaiduMap mBaiduMap;
    MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;
    private SQLiteDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.my_map);


        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        List<MyItem> items = addMarkers();

        LatLng latLng = items.get(items.size()-1).getPosition();
        if (latLng.latitude == 0)
            ms = new MapStatus.Builder().target(new LatLng(23, 113)).zoom(8).build();
        else ms = new MapStatus.Builder().target(latLng).zoom(8).build();
        float f = mMapView.getMap().getMaxZoomLevel();

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, f);

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
//        mBaiduMap.setMapStatus(mMapStatusUpdate);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

        mBaiduMap.animateMapStatus(u);
         // 定义点聚合管理类ClusterManager


        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        mClusterManager.addItems(items);
        // 添加Marker点
        addMarkers();
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(MyMap.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(MyMap.this, MainActivity.class);
                startActivity(intent);

                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                Toast.makeText(MyMap.this,
                        "点击单个Item", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("button", "update");
                intent.setClass(MyMap.this, WriteDiaryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());
                intent.putExtras(bundle);
                startActivity(intent);

                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }


    /**
     * 向地图添加Marker点
     */
    public List<MyItem> addMarkers() {
        // 添加Marker点
        DiaryService ds = new DiaryService(MyMap.this);
        Cursor cursor = ds.getAllDiaries();
        List<MyItem> items = new ArrayList<MyItem>();
        LatLng latLng = new LatLng(0, 0);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("mId"));
                double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("latitude")).split("：")[1]);
//                Log.v("double", latitude+"");
                double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitude")).split("：")[1]);
                 latLng = functions.convertLocation(latitude, longitude);
                int x = cursor.getColumnIndex("cover");
                Bitmap bitmap;
                String y = cursor.getString(x);
                if (!y.equals(""))
                    bitmap = functions.generateThumnailFromPicture(y, 100, 100);
                else
                {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.defaultmappic);

                }
                MyItem item = new MyItem(latLng, bitmap, id);
                items.add(item);
            }while (cursor.moveToNext());
        }


        return items;
    }


    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final Bitmap bitmap;
        private final int id;

        public MyItem(LatLng latLng, Bitmap b, int mId) {
            mPosition = latLng;
            bitmap = b;
            id = mId;
        }


        public LatLng getPosition() {
            return mPosition;
        }


        public int getId() {
            return id;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory
                    .fromBitmap(bitmap);
        }
    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(9).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

}
