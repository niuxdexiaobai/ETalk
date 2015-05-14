package com.zhy.dialtong.view;

import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.RecentCallFavorActivity;
import com.zhy.dialtong.fragment.RecentCallFragment;
import com.zhy.dialtong.fragment.recentcall.CallLogFragment;

public class RecentCallTabHostActivity extends TabActivity implements OnClickListener{
	
	private TabHost tabHost;
	private LinearLayout callrecords;
	private LinearLayout favoritesrecords;
	
	private TextView tv_callrecords_btn;
	private TextView tv_favoritesrecords_btn;
	private TextView tv_record_del;
	
	private Context context;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recent_call_record_page);
		
		init();
	}
	
	@SuppressWarnings("deprecation")
	private void init(){
		
		tabHost=getTabHost();
		
		Intent callrecords_btn = new Intent(this,CallLogFragment.class);// RecentCallFragment
		Intent favoritesrecords_btn = new Intent(this,RecentCallFavorActivity.class);
		
		callrecords=(LinearLayout) findViewById(R.id.callrecords);
		callrecords.setOnClickListener(this);
		
		favoritesrecords=(LinearLayout) findViewById(R.id.favoritesrecords);
		favoritesrecords.setOnClickListener(this);
		
		tv_callrecords_btn = (TextView) findViewById(R.id.callrecords_btn);
		tv_favoritesrecords_btn = (TextView) findViewById(R.id.favoritesrecords_btn);
		
//		tv_record_del = (TextView) findViewById(R.id.recentcall_del);
//		tv_record_del.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				DeleteCallLogByNumber();
//				context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,null,null);
//				Uri uri = Uri.parse("content://call_log/calls");
//				int d = getContentResolver().delete(uri, null, null);
//				ContentResolver resolver = getContentResolver();
//				resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
//				if (v.getId() == R.id.recentcall_del) {
//					// Log delete
//					Context context = getApplicationContext();
//					CharSequence text = "ͨ����¼��ɾ��";//Call Log successfully deleted!!!
//					int duration = Toast.LENGTH_SHORT;
//
//					Toast toast = Toast.makeText(context, text, duration);
//					toast.show();
//
//				} else {
//					// Log not Delete
//					Context context = getApplicationContext();
//					CharSequence text = "ͨ����¼ɾ��ʧ��";//Call Log NOT deleted!!!
//					int duration = Toast.LENGTH_LONG;
//
//					Toast toast = Toast.makeText(context, text, duration);
//					toast.show();
//
//				}
//			}
//		});
		
		callrecords.setBackgroundResource(R.drawable.rect_gray_left_checked);
		tv_callrecords_btn.setTextColor(getResources().getColor(R.color.Green));
		
		tabHost.addTab(tabHost.newTabSpec("callrecords")
			      .setIndicator(getResources().getString(R.string.tv_callrecords_btn), null)
			      .setContent(callrecords_btn));
		tabHost.addTab(tabHost.newTabSpec("favoritesrecords")
			      .setIndicator(getResources().getString(R.string.tv_favoritesrecords_btn), null)
			      .setContent(favoritesrecords_btn));
		
	}

	public void DeleteCallLogByNumber() {
		// TODO Auto-generated method stub
//		String queryString="NUMBER="+number; 
//	    this.getContentResolver().delete(CallLog.Calls.CONTENT_URI,queryString,null);
//		context.getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI, null, null);
		//java.lang.NullPointerException

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		changeBg(v.getId());
		
		switch (v.getId()) {
		case R.id.callrecords:
			this.tabHost.setCurrentTabByTag("callrecords");
			break;
		case R.id.favoritesrecords:
			this.tabHost.setCurrentTabByTag("favoritesrecords");
			break;
		}
		
	}
	
	private void changeBg(int id){
		initBgColor();
		
		if(id==R.id.callrecords){
			findViewById(id).setBackgroundResource(R.drawable.rect_gray_left_checked);
			tv_callrecords_btn.setTextColor(getResources().getColor(R.color.Green));
		}else if(id==R.id.favoritesrecords){
			findViewById(id).setBackgroundResource(R.drawable.rect_gray_right_checked);
			tv_favoritesrecords_btn.setTextColor(getResources().getColor(R.color.Green));
		}
	}
	
	private void initBgColor(){
		findViewById(R.id.callrecords).setBackgroundResource(R.drawable.rect_gray_left);
		findViewById(R.id.favoritesrecords).setBackgroundResource(R.drawable.rect_gray_center);
		
		tv_callrecords_btn.setTextColor(getResources().getColor(R.color.gray));
		tv_favoritesrecords_btn.setTextColor(getResources().getColor(R.color.gray));
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return super.onTouchEvent(event);
	}

}
