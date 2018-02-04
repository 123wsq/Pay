package com.lk.td.pay.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lk.td.pay.beans.TradeBean;
import com.pay.library.config.AppConfig;
import com.pay.library.config.AppConfig.BUSTYPE;
import com.td.app.pay.hx.R;

import java.util.List;

public class DealRecordAdapter extends BaseAdapter {

    private Context c;
    // private List<Map<String, Object>> list;
    private List<TradeBean> list;

    public DealRecordAdapter(Context c, List<TradeBean> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    public void refreshValues(List<TradeBean> val) {
        this.list = val;
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(
                    R.layout.deal_record_item, null);
            holder.orderName = (TextView) convertView
                    .findViewById(R.id.tv_deal_record_name);
            holder.orderAmt = (TextView) convertView
                    .findViewById(R.id.tv_deal_record_amount);
            holder.orderDate = (TextView) convertView
                    .findViewById(R.id.tv_deal_record_date);
            holder.orderStatus = (TextView) convertView
                    .findViewById(R.id.tv_deal_record_status);

            convertView.setTag(holder);
        }
        String type = toS(list.get(position).getBusType());

        TradeBean bean = list.get(position);
        // holder.orderName.setText(toS(list.get(position).getBusType()));
        String status = list.get(position).getTranState();
        holder.orderAmt.setText(list.get(position).getTranAmt() + " 元");
        holder.orderDate.setText(bean.getTarnTime());
        
        if (type.equals(AppConfig.BUSTYPE.BUS_TYPE_01)) {
        	
        	String html = setHtmlText(AppConfig.BUSTYPE.getValue(AppConfig.BUSTYPE.BUS_TYPE_01),
        			AppConfig.PAYTYPE.getValue(bean.getPayWay()));
//        	 holder.orderName.setText("收款" +"        "+ AppConfig.PAYTYPE.getInstance().getValue(bean.getPayWay()));
        	holder.orderName.setText(Html.fromHtml(html));
        	 holder.orderStatus.setText(AppConfig.ORDSTATUS.getContent(bean.getTranState()));
		}else if (type.equals("02")) {
            holder.orderName.setText(AppConfig.BUSTYPE.getValue(AppConfig.BUSTYPE.BUS_TYPE_02));
		}else if(type.equals(AppConfig.BUSTYPE.BUS_TYPE_03)) {
//			 holder.orderName.setText("提现"+"        "+AppConfig.ACCTTYPE.getInstance().getValue(bean.getAcctType()));
			String html = setHtmlText(AppConfig.BUSTYPE.getValue(AppConfig.BUSTYPE.BUS_TYPE_03),
        			AppConfig.ACCTTYPE.getValue(bean.getAcctType()));
			holder.orderName.setText(Html.fromHtml(html));
			 holder.orderStatus.setText(AppConfig.ORDSTATUS.getContent(bean.getTranState()));
		}else{
            holder.orderName.setText("--");
        }
        /*
        if (type.equals("01")) {
            holder.orderName.setText("收款");
            if (bean.getTranState().equals("00")) {
                holder.orderStatus.setText("未交易");
            } else if (bean.getTranState().equals("01")) {
                holder.orderStatus.setText("交易成功");
            } else if (bean.getTranState().equals("02")) {
                holder.orderStatus.setText("交易失败");
            } else if (bean.getTranState().equals("03")) {
                holder.orderStatus.setText("可疑");
            } else if (bean.getTranState().equals("04")) {
                holder.orderStatus.setText("审核中");
            } else if (bean.getTranState().equals("05")) {
                holder.orderStatus.setText("审核拒绝");
            } else if (bean.getTranState().equals("06")) {
                holder.orderStatus.setText("未交易");
            } else if (bean.getTranState().equals("07")) {
                holder.orderStatus.setText("交易成功");
            } else if (bean.getTranState().equals("08")) {
                holder.orderStatus.setText("交易中");
            }
//            else if (bean.getTranState().equals("09")) {
//                holder.orderStatus.setText("部分退货");
//            }
        } else if (type.equals("02")) {
            holder.orderName.setText("消费");
        } else if (type.equals("03")) {
            holder.orderName.setText("提现");
            if (bean.getTranState().equals("00")) {
                holder.orderStatus.setText("未交易");
            } else if (bean.getTranState().equals("01")) {
                holder.orderStatus.setText("交易中");
            } else if (bean.getTranState().equals("02")) {
                holder.orderStatus.setText("交易失败");
            } else if (bean.getTranState().equals("03")) {
                holder.orderStatus.setText("可疑");
            } else if (bean.getTranState().equals("04")) {
                holder.orderStatus.setText("审核中");
            } else if (bean.getTranState().equals("05")) {
                holder.orderStatus.setText("审核拒绝");
            } else if (bean.getTranState().equals("06")) {
                holder.orderStatus.setText("交易中");
            } else if (bean.getTranState().equals("07")) {
                holder.orderStatus.setText("交易成功");
            } else if (bean.getTranState().equals("08")) {
                holder.orderStatus.setText("交易中");
            }
        } else {
            holder.orderName.setText("--");
        }
        */
        convertView.setMinimumHeight(70);
        return convertView;
    }

    private String toS(Object str) {
        if (null == str)
            return "";
        return str.toString();
    }

    class ViewHolder {
        TextView orderName;
        TextView orderAmt;
        TextView orderDate;
        TextView orderStatus;
    }
    
    public static String setHtmlText(String busType, String accType){
		String html = "";
		
			StringBuffer sb = new StringBuffer();
			sb.append("<font color=\"#000000\">");
			sb.append(busType);
			sb.append("</font>");
			
			for (int i = 0; i < 6; i++) {				
				sb.append("&nbsp;");
			}
			sb.append("<font color=\"#878787\">");
			sb.append(accType);
			sb.append("</font>");
			
			html = sb.toString();
		
		return html;
	}
}
