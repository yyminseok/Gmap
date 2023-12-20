package com.example.gmap_project2.ui;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gmap_project2.InfoActivity;
import com.example.gmap_project2.R;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {
    ViewGroup viewGroup;

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "l7xxff44cbdb12ac4b9e9a5527b38bec484f"; // 발급받은 appKey
    private static int mMarkerID;
    TMapPolyLine route;
    double time;
    double route_distance;
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>(); // Marker 넣을 리스트
    ArrayList<String> marker_la = new ArrayList<String>();
    ArrayList<String> marker_lo = new ArrayList<String>();
    ArrayList<String> marker_name = new ArrayList<String>();
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    String start_latitude, start_longitude, end_latitude, end_longitude, start_building, end_building;
    Location gps_location = null;
    boolean check_find_route = false;
    boolean check_gps = true; //현재 위치에서 목적지까지 찾을 시 true
    boolean map_init = true;
    boolean start_path_find = false;
    boolean is_start_here = false;
    boolean is_add_point = false;
    TextView building_name, direction_info;
    Button start_btn,end_btn,btn_info;
    LinearLayout direction_layout, information_layout, directioninfo_layout;
    ImageButton ok_btn,direction_btn;
    String st, en;
    int check, st_int, end_int;
    String[] building_list = new String[62];
    String arrival = null;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        mContext = container.getContext();
        LinearLayout linearLayout = (LinearLayout) viewGroup.findViewById(R.id.mapview);
        tmapview = new TMapView(container.getContext());

        tmapview.setSKTMapApiKey(mApiKey);
        map_init=true;

        addPoint();
        showMarkerPoint();
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        building_name=(TextView) viewGroup.findViewById(R.id.building_Name);
        start_btn=(Button) viewGroup.findViewById(R.id.start_Btn);
        end_btn=(Button) viewGroup.findViewById(R.id.end_Btn);
        btn_info=(Button) viewGroup.findViewById(R.id.btn_info);
        direction_btn=(ImageButton) viewGroup.findViewById(R.id.direction_Btn);
        direction_layout=(LinearLayout) viewGroup.findViewById(R.id.direction_Layout);
        directioninfo_layout=(LinearLayout) viewGroup.findViewById(R.id.directioninfo_Layout);
        information_layout=(LinearLayout) viewGroup.findViewById(R.id.information_Layout);
        ok_btn=(ImageButton) viewGroup.findViewById(R.id.ok_Btn);
        direction_info=(TextView) viewGroup.findViewById(R.id.direction_txt);
        check=0;

        direction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check==0) {
                    direction_layout.setVisibility(VISIBLE);
                    check=1;
                }
                else{
                    direction_layout.setVisibility(View.GONE);
                    check=0;
                }
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(container.getContext());
                dlg.setTitle("출발 건물 선택");
                dlg.setSingleChoiceItems(building_list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        st=building_list[which];
                        st_int=which-1;
                        start_btn.setText(st);
                    }
                });
                dlg.setPositiveButton("확인",null);
                dlg.show();
            }
        });

        end_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(container.getContext());
                dlg.setTitle("도착 건물 선택");
                dlg.setSingleChoiceItems(building_list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        en=building_list[which];
                        end_int=which-1;
                        end_btn.setText(en);
                    }
                });
                dlg.setPositiveButton("확인",null);
                dlg.show();
            }
        });
        try {
            arrival = this.getArguments().getString("arrival");
            direction_btn.performClick();
            end_btn.setText(arrival);
            end_int = Arrays.asList(building_list).indexOf(arrival)-1;
            en = arrival;
        }catch (NullPointerException e){}

        ok_btn.setOnClickListener(new View.OnClickListener() { //길찾기 시작 버튼
            @Override
            public void onClick(View v) {
                try {
                    find_Route_DB(st, en);
                    start_path_find = true;
                    is_start_here = false;
                    if (st == null || en == null) {
                        Toast tmsg = Toast.makeText(getContext(), "출발지와 도착지를 설정하세요", Toast.LENGTH_SHORT);
                        tmsg.show();
                    } else {
                        if (st.equals(en)) {
                            Toast tmsg = Toast.makeText(getContext(), "출발지와 도착지가 중복됩니다", Toast.LENGTH_SHORT);
                            tmsg.show();
                        }

                        else {
                            if (st.equals("현위치")) {
                                is_start_here = true;
                                drawMapPath(gps_location.getLatitude(), gps_location.getLongitude(), Double.valueOf(marker_la.get(end_int)), Double.valueOf(marker_lo.get(end_int)));
                            }
                            else if (en.equals("현위치")) {
                                drawMapPath(Double.valueOf(marker_la.get(st_int)), Double.valueOf(marker_lo.get(st_int)), gps_location.getLatitude(), gps_location.getLongitude());
                            }
                            else {
                                drawMapPath(Double.valueOf(marker_la.get(st_int)), Double.valueOf(marker_lo.get(st_int)), Double.valueOf(marker_la.get(end_int)), Double.valueOf(marker_lo.get(end_int)));
                            }
                            if (route != null) {
                                route_distance = route.getDistance();
                                double speed = (1000 / 12);
                                time = route_distance / speed;
                                directioninfo_layout.setVisibility(VISIBLE);
                                direction_info.setText("거리 : " + String.valueOf(Math.round(route.getDistance())) + "m" + "\t 도보시간 : " + String.valueOf(Math.round(time)) + "분");
                            }
                            is_start_here = false;
                        }
                    }
                }catch(NullPointerException e){}
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoActivity.class);

                String res = building_name.getText().toString();
                String opt = "건물";
                intent.putExtra("res0", res);
                intent.putExtra("opt", opt);
                startActivity(intent);
            }
        });
        /* 현재 보는 방향 */
        tmapview.setCompassMode(true);
        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);
        /* 줌레벨 */
        tmapview.setZoomLevel(17);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

//        tmapgps = new TMapGpsManager(container.getContext());
//        tmapgps.setMinTime(1000);
//        tmapgps.setMinDistance(5);
//        //tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
//        //실내일 때 유용합니다.
//        tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.
//        tmapgps.OpenGps();

        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

                try {
                    TMapMarkerItem a = arrayList.get(0);
                    System.out.println(a.getCalloutTitle());
                    information_layout.setVisibility(VISIBLE);
                    building_name.setText(a.getCalloutTitle());

                }catch(IndexOutOfBoundsException e){

                }
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });

        // 풍선에서 우측 버튼 클릭시 할 행동입니다
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Toast.makeText(getContext(), "클릭", Toast.LENGTH_SHORT).show();
            }
        });

        if (isGPSEnabled) {
            Log.e("GPS Enable", "true");

            final List<String> m_lstProviders = locationManager.getProviders(false);
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
                    tmapview.setCenterPoint(location.getLongitude(), location.getLatitude());

                    if(start_path_find && is_start_here){
                        try {
                            drawMapPath(location.getLatitude(), location.getLongitude(), Double.valueOf(marker_la.get(end_int)), Double.valueOf(marker_lo.get(end_int)));
                        }catch (NullPointerException e){}
                    }
                    if(map_init) {
                        tmapview.setCompassMode(true);
                        tmapview.setIconVisibility(true);
                        tmapview.setZoomLevel(16);
                        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);  //일반지도
                        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
                        tmapview.setTrackingMode(false);
                        tmapview.setSightVisible(false);
                        linearLayout.addView(tmapview);
                        map_init=false;
                    }
                    Log.e("onLocationChanged", "onLocationChanged");
                    Log.e("location", "[" + location.getProvider() + "] (" + location.getLatitude() + "," + location.getLongitude() + ")");
                    gps_location = location;
                    String data = location.getProvider();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.e("onStatusChanged", "onStatusChanged");
                }
                @Override
                public void onProviderEnabled(String provider) {
                    Log.e("onProviderEnabled", "onProviderEnabled");
                }
                @Override
                public void onProviderDisabled(String provider) {
                    Log.e("onProviderDisabled", "onProviderDisabled");
                }
            };

            // QQQ: 시간, 거리를 0 으로 설정하면 가급적 자주 위치 정보가 갱신되지만 베터리 소모가 많을 수 있다.

            getActivity().runOnUiThread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    for (String name : m_lstProviders) {
                        locationManager.requestLocationUpdates(name, 1000, 0, locationListener);

//                        if(check_find_route == true) {
//                            tmapview.removeTMapPath();
//                        }
//
//                        if(check_find_route && name.equals("gps")) {
//                            find_Route_DB("공과대학1호관","인문관");
//                            drawMapPath(gps_location.getLatitude(),gps_location.getLongitude());
//                        }
                    }
                }
            });

        } else {
            Log.e("GPS Enable", "false");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(getActivity().getCurrentFocus(), "앰버를 발견했습니다 GPS를 잠시 켜주시기발바니다.", Snackbar.LENGTH_INDEFINITE).setAction("GPS켜기", new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
                }
            });
        }
        return viewGroup;
    }
    public void drawMapPath(double s_latitude, double s_longtitude, double e_latitude, double e_longtitude) {
        TMapPoint start = new TMapPoint(s_latitude,s_longtitude);
        TMapPoint end = new TMapPoint(e_latitude,e_longtitude);

        TMapData tmapdata = new TMapData();
        tmapdata.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {

            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                route = polyLine;
                try{
                    tmapview.addTMapPath(route);
                }catch(NullPointerException e){}
            }
        });
    }

    public void addPoint() { //여기에 핀을 꼽을 포인트들을 배열에 add해주세요!
        marker_DB();
        int data_len= marker_la.size();
        m_mapPoint = new ArrayList<MapPoint>();
        for (int i=0; i<data_len; i++) {
            m_mapPoint.add(new MapPoint(marker_name.get(i), Double.valueOf(marker_la.get(i)),Double.valueOf(marker_lo.get(i))));
        }
    }

    public void showMarkerPoint() {// 마커 찍는 함수
        for (int i = 0; i < m_mapPoint.size(); i++) {
            TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
                    m_mapPoint.get(i).getLongitude());
            TMapMarkerItem item1 = new TMapMarkerItem();

            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mark);

            //poi_dot은 지도에 꼽을 빨간 핀 이미지입니다

            item1.setTMapPoint(point);
            item1.setName(m_mapPoint.get(i).getName());
            item1.setVisible(item1.VISIBLE);

            item1.setIcon(bitmap);

            // 풍선뷰 안의 항목에 글을 지정합니다.
            item1.setCalloutTitle(m_mapPoint.get(i).getName());
            item1.setCalloutSubTitle("군산대학교");
            item1.setCanShowCallout(true);  // 마커 클릭 시 풍선 띄우게 하는 함수
            item1.setAutoCalloutVisible(false); // 마커를 맵이켜지자마자 풍선이 보이게 할지의 여부
            item1.getCalloutRect();
            item1.getCalloutSubTitle();
            Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mark);

            item1.setCalloutRightButtonImage(bitmap_i);

            String strID = String.format("pmarker%d", mMarkerID++);

            tmapview.addMarkerItem(strID, item1);
            mArrayMarkerID.add(strID);
        }
    }
    public void marker_DB() {
        myHelper = new myDBHelper(getContext());
        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT distinct 건물이름,위도,경도 FROM Building_table ", null);
        marker_name = new ArrayList<String>(); // 수정
        marker_la = new ArrayList<String>();
        marker_lo = new ArrayList<String>();
        while(cursor.moveToNext()) {
            marker_name.add(cursor.getString(0));
            marker_la.add(cursor.getString(1));
            marker_lo.add(cursor.getString(2));
        }
        building_list[0]="현위치";
        for(int i=0;i<marker_name.size();i++){
            building_list[i+1]=marker_name.get(i);
        }
        cursor.close();
        myHelper.close();
    }
    public void find_Route_DB(String start_building, String end_building){//길찾기 경로 불러오기 check=0,1 (0 : gps, 1:건물간)
        myHelper = new myDBHelper(getContext());
        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor, cursor2;
        ArrayList<String> end_la = new ArrayList<String>(); //위도
        ArrayList<String> end_lo = new ArrayList<String>(); //경도
        ArrayList<String> start_la = new ArrayList<String>(); //위도
        ArrayList<String> start_lo = new ArrayList<String>(); //경도
        cursor = sqlDB.rawQuery("SELECT distinct * FROM Building_table WHERE 건물이름 = '"+end_building+"'", null);
        if(start_building!=null) {
            cursor2 = sqlDB.rawQuery("SELECT distinct * FROM Building_table WHERE 건물이름 = '"+start_building+"'", null);
            while(cursor2.moveToNext()){
                start_la.add(cursor2.getString(5));
                start_lo.add(cursor2.getString(6));
            }


            cursor2.close();
        }
        while(cursor.moveToNext()){
            end_la.add(cursor.getString(5));
            end_lo.add(cursor.getString(6));
        }
        cursor.close();
        myHelper.close();
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "timetable.db", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}