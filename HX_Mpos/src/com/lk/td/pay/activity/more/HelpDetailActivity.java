package com.lk.td.pay.activity.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.lk.td.pay.activity.base.BaseFragmentActivity;
import com.lk.td.pay.adapter.HelpExpandableListAdapter;
import com.td.app.pay.hx.R;

public class HelpDetailActivity extends BaseFragmentActivity {

    private HelpExpandableListAdapter adapter;
    private ExpandableListView listView;
    private TextView contentText;
    private String[] groups;
    private String[] childrens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_detail);
        contentText = (TextView) findViewById(R.id.help_detail_content);
        listView = (ExpandableListView) findViewById(R.id.help_detail_expandablelistview);
        initData();
        adapter = new HelpExpandableListAdapter(this, groups, childrens);
        listView.setGroupIndicator(null);
        listView.setAdapter(adapter);
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    private void initData() {
        int position = getIntent().getIntExtra("position", 0);
        contentText.setText(getIntent().getStringExtra("NAME"));
        switch (position) {
            case 0:
                groups = getResources().getStringArray(R.array.question_one_q);
                childrens = getResources().getStringArray(R.array.question_one_a);
                break;
            case 1:
                groups = getResources().getStringArray(R.array.question_two_q);
                childrens = getResources().getStringArray(R.array.question_two_a);
                break;
            case 2:
                groups = getResources().getStringArray(R.array.question_three_q);
                childrens = getResources().getStringArray(R.array.question_three_a);
                break;
            case 3:
                groups = getResources().getStringArray(R.array.question_four_q);
                childrens = getResources().getStringArray(R.array.question_four_a);
                break;
            case 4:
                groups = getResources().getStringArray(R.array.question_five_q);
                childrens = getResources().getStringArray(R.array.question_five_a);
                break;
            case 5:
                groups = getResources().getStringArray(R.array.question_six_q);
                childrens = getResources().getStringArray(R.array.question_six_a);
                break;
            case 6:
                groups = getResources().getStringArray(R.array.question_seven_q);
                childrens = getResources().getStringArray(R.array.question_seven_a);
                break;
            case 7:
                groups = getResources().getStringArray(R.array.question_eight_q);
                childrens = getResources().getStringArray(R.array.question_eight_a);
                break;
            case 8:
                groups = getResources().getStringArray(R.array.question_nine_q);
                childrens = getResources().getStringArray(R.array.question_nine_a);
                break;
            case 9:
                groups = getResources().getStringArray(R.array.question_ten_q);
                childrens = getResources().getStringArray(R.array.question_ten_a);
                break;
            case 10:
                groups = getResources().getStringArray(R.array.question_eleven_q);
                childrens = getResources().getStringArray(R.array.question_eleven_a);
                break;
            case 11:
                groups = getResources().getStringArray(R.array.question_twelve_q);
                childrens = getResources().getStringArray(R.array.question_twelve_a);
                break;

            default:
                break;
        }
    }
}
