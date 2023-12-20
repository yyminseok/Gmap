package com.example.gmap_project2.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gmap_project2.DataBaseHelper;
import com.example.gmap_project2.InfoActivity;
import com.example.gmap_project2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    ViewGroup viewGroup;

    LinearLayout layout_search_result;
    MultiAutoCompleteTextView edt_search;
    ImageButton btn_search;
    Context ct;
    List<List<String>> result;
    Spinner spn_from;
    HashMap<String, String> table_info = new HashMap<String,String>();
    HashMap<String, String> col_info = new HashMap<String,String>();
    HashMap<String, String> con_info = new HashMap<String,String>();
    HashMap<String, String> sort_info = new HashMap<String,String>();
    Cursor cursor;

    View.OnClickListener res_list_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(),
                    InfoActivity.class);
            List<String> res = result.get(v.getId());
            String opt = spn_from.getSelectedItem().toString();

            for(int i = 0 ; i<res.size() ; i++){
                intent.putExtra("res"+Integer.toString(i), res.get(i));
            }
            intent.putExtra("opt", opt);
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        table_info.put("건물", "Building_table");
        table_info.put("호수", "Building_table");
        table_info.put("교수", "Time_table");
        table_info.put("과목", "Time_table");
        col_info.put("건물", "DISTINCT 건물이름");
        col_info.put("호수", "호실, 건물이름, 분류");
        col_info.put("교수", "DISTINCT 담당교수");
        col_info.put("과목", "교과목명, 전공, 분반");
        con_info.put("건물", "건물이름");
        con_info.put("호수", "호실");
        con_info.put("교수", "담당교수");
        con_info.put("과목", "교과목명");
        sort_info.put("건물", "ORDER BY 건물이름");
        sort_info.put("호수", "ORDER BY 건물이름, 호실");
        sort_info.put("교수", "ORDER BY 담당교수");
        sort_info.put("과목", "ORDER BY 전공, 교과목명, 분반");

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        layout_search_result = (LinearLayout) viewGroup.findViewById(R.id.layout_search_result);
        edt_search = (MultiAutoCompleteTextView) viewGroup.findViewById(R.id.edt_search);
        btn_search = (ImageButton) viewGroup.findViewById(R.id.btn_search);

        String[] subject_list = new String[5];
        subject_list[0]="선택하세요";subject_list[1]="건물";subject_list[2]="호수";
        subject_list[3]="교수";subject_list[4]="과목";

        spn_from = (Spinner) viewGroup.findViewById(R.id.spn_from);

        ArrayAdapter firstAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, subject_list);
        spn_from.setAdapter(firstAdapter);

        spn_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String from = spn_from.getItemAtPosition(i).toString();

                if(from == "선택하세요"){
                    Toast.makeText(ct.getApplicationContext(), "검색 주체를 선택하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String table_name = table_info.get(from);
                    String col_name = col_info.get(from);
                    String sort_con = sort_info.get(from);
                    String Q = "SELECT "+col_name+" FROM "+table_name+" "+sort_con;
                    result = search(Q);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, col_ref(result,0));
                    MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
                    edt_search.setTokenizer(token);
                    edt_search.setAdapter(adapter);
                    make_result_list(result);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String from = spn_from.getSelectedItem().toString();
                String arg = edt_search.getText().toString();
                String table_name = table_info.get(from);
                String col_name = col_info.get(from);
                String condition = con_info.get(from);

                String Q = "SELECT "+col_name+" FROM "+table_name+" WHERE "+condition+" LIKE \"%"+arg+"%\"";
                result = search(Q);
                make_result_list(result);
            }
        });


        ct = container.getContext();

        return viewGroup;
    }

    public LinearLayout make_linear_layout(List<String> res, int id, String opt){
        LinearLayout lay = new LinearLayout(getActivity());
        lay.setId(id);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setBackgroundColor(Color.rgb(234, 243, 255)); // #EAF3FF
        lay.setPadding(0,20,0,20);
        lay.setGravity(Gravity.CENTER);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setBackgroundResource(R.drawable.list_info_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lay.setLayoutParams(params);
        lay.setOnClickListener(res_list_clickListener);

        LinearLayout.LayoutParams t_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t_params.setMargins(30,0,30,0);
        int w_size = res.size();
        int text_size = 15;

        for(int i =0 ; i<w_size ; i++){
            TextView tv = new TextView(getContext());
            String set_t = res.get(i);
            if(set_t != null && !set_t.equals("교양") && set_t.length()<=2){
                set_t = set_t+" 분반";
            }
            tv.setText(set_t);
            tv.setLayoutParams(t_params);
            tv.setTextSize(text_size);
            tv.setTextColor(Color.rgb(0,0,0));
            lay.addView(tv);
        }
        return lay;
    }

    public void make_result_list(List<List<String>> result){
        layout_search_result.removeAllViews();
        String opt = spn_from.getSelectedItem().toString();
        int id = 0;
        for(List<String> res : result){
            layout_search_result.addView(make_linear_layout(res, id, opt));
            id+=1;
        }
    }

    public List<String> col_ref(List<List<String>> list, int col){
        List<String> res = new ArrayList<String>();
        for(List<String> l : list){
            res.add(l.get(col));
        }
        return res;
    }

    public List<List<String>> search(String query) {

        DataBaseHelper dbHelper = new DataBaseHelper(ct);
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
}

