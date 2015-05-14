package com.zhy.dialtong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.zhy.dialtong.R;

public class RecentCallFavorActivity extends Activity{
	
	private LayoutInflater inflater;
	private ListView favorcallrecordlist;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calllog_view);
		
		favorcallrecordlist = (ListView) findViewById(R.id.callrecordlist);
	}

}
