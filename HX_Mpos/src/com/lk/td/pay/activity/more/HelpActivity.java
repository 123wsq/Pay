package com.lk.td.pay.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.td.app.pay.hx.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpActivity extends BaseFragmentActivity {

    private ListView listView;
    private Button btn_back;
    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initData();
        listView = (ListView) findViewById(R.id.help_listview);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.activity_help_item, new String[]{"NAME"}, new int[]{R.id.help_item_text});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goDetail(position);

            }
        });
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setText("使用帮助");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    private void initData() {
        String[] items = getResources().getStringArray(R.array.help_list);
        list = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("NAME", items[i]);
            list.add(map);
        }
    }

    private void goDetail(int position) {
        Intent it = new Intent(this, HelpDetailActivity.class);
        it.putExtra("NAME", list.get(position).get("NAME"));
        it.putExtra("position", position);
        startActivity(it);

    }

}
