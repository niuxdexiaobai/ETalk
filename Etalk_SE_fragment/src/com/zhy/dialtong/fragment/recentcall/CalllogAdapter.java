/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.recentcall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.RecentCallDetailActivity;

/**
 * 提供通话记录ListView的视图适配。
 * <p>
 * 注意：由于当前的UI设计和类归档位置，ListView进入删除模式的方法在Adapter内部，
 * 而控制ListView退出删除模式的方法则在CallFragment里，两个类之间没有直接的属性继承关系，
 * 需要使用接口和额外的方法来实现通信。<br/>
 * 	当CallFragment需要通知Adapter切换到正常模式的时候，要先调用{@link onListViewModeChangedListener}的方法
 * 通知适配器，<strong>这时候不要切换界面显示。</strong>ListView更新视图后会再次通知Fragment，此时Fragment才应该
 * 执行UI的恢复工作。
 * </p>
 * @author Muyangmin
 * @create 2014-9-5
 * @version 1.0
 */
public final class CalllogAdapter extends BaseAdapter {
	private static final String LOG_TAG = "CalllogAdapter";
	
	/*
	 * 原有的、全部通话记录数据。
	 * list在执行refreshDistinctRecords()方法后会被置为null以减少内存占用。
	 */
	private ArrayList<CallRecord> list;				//
	private LayoutInflater inflater; 				//加载布局用
	/*
	 * 根据一定的规则过滤的单条数据，用于显示和控制。
	 * 当执行对某个联系人的删除操作后，distinctList中的数据也会删除，并调用notifyDataSetChanged()方法刷新UI。
	 * 也就是说，在刷新的时候list仍然为null，而新的列表数据是根据distinctList的数据来进行显示的。
	 * 如果UI要求刷新，则必须提供一个非空的list。
	 */
	private ArrayList<CallRecord> distinctList;		//
	
	private Context context;						//用于启动新的Activity
	private boolean isDeleteMode;
	//这里，由于每个Item都需要保存状态，故使用ArrayList并不会浪费空间，但要注意List的大小。
	private ArrayList<Boolean> itemSelectedList;		//用于保存已选择的项
	private int currentCheckItemCount = 0;

	/**
	 * 尝试提高删除模式的性能。
	 */
	private SparseArray<View> views;
	/**
	 * 创建适配器。
	 * @param list 通话记录列表
	 * @param modeListener 接收ListView模式改变的接口
	 */
	public CalllogAdapter(Context context, ArrayList<CallRecord> list) {
		this.list = list;
		this.context = context;
		distinctList = new ArrayList<CallRecord>();
		refreshDistinctRecords();//按每个号码筛选记录，调用该方法之前须确保list和distinctList不为null。
		inflater = LayoutInflater.from(context);
		views = new SparseArray<View>();
		itemSelectedList = new ArrayList<Boolean>();
		// 初始化所有checked box选中状态为false 
		for (int i=0; i<getCount(); i++){
			itemSelectedList.add(false);
		}
	}
	
	/**
	 * 刷新列表的数据。
	 * @param list
	 */
	public void setDataList(ArrayList<CallRecord> list){
		if (list!=null){
			this.list = list;
			distinctList.clear();
			refreshDistinctRecords();
		}
		views.clear();
		itemSelectedList.clear();
		// 初始化所有checked box选中状态为false
		for (int i = 0; i < getCount(); i++) {
			itemSelectedList.add(false);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent){
		view = views.get(position);
		if (view==null){
			view = createCalllogView(position);
			Log.i(LOG_TAG, "i'm putting into sparsearray:"+position);
			views.put(position, view);
		}
		
		return view;
	}
	
	/**
	 * 创建通话记录主视图。
	 * @param position
	 * @return
	 */
	private View createCalllogView(final int position){
		ViewHolder holder;
		View view = inflater.inflate(R.layout.recent_call_list_item, null);
		holder = new ViewHolder();
		holder.call_type = (ImageView) view.findViewById(R.id.call_type);
		holder.name = (TextView) view.findViewById(R.id.name);
//		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.time = (TextView) view.findViewById(R.id.time);
		holder.call_btn = (LinearLayout) view.findViewById(R.id.call_btn);
		holder.count = (TextView) view.findViewById(R.id.count);
		holder.call_log_item = (RelativeLayout) view.findViewById(R.id.call_log_item);
//		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				requestChangeItemCheckState(position, isChecked, true);
//			}
//		});
//		//当前是否为删除模式
//		if (isDeleteMode){
//			checkBox.setVisibility(View.VISIBLE);
//			
//			btn_detail.setVisibility(View.INVISIBLE);
//			checkBox.setChecked(itemSelectedList.get(position));
//			
//		}
//		else{
//			btn_detail.setVisibility(View.VISIBLE);
//			checkBox.setVisibility(View.INVISIBLE);
//		}
		
		CallRecord record = distinctList.get(position);
		switch (record.getType()) {		
			case CALL_INCOMING:holder.call_type.setImageResource(R.drawable.icon_history_incoming);
				break;
			case CALL_MISSED:holder.call_type.setImageResource(R.drawable.icon_history_missed);
				break;
				//outgoing
			default:holder.call_type.setImageResource(R.drawable.icon_history_outbound);
				break;
		}
		
		String name1 = record.getCacheName();
		if (name1==null || name1.equals("")) {
			holder.name.setText(record.getNumber());
		}
		else{
			holder.name.setText(name1);	
		}
		holder.time.setText(getTimeString(record.getDate()));
		
		addViewListener(holder.call_btn, record, position);
		
		return view;
	}
	
	private static class ViewHolder {
		ImageView call_type;
		TextView name;
		TextView number;
		TextView time;
		LinearLayout call_btn;
		RelativeLayout call_log_item;
		TextView count;
	}
	
	private void addViewListener(View view, final CallRecord clb, final int position){
		view.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
//				Uri uri = Uri.parse("tel:" + clb.getNumber());
//				Intent it = new Intent(Intent.ACTION_CALL, uri);
//				ctx.startActivity(it);
				Intent intent=new Intent(context,RecentCallDetailActivity.class);
//				intent.putExtra("phoneNumber", getIntent().getStringExtra("number"));
//				intent.putExtra("name", getIntent().getStringExtra("name"));
				intent.putExtra("phoneNumber", clb.getNumber());
				context.startActivity(intent);
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
	private void refreshDistinctRecords(){
		HashSet<String> numberset = new HashSet<String>();
		distinctList.clear();
		for (int i=0; i<list.size(); i++){
			CallRecord record = list.get(i);
			String number = record.getNumber();
			if (!numberset.contains(number)){
				numberset.add(number);
				distinctList.add(record);
			}
		}
		//将list置为空，减少内存消耗
		list.clear();
		list = null;
	}
	
	@Override
	public int getCount() {
		return distinctList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return distinctList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
