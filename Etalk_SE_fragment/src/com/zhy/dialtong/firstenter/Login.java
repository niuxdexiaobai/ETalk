package com.zhy.dialtong.firstenter;

import com.zhy.dialtong.R;
import com.zhy.dialtong.view.FrameActivity;
import com.zhy.dialtong.view.GuideScreenActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Login extends Activity{
	private TextView Registration_OK;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup);
		init();
		
	}

	private void init() {
		Registration_OK = (TextView) findViewById(R.id.Registration_OK);
		Registration_OK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Login.this, FrameActivity.class);
				Login.this.startActivity(i);
				Login.this.finish();
				Login.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		
	}

}
