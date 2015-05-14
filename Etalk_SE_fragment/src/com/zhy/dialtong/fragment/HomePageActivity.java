package com.zhy.dialtong.fragment;


import com.zhy.dialtong.QuickCallConstants;
import com.zhy.dialtong.R;
import com.zhy.dialtong.view.FrameActivity;
import com.zhy.dialtong.view.GuideScreenActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class HomePageActivity extends Activity{
	private static final int SPLASH_SHOW_TIME = 1000;
	private boolean mIsFirstOpen = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash_screen_layout);
		
		mIsFirstOpen = false;
		SharedPreferences setting = getSharedPreferences(QuickCallConstants.QC_SHARED_PREFERENCES_CONSTANTS, 0);  
		Boolean user_first = setting.getBoolean(QuickCallConstants.QC_IS_FIRST_OPEN, true);
		if (user_first) {
			mIsFirstOpen = true;
			setting.edit().putBoolean(QuickCallConstants.QC_IS_FIRST_OPEN, false).commit();
		}
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				if(mIsFirstOpen){
					startGuideScreen();
				} else {
					startHomeScreen();
				}
			}
			
		}, SPLASH_SHOW_TIME);
		
	}
	
	private void startHomeScreen() {
		Intent intent = new Intent(this, FrameActivity.class);
		this.startActivity(intent);
		this.finish();
		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void startGuideScreen() {
		Intent intent = new Intent(this, GuideScreenActivity.class);
		this.startActivity(intent);
		this.finish();
		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
