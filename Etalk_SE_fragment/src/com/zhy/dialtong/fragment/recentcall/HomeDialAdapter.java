package com.zhy.dialtong.fragment.recentcall;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.RecentCallDetailActivity;

public class HomeDialAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<CallLogBean> list;
	private LayoutInflater inflater;
//	private ArrayList<CallRecord> list;
//	private ArrayList<CallRecord> distinctList;
	
	public HomeDialAdapter(Context context, List<CallLogBean> list) {
		this.list = list;
		this.ctx = context;
//		distinctList = new ArrayList<CallRecord>();
//		refreshDistinctRecords();
		this.inflater = LayoutInflater.from(context);
	}
	
	/**
	 * 刷新列表的数据。
	 * @param list
	 */
//	public void setDataList(ArrayList<CallRecord> list){
//		if (list!=null){
//			this.list = list;
//			distinctList.clear();
//			refreshDistinctRecords();
//		}
//		views.clear();
//		itemSelectedList.clear();
//		// 初始化所有checked box选中状态为false
//		for (int i = 0; i < getCount(); i++) {
//			itemSelectedList.add(false);
//		}
//		notifyDataSetChanged();
//	}
	
	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.recent_call_list_item, null);
			holder = new ViewHolder();
			holder.call_type = (ImageView) convertView.findViewById(R.id.call_type);
			holder.name = (TextView) convertView.findViewById(R.id.name);
//			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.call_btn = (TextView) convertView.findViewById(R.id.call_btn);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.call_log_item = (RelativeLayout) convertView.findViewById(R.id.call_log_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final CallLogBean clb = list.get(position);
//		final CallRecord record = distinctList.get(position);
		switch (clb.getType()) {
		case 1:
			holder.call_type.setBackgroundResource(R.drawable.icon_history_incoming);
			break;
		case 2:
			holder.call_type.setBackgroundResource(R.drawable.icon_history_outbound);
			break;
		case 3:
			holder.call_type.setBackgroundResource(R.drawable.icon_history_missed);
			break;
		}
		holder.name.setText(clb.getName());
		if (holder.name==null || holder.name.equals("")) {
			holder.name.setText(clb.getNumber());
		}
		else{
			holder.name.setText(clb.getName());	
		}
//		holder.number.setText(clb.getNumber());
		holder.time.setText(clb.getDate());
//		holder.count.setText("(" + clb.getCount() );
		holder.call_log_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Uri uri = Uri.parse("tel:" + clb.getNumber());
//				Intent it = new Intent(Intent.ACTION_CALL, uri);
//				ctx.startActivity(it);
			}
		});
//		holder.call_log_item.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				ctx.getContentResolver().delete(CallLog.Calls.CONTENT_URI,null,null);
//				return true;
//			}
//		});
		
		addViewListener(holder.call_btn, clb, position);//右边实现点击跳转到下一个通话记录详情页
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView call_type;
		TextView name;
		TextView number;
		TextView time;
		TextView call_btn;
		RelativeLayout call_log_item;
		TextView count;
	}
	
	private void addViewListener(View view, final CallLogBean clb, final int position){
		view.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
//				Uri uri = Uri.parse("tel:" + clb.getNumber());
//				Intent it = new Intent(Intent.ACTION_CALL, uri);
//				ctx.startActivity(it);
				Intent intent=new Intent(ctx,RecentCallDetailActivity.class);
//				intent.putExtra("phoneNumber", getIntent().getStringExtra("number"));
//				intent.putExtra("name", getIntent().getStringExtra("name"));
				intent.putExtra("phoneNumber", clb.getNumber());
				ctx.startActivity(intent);
			}
		});
	}
	
	/**
	 * 负责计算并返回每条通话记录的时间。
	 * @param date 要计算的时间
	 * @return 返回计算后的时间字符串。
	 */
	private String getTimeString(long date){
		long now = System.currentTimeMillis();
		long period = now -date;
		if (period<0){
			return "来自Future";
		}
		if (period < 300000){//5分钟内
			return "刚刚";
		}
		if (period < 3600000){//1小时内
			return (period/60000)+"分钟前";
		}
		if (period < 86400000){//1天内
			return (period/3600000)+"小时前";
		}
		if (period < 172800000){
			return "昨天";
		}
		if (period < 259200000){
			return "前天";
		}
		return new SimpleDateFormat("M月d日", Locale.CHINA).format(new Date(date));
	}
	
	/**
	 * 按每个号码筛选记录，调用该方法之前须确保list和distinctList不为null。
	 */
//	private void refreshDistinctRecords(){
//		HashSet<String> numberset = new HashSet<String>();
//		list = new ArrayList<CallRecord>();
//		distinctList.clear();
//		for (int i=0; i<list.size(); i++){
//			CallRecord record = list.get(i);
//			String number = record.getNumber();
//			if (!numberset.contains(number)){
//				numberset.add(number);
//				distinctList.add(record);
//			}
//		}
//		//将list置为空，减少内存消耗
//		list.clear();
//		list = null;
//	}
//
}
