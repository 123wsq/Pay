package com.lk.td.pay.adapter;

import com.td.app.pay.hx.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class HelpExpandableListAdapter extends BaseExpandableListAdapter{

	public String[] groups;  
    public String[] childrens;
    private Context context;
    
	public HelpExpandableListAdapter(Context context, String[] groups, String[] childrens){
		this.groups = groups;
		this.childrens = childrens;
		this.context = context;
	}
	@Override
	public int getGroupCount() {

		return groups.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groups[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return childrens[groupPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
        return childPosition; 
	}

	@Override
	public boolean hasStableIds() {
		
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getGenericView();  
		textView.setTextSize(18);
		textView.setTextColor(context.getResources().getColor(R.color.darkgray));
        textView.setText(groups[groupPosition]);  
        return textView; 
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 TextView textView=getGenericView();  
		 textView.setTextSize(16);
		 textView.setBackgroundColor(context.getResources().getColor(R.color.white));
         textView.setText(getChild(groupPosition, childPosition).toString());  
         return textView; 
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	//新建一个TextView  
    public TextView getGenericView() {  
           // Layout parameters for the ExpandableListView  
           AbsListView.LayoutParams lp = new AbsListView.LayoutParams(  
                   ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);  
           TextView textView = new TextView(context);  
           textView.setLayoutParams(lp);  
           // Center the text vertically  
           textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);  
           // Set the text starting position  
           textView.setPadding(30, 20, 30, 20);  
           return textView;  
   }  

}
