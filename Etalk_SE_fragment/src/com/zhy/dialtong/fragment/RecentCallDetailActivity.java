package com.zhy.dialtong.fragment;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.Contact;
import com.zhy.dialtong.fragment.contacts.ContactReader;
import com.zhy.dialtong.fragment.recentcall.CallLogManager;
import com.zhy.dialtong.fragment.recentcall.CallRecord;
import com.zhy.dialtong.utils.widgets.BitmapUtils;

public class RecentCallDetailActivity extends Activity implements OnClickListener{
	
	private TextView show_name,show_phonenumber;
	private ListView contact_detail_calllog;
	private ImageView contacts_imageView,calllog_back,calllog_edit;
	private ImageButton ImageButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recent_call_detail);
		String number = getIntent().getStringExtra("phoneNumber");
		init(number);
	}

	private void init(final String number) {
		// TODO Auto-generated method stub
		calllog_back = (ImageView) findViewById(R.id.button_back);
		calllog_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		calllog_edit = (ImageView) findViewById(R.id.button_little_edit);
		
		show_phonenumber = (TextView) findViewById(R.id.numbershow1);
		show_phonenumber.setText(number);
		
		ImageButton = (android.widget.ImageButton) findViewById(R.id.button_calllog_info_call);
		ImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + number);
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				startActivity(it);
			}
		});
		show_name = (TextView) findViewById(R.id.showname);
		
		contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
		ContactReader reader = ContactReader.getInstance(this);
		Contact contact = reader.getContactByNumber(number);
		if (null == reader.getContactByNumber(number)){
			contacts_imageView.setImageResource(R.drawable.btn_head);
			show_name.setText(number);
			
		}
		else{
			Bitmap bitmap = reader.getContactPhoto(contact.getId());
			if (bitmap!=null){
				bitmap = BitmapUtils.toRoundBitmap(bitmap);
				contacts_imageView.setImageBitmap(bitmap);
//			Drawable avatar = new BitmapDrawable(getResources(), bitmap);
//			contacts_imageView.setBackgroundDrawable(avatar);
			}
			show_name.setText(contact.getName());
		}
		
		
		CallLogManager manager = new CallLogManager(this);
		ArrayList<CallRecord> list = manager.getSpecificNumberCallRecords(number);
		DetailAdapter adapter = new DetailAdapter(this, list);
		contact_detail_calllog = (ListView) findViewById(R.id.contact_detail_calllog);
		TextView tv_header = new TextView(this);
		tv_header.setText("共有"+list.size()+"条通话记录");
		contact_detail_calllog.addHeaderView(tv_header);
		contact_detail_calllog.setAdapter(adapter);
		contact_detail_calllog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	private static class DetailAdapter extends BaseAdapter{

		private ArrayList<CallRecord> list;
		private LayoutInflater inflater;

		public DetailAdapter(Context context, ArrayList<CallRecord> list) {
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView ==null){
				convertView = inflater.inflate(R.layout.calllog_detail_list_item, null);
			}
			CallRecord record = list.get(position);
			ImageView img_type = (ImageView) convertView.findViewById(R.id.calllog_detail_item_type);
			switch (record.getType()) {
				case CALL_INCOMING:
					img_type.setImageResource(R.drawable.icon_history_incoming);
					break;
				case CALL_MISSED:
					img_type.setImageResource(R.drawable.icon_history_missed);
					break;
				case CALL_OUTGOING:
					img_type.setImageResource(R.drawable.icon_history_outbound);
					break;
				default:
					break;
			}
			TextView tv_time = (TextView)convertView.findViewById(R.id.calllog_detail_item_time);
			tv_time.setText(getTimeString(record.getDate()));
			TextView tv_period = (TextView)convertView.findViewById(R.id.calllog_detail_item_period);
			tv_period.setText(getPeriodString(record.getDuration()));
			return convertView;
		}
		
		private String getPeriodString(long period){
			int hour = (int) (period/3600);
			int minute = (int) ((period%3600)/60);
			int second = (int) ((period%60));
			StringBuilder builder = new StringBuilder();
			if (hour >0){
				builder.append(hour).append("时");
			}
			builder.append(minute).append("分")
			.append(second).append("秒");
			return builder.toString();
		}
		
		private String getTimeString(long time){
			long current = System.currentTimeMillis();
			long period = current-time;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
			if (period <= 0){
				return "未来人!";
			}
			Date date = new Date(current);
			String stra = format.format(date);
			date.setTime(time);
			String strb = format.format(date);
			if (period < 86400000 && stra.equals(strb)){//一天以内
				return "今天  " + new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date(time));
			}
			return new SimpleDateFormat("M月d日  HH:mm", Locale.CHINA).format(new Date(time));
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
