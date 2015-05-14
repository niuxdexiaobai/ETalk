package com.zhy.dialtong.view;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.ContactsActivity;
import com.zhy.dialtong.fragment.ContactsFavorActivity;

@SuppressWarnings("deprecation")
public class ContactsTabHostActivity extends TabActivity implements OnClickListener{
	
	private TabHost tabHost;
	private LinearLayout callrecords;
	private LinearLayout favoritesrecords;
	
	private TextView tv_callrecords_btn;
	private TextView tv_favoritesrecords_btn;
	private TextView tv_contacts_add;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_page);
		init();
	}
	
	private void init(){
		tabHost=getTabHost();
		
		Intent callrecords_btn = new Intent(this,ContactsActivity.class);
		Intent favoritesrecords_btn = new Intent(this,ContactsFavorActivity.class);
		
		callrecords=(LinearLayout) findViewById(R.id.callrecords);
		callrecords.setOnClickListener(this);
		
		favoritesrecords=(LinearLayout) findViewById(R.id.favoritesrecords);
		favoritesrecords.setOnClickListener(this);
		
		tv_callrecords_btn = (TextView) findViewById(R.id.callrecords_btn);
		tv_favoritesrecords_btn = (TextView) findViewById(R.id.favoritesrecords_btn);
		
//		tv_contacts_add = (TextView) findViewById(R.id.contacts_add);
//		tv_contacts_add.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
//				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
//				startActivityForResult(intent, 1008);
//			}
//		});
		
		callrecords.setBackgroundResource(R.drawable.rect_gray_left_checked);
		tv_callrecords_btn.setTextColor(getResources().getColor(R.color.Green));
		
		tabHost.addTab(tabHost.newTabSpec("contacts")
			      .setIndicator(getResources().getString(R.string.tv_contacts_btn), null)
			      .setContent(callrecords_btn));
		tabHost.addTab(tabHost.newTabSpec("favorcontact")
			      .setIndicator(getResources().getString(R.string.tv_favoritescontacts_btn), null)
			      .setContent(favoritesrecords_btn));
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		changeBg(v.getId());
		
		switch (v.getId()) {
		case R.id.callrecords:
			this.tabHost.setCurrentTabByTag("contacts");
			break;
		case R.id.favoritesrecords:
			this.tabHost.setCurrentTabByTag("favorcontact");
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(1008 == requestCode){
			ContactsActivity.init();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected void onDestroy() {
		super.onDestroy();
//		stopReceiver1();
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, PROFILE_SETTING, 0, getString(R.string.profile_setting)).setIcon(R.drawable.settings_48px_16);
//		return true;
//	}
	
//	private String ACTION1 = "SET_DEFAULT_SIG";
//	private ContactsTabHostActivity.BaseReceiver1 receiver1 = null;
//	/**
//	 * �򿪽�����
//	 */
//	private void startReceiver1() {
//		if(null==receiver1){
//			IntentFilter localIntentFilter = new IntentFilter(ACTION1);
//			receiver1 = new ContactsTabHostActivity.BaseReceiver1();
//			this.registerReceiver(receiver1, localIntentFilter);
//		}
//	}
//	/**
//	 * �رս�����
//	 */
//	private void stopReceiver1() {
//		if (null != receiver1)
//			unregisterReceiver(receiver1);
//	}
//	public class BaseReceiver1 extends BroadcastReceiver {
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(ACTION1)) {
//
//				String str_bean = intent.getStringExtra("groupbean");
//				Gson gson = new Gson();
//				GroupBean gb = gson.fromJson(str_bean, GroupBean.class);
//				if(gb.getId() == 0){
//
//					init();
//				}else{
//
//					queryGroupMember(gb);
//				}
//			}
//		}
	


}
