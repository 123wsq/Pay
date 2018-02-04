package com.lk.td.pay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.td.pay.beans.MainDataBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.td.app.pay.hx.R;

import java.util.List;

/**
 * Created by wsq on 2016/5/6.
 */
public class MainDataAdapter extends BaseAdapter{

    private Context mContext;
    private List<MainDataBean> mData;
    private int COLUMNS ;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private int screenWidth ;
    private int grid_size;
    public MainDataAdapter(Context context, List<MainDataBean> list, int columns){
        this.mContext = context;
        this.mData = list;
        this.COLUMNS = columns;
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        grid_size =screenWidth / 3 ;

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoader = ImageLoader.getInstance();
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_main_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            holder.image = (ImageView) convertView.findViewById(R.id.item_image);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //设置ITEM大小
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, grid_size);
        holder.layout.setLayoutParams(params);
        //动态设置图片大小  itemWidth * 2 / 5;
        int imageSize = grid_size  / COLUMNS;
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        holder.image.setLayoutParams(imageParams);

        MainDataBean data = mData.get(position);
        holder.name.setText(data.getName());
        imageLoader.displayImage(data.getImgPath(), holder.image);
        return convertView;
    }

    public class ViewHolder{

        LinearLayout layout;
        ImageView image;
        TextView name;
    }


}
