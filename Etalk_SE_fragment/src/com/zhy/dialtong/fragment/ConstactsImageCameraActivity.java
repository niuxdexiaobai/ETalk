package com.zhy.dialtong.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.zhy.dialtong.SingleFragmentActivity;
import com.zhy.dialtong.fragment.contacts.ContactsImageCameraFragment;


public class ConstactsImageCameraActivity extends SingleFragmentActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment createFragment() {
		return new ContactsImageCameraFragment();
	}

}
