package com.example.gmap_project2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    TextView title, opt1, opt2, opt3;
    LinearLayout lay1, lay2, lay3;
    LinearLayout lv_list1, lv_list2, lv_list3;
    Button btn_find;
    Cursor cursor;

    HashMap<String, String> table_info = new HashMap<String,String>();
    HashMap<String, String> col_info = new HashMap<String,String>();
    HashMap<String, String> sort_info = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        String res = intent.getStringExtra("res0");
        String opt = intent.getStringExtra("opt");

        table_info.put("건물", "Building_table");
        table_info.put("호수", "Building_table");
        table_info.put("교수", "Time_table");
        table_info.put("과목", "Time_table");
        col_info.put("건물", "건물이름");
        col_info.put("호수", "호실");
        col_info.put("교수", "담당교수");
        col_info.put("과목", "교과목명");
        sort_info.put("건물", "ORDER BY 건물이름");
        sort_info.put("호수", "ORDER BY 건물이름, 호실");
        sort_info.put("교수", "ORDER BY 담당교수");
        sort_info.put("과목", "ORDER BY 전공, 교과목명, 분반");

        title = (TextView) findViewById(R.id.tv_title);
        opt1 = (TextView) findViewById(R.id.tv_opt1);
        opt2 = (TextView) findViewById(R.id.tv_opt2);
        opt3 = (TextView) findViewById(R.id.tv_opt3);
        List<TextView> opts = new ArrayList<TextView>();
        opts.add(opt1); opts.add(opt2); opts.add(opt3);
        lay1 = (LinearLayout) findViewById(R.id.ll_lay1);
        lay2 = (LinearLayout) findViewById(R.id.ll_lay2);
        lay3 = (LinearLayout) findViewById(R.id.ll_lay3);
        List<LinearLayout> lays = new ArrayList<LinearLayout>();
        lays.add(lay1); lays.add(lay2); lays.add(lay3);
        lv_list1 = (LinearLayout) findViewById(R.id.layout_search_result_info_1);
        lv_list2 = (LinearLayout) findViewById(R.id.layout_search_result_info_2);
        lv_list3 = (LinearLayout) findViewById(R.id.layout_search_result_info_3);
        List<LinearLayout> lvs = new ArrayList<LinearLayout>();
        lvs.add(lv_list1); lvs.add(lv_list2); lvs.add(lv_list3);
        btn_find = (Button) findViewById(R.id.btn_findLoad);
        title.setText(res);

        List<String> infos = new ArrayList<String>();

        btn_find.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class) ;
                intent.putExtra("state","goto_map");
                intent.putExtra("arrival",res);
                startActivity(intent);
            }
        });

        switch (opt){
            case "건물":{infos.add("호수");infos.add("과목");break;}
            case "호수":{infos.add("건물");infos.add("과목");break;}
            case "교수":{infos.add("과목");break;}
            case "과목":{infos.add("호수");infos.add("교수");break;}
        }

        int i = 0;
        String Q = null;
        String Q1 = null;
        String Q2 = null;
        String building_1 = null;
        String building_2 = null;
        String time = null;
        for(String info : infos){
            Boolean is_lec_to_room = false;
            switch (opt){
                case "건물":{
                    btn_find.setEnabled(true);
                    switch (info){
                        // 이 건물에 있는 모든 방의 이름과 넘버
                        case "호수":{
                            String sort = sort_info.get("호수");
                            Q = "SELECT 호실, 분류 FROM Building_table WHERE 건물이름 = \""+res+"\" "+sort; break;}
                        // 이 건물에서 진행되는 모든 과목들
                        case "과목":{
                            String sort = sort_info.get("과목");
                            Q = "SELECT 교과목명, 전공, 분반 FROM Time_table WHERE 건물1 = \""+res+"\" OR 건물2 = \""+res+"\" "+sort; break;}
                    }
                    break;
                }
                case "호수":{
                    btn_find.setEnabled(false);
                    String room_num = intent.getStringExtra("res2");
                    switch (info){
                        // 이 방이 위치한 건물
                        case "건물":{Q = "SELECT DISTINCT 건물이름 FROM Building_table WHERE 호실 = \""+res+"\" AND 분류 = \""+room_num+"\""; break;}
                        // 이 방에서 진행되는 과목
                        case "과목":{
                            String sort = sort_info.get("과목");
                            Q = "SELECT 교과목명, 전공, 분반 FROM Time_table WHERE 강의실1 = "+room_num+" OR 강의실2 = "+room_num+" "+sort; break;
                        }
                    }
                    break;
                }
                case "교수":{
                    switch (info){
                        case "과목":{
                            String sort = sort_info.get("과목");
                            Q = "SELECT 교과목명, 전공, 분반 FROM Time_table WHERE 담당교수 = \""+res+"\" "+sort;break;
                        }
                    }
                    break;
                }
                case "과목":{
                    String major = intent.getStringExtra("res1");
                    String sp_room_num = intent.getStringExtra("res2");
                    switch (info){
                        case "호수":{
                            is_lec_to_room=true;
                            String lec_1 = "(SELECT 강의실1 FROM Time_table WHERE 교과목명 = \""+res+"\" AND 전공 = \""+major+"\" AND 분반 = \""+sp_room_num+"\")";
                            String lec_2 = "(SELECT 강의실2 FROM Time_table WHERE 교과목명 = \""+res+"\" AND 전공 = \""+major+"\" AND 분반 = \""+sp_room_num+"\")";
                            building_1 = "SELECT 건물1 FROM Time_table WHERE 교과목명 = \""+res+"\" AND 전공 = \""+major+"\" AND 분반 = \""+sp_room_num+"\"";
                            building_2 = "SELECT 건물2 FROM Time_table WHERE 교과목명 = \""+res+"\" AND 전공 = \""+major+"\" AND 분반 = \""+sp_room_num+"\"";
                            time = "SELECT 시간 FROM Time_table WHERE 교과목명 = \""+res+"\" AND 전공 = \""+major+"\" AND 분반 = \""+sp_room_num+"\"";
                            Q1 = "SELECT 건물이름, 층수, 분류, 호실 FROM Building_table WHERE 분류 = "+lec_1;
                            Q2 = "SELECT 건물이름, 층수, 분류, 호실 FROM Building_table WHERE 분류 = "+lec_2;
                            break;
                        }
                        case "교수":{Q = "SELECT DISTINCT 담당교수 FROM Time_table WHERE 교과목명 = \""+res+"\"";break;}
                    }
                    break;
                }
            }
            List<List<String>> items = new ArrayList<List<String>>();
            if(is_lec_to_room){
                String q_res1, q_res2, time_res, final_res;
                List<String> result = new ArrayList<String>();
                try {
                    List<List<String>> tmp = search(Q1);
                    q_res1 = tmp.get(0).get(0)+" "+tmp.get(0).get(1)+"층 "+tmp.get(0).get(2)+" "+tmp.get(0).get(3);
                }catch (IndexOutOfBoundsException e){
                    q_res1 = search(building_1).get(0).get(0);
                }
                try {
                    List<List<String>> tmp = search(Q2);
                    q_res2 = tmp.get(0).get(0)+" "+tmp.get(0).get(1)+"층 "+tmp.get(0).get(2)+" "+tmp.get(0).get(3);
                }catch (IndexOutOfBoundsException e){
                    q_res2 = search(building_2).get(0).get(0);
                }
                time_res = search(time).get(0).get(0);
                if(q_res1.equals(q_res2)){
                    final_res = time_res + "/" + q_res1 +"/none";
                }else{
                    final_res = time_res + "/" + q_res1+ "/" + q_res2;
                }
                result.add(final_res);
                items.add(result);

                lays.get(i).setVisibility(View.VISIBLE);
                opts.get(i).setText(info);
                make_result_list_lec(items, lvs.get(i));
            }else{
                items = search(Q);
                lays.get(i).setVisibility(View.VISIBLE);
                opts.get(i).setText(info);
                make_result_list(items, lvs.get(i));
            }
            i++;
        }


    }

    public List<List<String>> search(String query) {

        DataBaseHelper dbHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<List<String>> result = new ArrayList<>();


        cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            List<String> l = new ArrayList<String>();
            for(int i=0;i<cursor.getColumnCount();i++){
                l.add(cursor.getString(i));
            }
            result.add(l);
        }

        cursor.close();
        dbHelper.close();
        return result;
    }

    public LinearLayout make_linear_layout_lec(List<String> res, int id){
        LinearLayout lay = new LinearLayout(this);
        TextView tv_time = new TextView(this);
        TextView tv_room1 = new TextView(this);
        TextView tv_room2 = new TextView(this);
        String[] print_result = res.get(0).split("/");
        String time = print_result[0];
        String room1 = print_result[1];
        String room2 = print_result[2];

        tv_time.setText(time);
        tv_time.setTextSize(20);
        tv_time.setTextColor(Color.rgb(0,0,0));

        tv_room1.setText(room1);
        tv_room1.setTextSize(20);
        tv_room1.setTextColor(Color.rgb(0,0,0));

        tv_room2.setText(room2);
        tv_room2.setTextSize(20);
        tv_room2.setTextColor(Color.rgb(0,0,0));

        lay.setId(id);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setGravity(Gravity.CENTER_VERTICAL);
        lay.setBackgroundResource(R.drawable.list_info_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lay.setLayoutParams(params);

        lay.addView(tv_time);
        lay.addView(tv_room1);
        if(!room2.equals("none")){ lay.addView(tv_room2); }
        return lay;
    }

    public void make_result_list_lec(List<List<String>> result, LinearLayout layout_search_result){
        layout_search_result.removeAllViews();
        int id = 0;
        for(List<String> res : result){
            layout_search_result.addView(make_linear_layout_lec(res, id));
            id+=1;
        }
    }

    public LinearLayout make_linear_layout(List<String> res, int id){
        LinearLayout lay = new LinearLayout(this);
        lay.setId(id);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setBackgroundColor(Color.rgb(234, 243, 255)); // #EAF3FF
        lay.setPadding(30,0,0,0);
        lay.setGravity(Gravity.CENTER);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setBackgroundResource(R.drawable.list_info_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200);
        lay.setLayoutParams(params);

        LinearLayout.LayoutParams t_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t_params.setMargins(30,0,30,0);
        for(int i =0 ; i<res.size() ; i++){
            TextView tv = new TextView(this);
            String set_t = res.get(i);
            if(set_t != null && !set_t.equals("교양") && set_t.length()<=2){
                set_t = set_t+" 분반";
            }
            tv.setText(set_t);
            tv.setLayoutParams(t_params);
            tv.setTextSize(20);
            tv.setTextColor(Color.rgb(0,0,0));
            lay.addView(tv);
        }
        return lay;
    }

    public void make_result_list(List<List<String>> result, LinearLayout layout_search_result){
        layout_search_result.removeAllViews();
        int id = 0;
        for(List<String> res : result){
            layout_search_result.addView(make_linear_layout(res, id));
            id+=1;
        }
    }
}
