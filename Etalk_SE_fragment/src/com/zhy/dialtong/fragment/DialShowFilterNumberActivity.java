package com.zhy.dialtong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.dial.MyApplication;
import com.zhy.dialtong.fragment.dial.T9Adapter;

public class DialShowFilterNumberActivity extends Activity{
	
	private ListView show_filter_number;
	private TextView show_number;
	private LinearLayout filterLinearLayout;
	
	private T9Adapter t9Adapter;
	private MyApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dial_show_filter_number);
		
		application = (MyApplication)getApplication();
		
		show_number = (TextView) findViewById(R.id.show_number);
		String number = getIntent().getStringExtra("photonumber");
		show_number.setText(number);
		
		show_filter_number = (ListView) findViewById(R.id.show_filter_number);
		show_filter_number.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				if (show_number.getText().toString().length() >= 4) {
//					call(show_number.getText().toString());
//				}
			}
		});
		
		
//		if(null == number){
//			show_filter_number.setVisibility(View.INVISIBLE);
//		}else{
		if(null == application.getContactBeanList() || application.getContactBeanList().size()<0 || "".equals(show_number.getText().toString())){
			show_filter_number.setVisibility(View.INVISIBLE);
		}else{
			if(null == t9Adapter){
				t9Adapter = new T9Adapter(DialShowFilterNumberActivity.this);
				t9Adapter.assignment(application.getContactBeanList());
				show_filter_number.setAdapter(t9Adapter);
				show_filter_number.setTextFilterEnabled(true);
				t9Adapter.getFilter().filter(number);
			}
			else{
				show_filter_number.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
//	private void call(String phone) {
//		Uri uri = Uri.parse("tel:" + phone);
//		Intent it = new Intent(Intent.ACTION_CALL, uri);
//		startActivity(it);
//	}

}
