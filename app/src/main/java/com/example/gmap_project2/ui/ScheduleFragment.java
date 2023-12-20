package com.example.gmap_project2.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gmap_project2.R;
import com.example.gmap_project2.ScheduleFragment_listplus;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleFragment extends Fragment {
    myDBHelper myHelper, temaHelper;
    SQLiteDatabase sqlDB, temaDB;
    ViewGroup viewGroup;
    FloatingActionButton set, listplusbtn;

    Integer[]  forest_tema ={R.color.greenTema1,R.color.greenTema2,R.color.greenTema3,R.color.greenTema4,
            R.color.greenTema5,R.color.greenTema6,R.color.greenTema7,R.color.greenTema8};

    Integer[]  sea_tema ={R.color.blueTema1,R.color.blueTema2,R.color.blueTema3,R.color.blueTema4,
            R.color.blueTema5,R.color.blueTema6,R.color.blueTema7,R.color.blueTema8};

    Integer[]  pink_tema ={R.color.pinkTema1,R.color.pinkTema2,R.color.pinkTema3,R.color.pinkTema4,
            R.color.pinkTema5,R.color.pinkTema6,R.color.pinkTema7,R.color.pinkTema8};

    Integer[]  aqua_tema ={R.color.aquaTema1,R.color.aquaTema2,R.color.aquaTema3,R.color.aquaTema4,
            R.color.aquaTema5,R.color.aquaTema6,R.color.aquaTema7,R.color.aquaTema8};

    final String[] temaList={"포레스트", "바다","핑크","딥아쿠아"};
    Integer[]  temaToastList={R.drawable.tema_forest,R.drawable.tema_sea,R.drawable.tema_pink,R.drawable.tema_aqua};
    int startSetNum;
    String tema;


    TextView
            monday1,tuesday1,wednesday1,thursday1,friday1,
            monday2,tuesday2,wednesday2,thursday2,friday2,
            monday3,tuesday3,wednesday3,thursday3,friday3,
            monday4,tuesday4,wednesday4,thursday4,friday4,
            monday5,tuesday5,wednesday5,thursday5,friday5,
            monday6,tuesday6,wednesday6,thursday6,friday6,
            monday7,tuesday7,wednesday7,thursday7,friday7,
            monday8,tuesday8,wednesday8,thursday8,friday8,
            monday9,tuesday9,wednesday9,thursday9,friday9;




    Map<String, Integer[]> htema = new HashMap<String, Integer[]>();




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewGroup = (ViewGroup) inflater.inflate(R.layout.timetable, container, false);

        monday1 = (TextView) viewGroup.findViewById(R.id.monday1);
        tuesday1 = (TextView) viewGroup.findViewById(R.id.tuesday1);
        wednesday1 = (TextView) viewGroup.findViewById(R.id.wednesday1);
        thursday1 = (TextView) viewGroup.findViewById(R.id.thursday1);
        friday1 = (TextView) viewGroup.findViewById(R.id.friday1);

        monday2 = (TextView) viewGroup.findViewById(R.id.monday2);
        tuesday2 = (TextView) viewGroup.findViewById(R.id.tuesday2);
        wednesday2 = (TextView) viewGroup.findViewById(R.id.wednesday2);
        thursday2 = (TextView) viewGroup.findViewById(R.id.thursday2);
        friday2 = (TextView) viewGroup.findViewById(R.id.friday2);

        monday3 = (TextView) viewGroup.findViewById(R.id.monday3);
        tuesday3 = (TextView) viewGroup.findViewById(R.id.tuesday3);
        wednesday3 = (TextView) viewGroup.findViewById(R.id.wednesday3);
        thursday3 = (TextView) viewGroup.findViewById(R.id.thursday3);
        friday3 = (TextView) viewGroup.findViewById(R.id.friday3);

        monday4 = (TextView) viewGroup.findViewById(R.id.monday4);
        tuesday4 = (TextView) viewGroup.findViewById(R.id.tuesday4);
        wednesday4 = (TextView) viewGroup.findViewById(R.id.wednesday4);
        thursday4 = (TextView) viewGroup.findViewById(R.id.thursday4);
        friday4 = (TextView) viewGroup.findViewById(R.id.friday4);

        monday5 = (TextView) viewGroup.findViewById(R.id.monday5);
        tuesday5 = (TextView) viewGroup.findViewById(R.id.tuesday5);
        wednesday5 = (TextView) viewGroup.findViewById(R.id.wednesday5);
        thursday5 = (TextView) viewGroup.findViewById(R.id.thursday5);
        friday5 = (TextView) viewGroup.findViewById(R.id.friday5);

        monday6 = (TextView) viewGroup.findViewById(R.id.monday6);
        tuesday6 = (TextView) viewGroup.findViewById(R.id.tuesday6);
        wednesday6 = (TextView) viewGroup.findViewById(R.id.wednesday6);
        thursday6 = (TextView) viewGroup.findViewById(R.id.thursday6);
        friday6 = (TextView) viewGroup.findViewById(R.id.friday6);

        monday7 = (TextView) viewGroup.findViewById(R.id.monday7);
        tuesday7 = (TextView) viewGroup.findViewById(R.id.tuesday7);
        wednesday7 = (TextView) viewGroup.findViewById(R.id.wednesday7);
        thursday7 = (TextView) viewGroup.findViewById(R.id.thursday7);
        friday7 = (TextView) viewGroup.findViewById(R.id.friday7);

        monday8 = (TextView) viewGroup.findViewById(R.id.monday8);
        tuesday8 = (TextView) viewGroup.findViewById(R.id.tuesday8);
        wednesday8 = (TextView) viewGroup.findViewById(R.id.wednesday8);
        thursday8 = (TextView) viewGroup.findViewById(R.id.thursday8);
        friday8 = (TextView) viewGroup.findViewById(R.id.friday8);

        monday9 = (TextView) viewGroup.findViewById(R.id.monday9);
        tuesday9 = (TextView) viewGroup.findViewById(R.id.tuesday9);
        wednesday9 = (TextView) viewGroup.findViewById(R.id.wednesday9);
        thursday9 = (TextView) viewGroup.findViewById(R.id.thursday9);
        friday9 = (TextView) viewGroup.findViewById(R.id.friday9);
        set=(FloatingActionButton) viewGroup.findViewById(R.id.set);
        listplusbtn=(FloatingActionButton) viewGroup.findViewById(R.id.listplus_btn);

        TextView[] cellNameV = {monday1, tuesday1, wednesday1, thursday1, friday1,
                monday2, tuesday2, wednesday2, thursday2, friday2,
                monday3, tuesday3, wednesday3, thursday3, friday3,
                monday4, tuesday4, wednesday4, thursday4, friday4,
                monday5, tuesday5, wednesday5, thursday5, friday5,
                monday6, tuesday6, wednesday6, thursday6, friday6,
                monday7, tuesday7, wednesday7, thursday7, friday7,
                monday8, tuesday8, wednesday8, thursday8, friday8,
                monday9, tuesday9, wednesday9, thursday9, friday9};

        String[] cellNameK = {
                "Monday1", "Tuesday1", "Wednesday1", "Thursday1", "Friday1",
                "Monday2", "Tuesday2", "Wednesday2", "Thursday2", "Friday2",
                "Monday3", "Tuesday3", "Wednesday3", "Thursday3", "Friday3",
                "Monday4", "Tuesday4", "Wednesday4", "Thursday4", "Friday4",
                "Monday5", "Tuesday5", "Wednesday5", "Thursday5", "Friday5",
                "Monday6", "Tuesday6", "Wednesday6", "Thursday6", "Friday6",
                "Monday7", "Tuesday7", "Wednesday7", "Thursday7", "Friday7",
                "Monday8", "Tuesday8", "Wednesday8", "Thursday8", "Friday8",
                "Monday9", "Tuesday9", "Wednesday9", "Thursday9", "Friday9"};

        htema.put("포레스트", forest_tema);
        htema.put("바다", sea_tema);
        htema.put("핑크", pink_tema);
        htema.put("딥아쿠아", aqua_tema);

        startSetNum = 0;
        tema_update(); //디비에 저장된 테마 불러오기
        tableShow(cellNameV, cellNameK, htema.get(tema));



        viewGroup.findViewById(R.id.set).setOnClickListener(new View.OnClickListener() { // 시간표 테마 설정
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle("테마 설정");
                dlg.setIcon(R.drawable.palette5);
                dlg.setPositiveButton("확인", null);
                dlg.setSingleChoiceItems(temaList, startSetNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        View layout = inflater.inflate(R.layout.toast_layout, null);
                        ImageView img = (ImageView) layout.findViewById(R.id.toast_image);

                        img.setImageDrawable(getResources().getDrawable(temaToastList[i]));
                        if (i == 0) {
                            tema_DBupdate("포레스트");
                            tema_update();
                        } else if (i == 1) {
                            tema_DBupdate("바다");
                            tema_update();
                        } else if (i == 2) {
                            tema_DBupdate("핑크");
                            tema_update();
                        } else {
                            tema_DBupdate("딥아쿠아");
                            tema_update();
                        }
                        tableShow(cellNameV, cellNameK, htema.get(tema));
                        Toast toast = new Toast(container.getContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                        startSetNum = i;
                    }
                });
                dlg.show();
            }
        });

        viewGroup.findViewById(R.id.listplus_btn).setOnClickListener(new View.OnClickListener() { //강의 리스트 관리 화면으로 이동
            @Override
            public void onClick(View view) {
                Intent inIntent = new Intent(container.getContext(), ScheduleFragment_listplus.class);
                inIntent.putExtra("intema",tema);
                startActivity(inIntent);
            }
        });
        Intent temaIntent = getActivity().getIntent();
        if(temaIntent.getStringExtra("outtema")!=null) {
            tema = temaIntent.getStringExtra("outtema");
            tableShow(cellNameV, cellNameK, htema.get(tema));
        }
        return viewGroup;
    }

    public void tableShow(TextView[] cellNameV, String[] cellNameK, Integer[] tema){ //시간표에 강의 표시
        myHelper = new myDBHelper(getActivity());
        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor;
        cursor=sqlDB.rawQuery("SElECT * FROM My_Timetable",null);
        ArrayList<String> strDate = new ArrayList<String>(); //수업 시간 저장할 리스트
        ArrayList<String> Subject = new ArrayList<String>(); //강의명 저장할 리스트
        Map<String, TextView> ht = new HashMap<String, TextView>();
        for(int i=0;i<45;i++){
            ht.put(cellNameK[i],cellNameV[i]);
        }
        ////셀 전부 초기화///////
        for(int i=0;i<cellNameK.length;i++){
            ht.get(cellNameK[i]).setText("");
            ht.get(cellNameK[i]).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.cell));
        }

        while(cursor.moveToNext()) {
            strDate.add(cursor.getString(3)); // 리스트에 시간표값 추가
            Subject.add(cursor.getString(1)); // 리스트에 강의명 추가
        }

        for(int i=0;i<strDate.size();i++){
            String day_week=null;
            String[] day= strDate.get(i).split(","); //요일별로 구분
            for(int j=0;j< day.length;j++){
                String[] daytime = day[j].split(" "); //day_week:요일 daytime[]:시간(공백으로 구분)
                day_week=daytime[0].substring(0,1);
                daytime[0]=daytime[0].substring(1,2);

                int hour= daytime.length; //몇시간 수업인지 저장

                if (hour == 1) { //한 교시만 있을 경우
                    String cell_name = dayToEnglish(day_week) + daytime[0];// 선택할 셀의 이름
                    ht.get(cell_name).setText(Subject.get(i));
                    ht.get(cell_name).setBackground(ContextCompat.getDrawable(getActivity(), tema[i]));
                    ht.get(cell_name).setTextColor(Color.parseColor("#ffffff"));
                    ht.get(cell_name).setTextSize(13);

                } else {
                    for (int k = 0; k < hour; k++) {
                        String cell_name = dayToEnglish(day_week) + daytime[k]; // 선택할 셀의 이름
                        if (k == 0) { //수업이 연속으로 있을경우 맨 처음에만 수업명을 표시
                            ht.get(cell_name).setText(Subject.get(i));
                            ht.get(cell_name).setTextSize(12);
                        }
                        ht.get(cell_name).setBackground(ContextCompat.getDrawable(getActivity(), tema[i]));
                        ht.get(cell_name).setTextColor(Color.parseColor("#ffffff"));
                    }
                }

            }
        }
        cursor.close();
        sqlDB.close();

    }

    public void tema_update(){ //DB에 있는 테마 불러오기
        temaHelper=new myDBHelper(getActivity());
        temaDB=temaHelper.getReadableDatabase();
        Cursor cursor2;
        cursor2=temaDB.rawQuery("SElECT * FROM Tema_table",null);
        ArrayList<String> f = new ArrayList<String>();
        while(cursor2.moveToNext()){
            f.add(cursor2.getString(1));
        }
        tema=f.get(0);

        cursor2.close();
        temaDB.close();

        //테마에 따른 라디오버튼기본 위치값 설정
        //테마에 따른 라디오버튼기본 위치값 설정
        if(tema.equals("포레스트")){
            startSetNum = 0;
            set.setColorPressedResId(R.color.greenTema2);
            listplusbtn.setColorPressedResId(R.color.greenTema2);
        }
        else if(tema.equals("바다")){
            startSetNum = 1;
            set.setColorPressedResId(R.color.blueTema2);
            listplusbtn.setColorPressedResId(R.color.blueTema2);
        }
        else if(tema.equals("핑크")){
            startSetNum = 2;
            set.setColorPressedResId(R.color.pinkTema2);
            listplusbtn.setColorPressedResId(R.color.pinkTema2);
        }
        else if(tema.equals("딥아쿠아")){
            startSetNum = 3;
            set.setColorPressedResId(R.color.aquaTema1);
            listplusbtn.setColorPressedResId(R.color.aquaTema1);
        }

    }

    public void tema_DBupdate(String intema){ //DB에 테마 update
        temaHelper=new myDBHelper(getActivity());
        temaDB=temaHelper.getWritableDatabase();
        temaDB.execSQL("UPDATE Tema_table SET tema = '"+intema+"' WHERE id=1");
        temaDB.close();
    }

    public String dayToEnglish(String day) //요일 한글을 영어로 바꿔서 반환
    {
        if (day.equals("월"))
            return "Monday";
        else if (day.equals("화"))
            return "Tuesday";
        else if (day.equals("수"))
            return "Wednesday";
        else if (day.equals("목"))
            return "Thursday";
        else if (day.equals("금"))
            return "Friday";
        else
            return "0";
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