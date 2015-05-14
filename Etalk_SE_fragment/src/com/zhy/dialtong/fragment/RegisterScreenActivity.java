package com.zhy.dialtong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zhy.dialtong.R;
import com.zhy.dialtong.view.FrameActivity;

public class RegisterScreenActivity extends Activity{
	private EditText account,password;
	private Button login;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		mContext=this;
		initView();
		
	}
	private void initView(){
		account =(EditText) findViewById(R.id.account);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, FrameActivity.class);
				startActivity(intent);
			}
		});
	}

}
