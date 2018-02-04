package com.lk.td.pay.wedget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lk.td.pay.adapter.ProvinceAdapter;
import com.lk.td.pay.golbal.MApplication;
import com.td.app.pay.hx.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowProvinceListDialog extends DialogFragment implements OnItemClickListener {

    private TextView titleTxt;
    private ListView provinceListView;
    private String tipStr;
    private ArrayList<HashMap<String, Object>> arrayList;//既当省份用又当具体的市用
    private ArrayList<HashMap<String, Object>> tempProvinceArrayList;//临时存储省份
    private ProvinceAdapter provinceAdapter;
    private MApplication mApplication;
    private IGetProvinceIdAndCityId iGetProvinceIdAndCityId;
    private String provName, provId;
    private String cityName, cityId;

    public ShowProvinceListDialog(IGetProvinceIdAndCityId iGetProvinceIdAndCityId) {

        this.iGetProvinceIdAndCityId = iGetProvinceIdAndCityId;

    }

    public void setContent(String tipStr, ArrayList<HashMap<String, Object>> arrayList) {

        this.tipStr = tipStr;
        this.arrayList = arrayList;
        this.tempProvinceArrayList = arrayList;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mApplication = (MApplication) getActivity().getApplication();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.province_city_layout, null);
        titleTxt = (TextView) view.findViewById(R.id.titleTxt);
        titleTxt.setText(tipStr);
        provinceListView = (ListView) view.findViewById(R.id.provinceListView);
        provinceAdapter = new ProvinceAdapter(getActivity(), arrayList);
        provinceListView.setAdapter(provinceAdapter);
        provinceListView.setOnItemClickListener(this);
        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(30, 50, 30, 100);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        return dialog;
    }

    public void setNotifyDataSetChanged() {

        provinceAdapter.setArrayList(arrayList);
        provinceAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String value = (String) arrayList.get(position).get("provName");
        if (!TextUtils.isEmpty(value)) {
            provName = value;
            provId = (String) arrayList.get(position).get("provId");
            arrayList = (ArrayList<HashMap<String, Object>>) arrayList.get(position).get("cityList");
            titleTxt.setText("请选择城市");
            setNotifyDataSetChanged();

        } else {
            cityName = (String) arrayList.get(position).get("cityName");
            cityId = (String) arrayList.get(position).get("cityId");
            iGetProvinceIdAndCityId.getProvinceIdAndCityId(provName, provId, cityName, cityId);
        }


    }

    public interface IGetProvinceIdAndCityId {

        public void getProvinceIdAndCityId(String provName, String provId, String cityName, String cityId);

    }

}
