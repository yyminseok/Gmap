package com.example.gmap_project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gmap_project2.ui.HomeFragment;
import com.example.gmap_project2.ui.MapFragment;
import com.example.gmap_project2.ui.ScheduleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment fragment1;
    MapFragment fragment2;
    ScheduleFragment fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent inIntent = getIntent();
        String state = inIntent.getStringExtra("state");

        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //프래그먼트 생성
        fragment1 = new HomeFragment();
        fragment2 = new MapFragment();
        fragment3 = new ScheduleFragment();

        //제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commitAllowingStateLoss();

        if(state !=null && state.equals("from_listplus")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment3).commitAllowingStateLoss();
        }else if(state !=null && state.equals("goto_map")){
            Bundle bundle = new Bundle();
            bundle.putString("arrival",inIntent.getStringExtra("arrival"));
            fragment2.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment2).commitAllowingStateLoss();
        }


        //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                    case R.id.tab1:{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,fragment1).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab2:{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,fragment2).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab3:{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,fragment3).commitAllowingStateLoss();
                        return true;
                    }
                    default: return false;
                }

            }

        });

    }
}