package com.zhy.dialtong.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.Contact;
import com.zhy.dialtong.fragment.contacts.ContactReader;
import com.zhy.dialtong.fragment.dial.PhoneCaller;
import com.zhy.dialtong.fragment.recentcall.CallLogManager;
import com.zhy.dialtong.fragment.recentcall.CallRecord;

public class CalllogDetailActivity extends Activity {
	private static final String LOG_TAG = "CalllogDetailActivity";
	
	public static final String INTENT_EXTRA_NUMBER = "number";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calllog_detail);//实例化一个联系人的详细通话记录的视图
		//使屏幕从右边滑入，动画
//		overridePendingTransition(R.anim.anim_activity_enter_to_left, R.anim.anim_activity_exit_to_left);
		Intent intent = getIntent();//Return the intent that started this activity. 
		String number = intent.getStringExtra(INTENT_EXTRA_NUMBER);//Retrieve extended data from the intent.
		if (number==null){
			//TODO add 404 page?
			return ;
		}
		initCompoments(number);
	}
	
	@SuppressWarnings("deprecation")
	private void initCompoments(final String number){
		//初始化菜单栏
//		ImageButton img_back = (ImageButton) findViewById(R.id.calllog_detail_back);
//		img_back.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				finish();
//				overridePendingTransition(R.anim.anim_activity_enter_to_right, 
//						R.anim.anim_activity_exit_to_right);
//			}
//		});
		
		//初始化头像
		RelativeLayout rl_avatar = (RelativeLayout) findViewById(R.id.calllog_detail_item_photo_frame);
		TextView tv_avatar = (TextView) findViewById(R.id.calllog_detail_item_photo_smart);
		ContactReader reader = ContactReader.getInstance(this);
		Contact contact = reader.getContactByNumber(number);
		Bitmap bitmap = reader.getContactPhoto(contact.getId());
//		if (bitmap!=null){
//			Drawable avatar = new BitmapDrawable(getResources(), bitmap);
//			rl_avatar.setBackgroundDrawable(avatar);
//		}
//		else{
//			rl_avatar.setBackgroundColor(getResources().getColor(R.color.contact_photo_backgroud));
//			String lastname = contact.getName().trim();
//			//找出名字中最后一个汉字
//			for (int i=lastname.length()-1;i>=0; i--){
//				if (ChineseUtil.isChinese(lastname.charAt(i))){
//					lastname = lastname.charAt(i)+"";
//					break;
//				}
//			}
//			tv_avatar.setText(lastname);
//		}
		//显示其他组件
		TextView tv_name = (TextView) findViewById(R.id.calllog_detail_item_name);
		TextView tv_number = (TextView) findViewById(R.id.calllog_detail_item_number);
		tv_name.setText(contact.getName());
		tv_number.setText(number);
		CallLogManager manager = new CallLogManager(this);
		ArrayList<CallRecord> list = manager.getSpecificNumberCallRecords(number);
		DetailAdapter adapter = new DetailAdapter(this, list);
		ListView listView = (ListView) findViewById(R.id.calllog_detail_item_listview);
		TextView tv_header = new TextView(this);
		tv_header.setText("共有"+list.size()+"条通话记录");
		listView.addHeaderView(tv_header);
		listView.setAdapter(adapter);
		RelativeLayout tv_call = (RelativeLayout) findViewById(R.id.calllog_detail_item_call);
		final Context context = this;
		tv_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PhoneCaller.makePhoneCall(context, number);
			}
		});
		RelativeLayout tv_msg = (RelativeLayout)findViewById(R.id.calllog_detail_item_msg);
		tv_msg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO sending message
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
					img_type.setImageResource(R.drawable.ic_calllog_incomming_normal);
					break;
				case CALL_MISSED:
					img_type.setImageResource(R.drawable.ic_calllog_missed_normal);
					break;
				case CALL_OUTGOING:
					img_type.setImageResource(R.drawable.ic_calllog_outgoing_nomal);
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
}