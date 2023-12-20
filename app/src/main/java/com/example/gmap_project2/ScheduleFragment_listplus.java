package com.example.gmap_project2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gmap_project2.ui.ScheduleFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleFragment_listplus extends AppCompatActivity {
    ScheduleFragment fragment3 = new ScheduleFragment();

    ListView classlistviewplus, classlistview; //추가된 강의 목록, 조회한 강의목록
    RadioButton major, culture; //전공, 교양
    Spinner divisionsp1,divisionsp2, gradesp, mainpointsp; //교양이수구분, 전공이수구분, 학년, 분반, 핵심영역
    Button varsity,department;//대학, 학과
    ImageButton  plus_btn, delete_btn, back_btn; //추가, 삭제, 뒤로가기
    RadioGroup rgroup;
    int dtchoice;
    SQLiteDatabase sqlDB, plusDB, my_Class_DB;
    DBlistHelper listshowHelper, listplusHelper, my_class_Helper;
    Integer Class_num;
    String Time_check, Division, Class_name,delete_Calss_num, delete_Division, delete_Time;
    SearchView search;

    String[] divisionlist1= {"전체 영역","기초교양", "핵심영역","일반교양","교직","자유선택"};
    String[] divisionlist2= {"전체 영역","전공필수","전공선택","전공"};
    String[] mainpointlist= {"핵심1영역","핵심2영역","핵심3영역","핵심4영역"};
    String[] gradelist= {"전체 학년","1학년","2학년","3학년","4학년"};
    String[] varsitylsit={"인문대학","예술대학","사회과학대학","자연과학대학","공과대학","해양과학대학","산학융합공과대학","공유/융합/연계전공"};
    String[] departmentlist1={"국어국문학과","영어영문학과","미디어문화학과","동아시아학부","역사철학부"};
    String[] departmentlist2={"미술학과","산업디자인학과","음악과"};
    String[] departmentlist3={"행정경제학부","법학과","경영학과","회계학과","무역학과","사회복지학과","국제물류학과"};
    String[] departmentlist4={"수학과","물리학과","화학과","생명과학과","생물학과","아동가족학과","의류학과","간호학과","체육학과"};
    String[] departmentlist5={"전자공학과","전기공학과","토목공학과","환경공학과","신소재공학과","화학공학과","컴퓨터정보통신공학부","IT정보제어공학부","조선해양공학과"};
    String[] departmentlist6={"해양경찰학과","수산생명의학과","해양생물자원학과","해양생명과학과","식품생명과학부","해양생산","마린"};
    String[] departmentlist7={"기계공학부","건축해양건설융합공학부","공간디자인융합기술학과","융합기술창업학과","소프트웨어학과"};
    String[] departmentlist8={"글로벌재경전공","공공세무전공","연금관리전공","빅데이터공학전공","디지털포렌식전공","주거복지연계전공","기타"};

    String mc, va, de, gr, di; //전공교양, 대학, 학과, 학년, 영역
    int c; //처음에 query문 실행안되게 하는거
    String search_Class_name;


    //추가된 요일 시간///
    ArrayList<Integer> monday = new ArrayList<Integer>();
    ArrayList<Integer> tuesday = new ArrayList<Integer>();
    ArrayList<Integer> wednesday = new ArrayList<Integer>();
    ArrayList<Integer> thursday = new ArrayList<Integer>();
    ArrayList<Integer> friday = new ArrayList<Integer>();

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        c=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplus);
        gradesp = (Spinner) findViewById(R.id.gradeSpin);
        divisionsp1 = (Spinner) findViewById(R.id.divisionSpin1);//교양 영역
        divisionsp2 = (Spinner) findViewById(R.id.divisionSpin2); //전공 영역
        mainpointsp=(Spinner)findViewById(R.id.main_PointSpin);//핵심 영역
        major=(RadioButton) findViewById(R.id.Major);//전공
        culture=(RadioButton) findViewById(R.id.Culture);//교양
        classlistviewplus=(ListView) findViewById(R.id.classListViewplus); //담은 과목 리스트
        classlistview=(ListView) findViewById(R.id.classListView); //검색 과목 리스트
        varsity=(Button)findViewById(R.id.Varsity); //대학
        department=(Button)findViewById(R.id.Department); //학과
        rgroup=(RadioGroup)findViewById(R.id.Rgroup);
        plus_btn= (ImageButton) findViewById(R.id.plus_Btn); //리스트 추가 버튼
        delete_btn= (ImageButton) findViewById(R.id.delete_Btn); //리스트 제거 버튼
        back_btn= (ImageButton) findViewById(R.id.back_Btn);
        search=(SearchView)findViewById(R.id.Search);


        Map<Integer, String[]> dtht=new HashMap<Integer, String[]>();
        dtht.put(0,departmentlist1);
        dtht.put(1,departmentlist2);
        dtht.put(2,departmentlist3);
        dtht.put(3,departmentlist4);
        dtht.put(4,departmentlist5);
        dtht.put(5,departmentlist6);
        dtht.put(6,departmentlist7);
        dtht.put(7,departmentlist8);
        search_Class_name=null;




        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {//검색후 버튼을 눌러야 실행
                search_Class_name=s;
                listshow();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {//검색창의 적는 내용을 실시간으로 반응
                search_Class_name=s;
                listshow();
                return false;
            }
        });


        mc="전공"; //처음 시작할때 오류 방지
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.Major){
                    varsity.setVisibility(View.VISIBLE);
                    divisionsp1.setVisibility(View.GONE);
                    divisionsp2.setVisibility(View.VISIBLE);
                    mainpointsp.setVisibility(View.GONE);
                    mc="전공";
                    de=null;
                    di="'전공필수', '전공선택', '전공'";
                    gr="1,2,3,4";
                    listshow();

                }
                else{
                    varsity.setVisibility(View.GONE);
                    department.setVisibility(View.GONE);
                    divisionsp1.setVisibility(View.VISIBLE);
                    divisionsp2.setVisibility(View.GONE);
                    mc="교양";
                    de=null;
                    department.setText("학과");
                    varsity.setText("대학");
                    di="'기초교양', '핵심1영역','핵심2영역','핵심3영역','핵심4영역', '일반교양', '교직', '자유선택'";
                    gr="1,2,3,4";
                    listshow();

                }
            }
        });
        monday.add(100);
        tuesday.add(100);
        wednesday.add(100);
        thursday.add(100);
        friday.add(100);
        for(int i=10;i<14;i++){
            monday.add(i);
            tuesday.add(i);
            wednesday.add(i);
            thursday.add(i);
            friday.add(i);
        }

        varsity.setOnClickListener(new View.OnClickListener() { //대학 선택 버튼
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleFragment_listplus.this);
                dlg.setTitle("대학 선택");
                dlg.setSingleChoiceItems(varsitylsit, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        department.setVisibility(View.VISIBLE);
                        varsity.setText(varsitylsit[i]);
                        department.setText("학과");
                        dtchoice=i;
                        va=varsitylsit[i];
                    }
                });
                dlg.setPositiveButton("확인",null);
                dlg.show();
            }
        });

        department.setOnClickListener(new View.OnClickListener() { //학과 선택 버튼
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleFragment_listplus.this);
                dlg.setTitle("학과 선택");
                dlg.setSingleChoiceItems(dtht.get(dtchoice), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        department.setVisibility(View.VISIBLE);
                        department.setText(dtht.get(dtchoice)[i]);
                        de=dtht.get(dtchoice)[i];
                        listshow();
                    }
                });
                dlg.setPositiveButton("확인",null);
                dlg.show();
            }
        });

        ArrayAdapter<String> gradapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gradelist);
        gradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradesp.setAdapter(gradapter);
        gradesp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //학년 스피너
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==1){
                    gr="1";
                    listshow();}
                else if(i==2){
                    gr="2";
                    listshow();}
                else if(i==3){
                    gr="3";
                    listshow();}
                else if (i==4){
                    gr="4";
                    listshow();}
                else if(i==0){
                    gr="1,2,3,4";
                    listshow();}

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> divadapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, divisionlist1);
        divadapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionsp1.setAdapter(divadapter1);
        divisionsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { //교양 이수구분 스피너
                if(i==0){
                    di="'기초교양', '핵심1영역','핵심2영역','핵심3영역','핵심4영역', '일반교양', '교직', '자유선택'";
                    mainpointsp.setVisibility(View.GONE);
                }
                else if(i==2){
                    di="'핵심1영역','핵심2영역','핵심3영역','핵심4영역'";
                    mainpointsp.setVisibility(View.VISIBLE);
                }
                else {
                    di="'"+divisionlist1[i]+"'";
                    mainpointsp.setVisibility(View.GONE);
                }
                listshow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> mainpointadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mainpointlist);
        mainpointadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainpointsp.setAdapter(mainpointadapter);
        mainpointsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //핵심교양 스피너
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                di="'"+mainpointlist[i]+"'";
                System.out.println(di);
                listshow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<String> divadapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, divisionlist2);
        divadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionsp2.setAdapter(divadapter2);
        divisionsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //전공 이수구분 스피너
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    di="'전공필수', '전공선택', '전공'";
                }
                else{
                    di="'"+divisionlist2[i]+"'";
                }
                listshow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        plus_btn.setOnClickListener(new View.OnClickListener() {//추가 버튼
            @Override
            public void onClick(View view) {
                boolean a=time_overlap_check();
                if(a) {
                    my_list_plus();
                    my_class_show();
                }
                else{
                    Toast tmsg=Toast.makeText(ScheduleFragment_listplus.this,"시간이 중복됩니다.", Toast.LENGTH_SHORT);
                    tmsg.show();
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {//삭제 버튼
            @Override
            public void onClick(View view) {
                my_list_delete();
                my_class_show();
            }
        });
        Intent inIntent = getIntent();
        back_btn.setOnClickListener(new View.OnClickListener() { //뒤로가기 버튼
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class) ;
                intent.putExtra("state","from_listplus");
                startActivity(intent);
            }
        });




    }

    public void my_class_show() {//내 강의 리스트 보여주기
        my_class_Helper = new DBlistHelper(this);
        my_Class_DB = my_class_Helper.getReadableDatabase();
        Cursor cursor3;
        cursor3 = sqlDB.rawQuery("SELECT * FROM My_Timetable", null);

        ArrayList<String> my_class_time = new ArrayList<String>();//내 강의 시간
        ArrayList<String> my_class_num = new ArrayList<String>();//내 강의 학수번호
        ArrayList<String> my_class_division = new ArrayList<String>();//내 강의 분반
        HashMap<String, String> my_class;
        ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String, String>>();
        while (cursor3.moveToNext()) {
            my_class_time.add(cursor3.getString(3));
            my_class_num.add(cursor3.getString(0));
            my_class_division.add(cursor3.getString(2));

            my_class= new HashMap<String, String>();
            my_class.put("item1", cursor3.getString(1));//과목명
            my_class.put("item2", cursor3.getString(2) + "분반\t" + cursor3.getString(3));//분반과 시간 표시
            mylist.add(my_class);
        }
        SimpleAdapter my_class_adbapter  = new SimpleAdapter(this,mylist,android.R.layout.simple_list_item_2,new String[] {"item1","item2"},new int[] {android.R.id.text1, android.R.id.text2});
        classlistviewplus.setAdapter(my_class_adbapter);

        for(int i=0;i<my_class_time.size();i++){
            Time_check=my_class_time.get(i);
            boolean c=time_overlap_check();
        }

        cursor3.close();
        my_Class_DB.close();

        classlistviewplus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete_btn.setVisibility(View.VISIBLE);
                delete_Calss_num=my_class_num.get(i); //삭제할 학수번호
                delete_Division=my_class_division.get(i); //삭제할 분반
                delete_Time=my_class_time.get(i); //삭제할 시간
            }
        });
    }

    public void my_list_plus() {//내 강의 목록 강의 추가하기
        listplusHelper = new DBlistHelper(this);
        plusDB = listplusHelper.getReadableDatabase();
        plusDB.execSQL("INSERT INTO My_Timetable VALUES ("
                +Class_num+",'"
                +Class_name+"','"
                +Division+"','"
                +Time_check+"');");
        plusDB.close();
        Toast tmsg=Toast.makeText(ScheduleFragment_listplus.this,"추가되었습니다.",Toast.LENGTH_SHORT);
        tmsg.show();
    }
    public void my_list_delete(){//내 강의 목록 강의 제거하기
        listplusHelper = new DBlistHelper(this);
        plusDB = listplusHelper.getReadableDatabase();
        plusDB.execSQL("DELETE FROM My_Timetable WHERE 분반 = '" + delete_Division + "' and 학수번호 = "+delete_Calss_num+" ;");
        plusDB.close();

        String day_week = null;//요일명
        String[] day= delete_Time.split(","); //한개 이상의 요일을 저장
        for(int i=0;i< day.length;i++){
            String[] hour = day[i].split(" ");
            day_week=hour[0].substring(0,1);
            hour[0]=hour[0].substring(1,2);
            for(int j=0;j<hour.length;j++) {
                if(hour[j].length()<2){
                    if(day_week.equals("월")){
                        monday.remove(Integer.valueOf(hour[j]));
                    }
                    else if(day_week.equals("화")){
                        tuesday.remove(Integer.valueOf(hour[j]));
                    }
                    else if(day_week.equals("수")){
                        wednesday.remove(Integer.valueOf(hour[j]));
                    }
                    else if(day_week.equals("목")){
                        thursday.remove(Integer.valueOf(hour[j]));
                    }
                    else if(day_week.equals("금")){
                        friday.remove(Integer.valueOf(hour[j]));
                    }
                }
            }
        }
        Toast tmsg=Toast.makeText(ScheduleFragment_listplus.this,"제거되었습니다.",Toast.LENGTH_SHORT);
        tmsg.show();
    }

    public void listshow(){ //검색 강의목록 보여주기
        listshowHelper = new DBlistHelper(this);
        sqlDB=listshowHelper.getReadableDatabase();
        my_class_show();
        Cursor cursor =null;
        String query;
        System.out.println(c);

        if(c!=0) {
            if (search_Class_name=="" || search_Class_name==null){
                if (mc.equals("전공")) {
                    if (de == null) {
                        query = "SELECT * FROM Time_table WHERE 학년 in (" + gr + ") and 이수구분 in (" + di + ") ";
                    }else{
                        query = "SELECT * FROM Time_table WHERE 전공 = '" + de + "' and 학년 in (" + gr + ") and 이수구분 in (" + di + ") and 교전 = '" + mc + "'";
                    }
                } else {
                    query = "SELECT * FROM Time_table WHERE 이수구분 in (" + di + ") and 교전 = '" + mc + "'";
                }
            }
            else {
                if (mc.equals("전공")) {
                    if (de == null) {
                        query = "SELECT * FROM Time_table WHERE 학년 in (" + gr + ") and 이수구분 in (" + di + ")  and 교과목명 like '" + search_Class_name + "%' ";
                    } else {
                        query = "SELECT * FROM Time_table WHERE 전공 = '" + de + "' and 학년 in (" + gr + ") and 이수구분 in (" + di + ") and 교과목명 like '" + search_Class_name + "%'  and 교전 = '" + mc + "'";
                    }
                }
                else{
                    query = "SELECT * FROM Time_table WHERE 이수구분 in (" + di + ") and 교과목명 like '" + search_Class_name + "%'  and 교전 = '" + mc + "'";
                }
            }

            System.out.println(query);
            cursor = sqlDB.rawQuery(query, null);


            ArrayList<String> class_Name = new ArrayList<String>();//교과목명
            ArrayList<String> class_Num = new ArrayList<String>();//학수번호
            ArrayList<String> time = new ArrayList<String>();//시간표
            ArrayList<String> division = new ArrayList<String>();//분반
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String,String> item;

            while (cursor.moveToNext()) {
                class_Name.add(cursor.getString(3));
                class_Num.add(cursor.getString(2));
                time.add(cursor.getString(12));
                division.add(cursor.getString(4));

                item = new HashMap<String, String>();
                item.put("item1",cursor.getString(3));
                item.put("item2",cursor.getString(4)+"분반\t"+cursor.getString(11)+"\n"+cursor.getString(12));
                list.add(item);
            }


            SimpleAdapter dbadpter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,new String[] {"item1","item2"},new int[] {android.R.id.text1, android.R.id.text2});
            classlistview.setAdapter(dbadpter);

            classlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Class_num= Integer.valueOf(class_Num.get(i));//학수번호
                    Class_name=class_Name.get(i);//강의명
                    Time_check=time.get(i);//시간표
                    Division=division.get(i);//분반
                    plus_btn.setVisibility(View.VISIBLE);
                }
            });
        }
        c=+1;
//        sqlDB.close();
//        cursor.close();
    }

    public boolean time_overlap_check(){
        String day_week = null;//요일명
        String[] day= Time_check.split(","); //한개 이상의 요일을 저장
        for(int i=0;i<day.length;i++){
            String[] hour = day[i].split(" ");
            day_week=hour[0].substring(0,1);
            hour[0]=hour[0].substring(1,2);
//            System.out.println(day_week);
            for(int j=0;j<hour.length;j++){
                if(day_week.equals("월")){
                    for(int k=0;k<monday.size();k++) {
                        if (monday.get(k) == (Integer.valueOf(hour[j]))) {
                            return false;
                        }
                    }
                    monday.add(Integer.valueOf(hour[j]));
                }
                else if(day_week.equals("화")) {
                    for (int k = 0; k < tuesday.size(); k++) {
                        if (tuesday.get(k) == Integer.valueOf(hour[j])) {
                            return false;
                        }
                    }
                    tuesday.add(Integer.valueOf(hour[j]));
                }
                else if(day_week.equals("수")){
                    for(int k=0;k<wednesday.size();k++) {
                        if (wednesday.get(k) == Integer.valueOf(hour[j])) {
                            return false;
                        }
                    }
                    wednesday.add(Integer.valueOf(hour[j]));
                }
                else if(day_week.equals("목")){
                    for(int k=0;k<thursday.size();k++) {
                        if (thursday.get(k) == Integer.valueOf(hour[j])) {
                            return false;
                        }
                    }
                    thursday.add(Integer.valueOf(hour[j]));
                }
                else if(day_week.equals("금")){
                    for(int k=0;k<friday.size();k++) {
                        if (friday.get(k) == Integer.valueOf(hour[j])) {
                            return false;
                        }
                    }
                    friday.add(Integer.valueOf(hour[j]));
                }
            }
        }
        return true;
    }

    public class DBlistHelper extends SQLiteOpenHelper {
        public DBlistHelper(Context context) {
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