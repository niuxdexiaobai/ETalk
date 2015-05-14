package com.zhy.dialtong.fragment;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.recentcall.CallLogBean;
import com.zhy.dialtong.fragment.recentcall.CalllogAdapter;
import com.zhy.dialtong.fragment.recentcall.HomeDialAdapter;

public class RecentCallFragment extends FragmentActivity{
	
	private Context context;
	private AsyncQueryHandler asyncQuery;
	private HomeDialAdapter adapter;
	private List<CallLogBean> list;
	private ListView callrecordlist;
	private CalllogAdapter adapter1;/**/
	
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calllog_view);
		callrecordlist = (ListView) findViewById(R.id.callrecordlist);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();
		
	}
	
	
	private void init(){
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = { 
				CallLog.Calls.DATE,
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls._ID,
		}; 
		asyncQuery.startQuery(0, null, uri, projection, null, null, Calls.DEFAULT_SORT_ORDER);  
	}
	
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
					String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
					int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//�����������绰���룬������Ĵ���
//					String count = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
					int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
//					String timeContact = cursor.getString(7);

					CallLogBean clb = new CallLogBean();
					clb.setId(id);
					clb.setNumber(number);
					clb.setName(cachedName);
					if(null == cachedName || "".equals(cachedName)){
						clb.setName(number);
					}
					clb.setType(type);
//					clb.setCount(count);
//					clb.setCount(timeContact);
					clb.setDate(sfd.format(date));
					
					list.add(clb);
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}
	
	private void setAdapter(List<CallLogBean> list) {
		adapter = new HomeDialAdapter(this, list);
//		TextView tv = new TextView(this);
//		tv.setBackgroundResource(R.drawable.dial_input_bg2);
//		callLogList.addFooterView(tv);
		callrecordlist.setAdapter(adapter);
		callrecordlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Uri uri = Uri.parse("tel:" + list.getNumber());
//				Intent it = new Intent(Intent.ACTION_CALL, uri);
//				ctx.startActivity(it);
//				Intent phoneIntent = new Intent("android.intent.action.CALL",
//						Uri.parse("tel:" + adapter.getNumber()));
//				context.startActivity(phoneIntent);
//				context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,null,null);
			}
		});
	}
	


}
