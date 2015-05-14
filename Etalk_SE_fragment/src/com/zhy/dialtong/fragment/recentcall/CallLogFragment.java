/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.recentcall;

import com.zhy.dialtong.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CallLogFragment extends Activity {
	
	private ListView listview;
	private CalllogAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calllog_view);
		retrieveWidgets();//实例化通话记录的部件
		CallLogManager reader = new CallLogManager(this);
		adapter = new CalllogAdapter(this, reader.getAllCallRecords());//获取系统所有的通话记录。
		listview.setAdapter(adapter);
	}
	
	
	private void retrieveWidgets(){
		listview = (ListView) findViewById(R.id.callrecordlist);
	}
	
}
