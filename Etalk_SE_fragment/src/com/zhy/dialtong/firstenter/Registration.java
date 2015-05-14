package com.zhy.dialtong.firstenter;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.zhy.dialtong.R;
import com.zhy.dialtong.view.FrameActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Registration extends Activity implements OnClickListener{
	
	protected static final int SHOW_RESPONSE = 0;
	protected static final int REJECT_RESPONSE = 1;
	protected static final int WRONG_NUM = 2;
	private EditText mRegistration_phone,mRegistration_verifycode,mRegistration_password;
	private TextView mRegistration_OK;
	private Button btn_verifycode;
	
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			//在这里进行UI操作，将结果显示到界面上
			case SHOW_RESPONSE :
				Toast.makeText(Registration.this, "注册成功", Toast.LENGTH_SHORT).show();
				break;
			case REJECT_RESPONSE :
				Toast.makeText(Registration.this, "注册失败", Toast.LENGTH_SHORT).show();
				break;
			case WRONG_NUM :
				Toast.makeText(Registration.this, "手机号码有误，请重新输入", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};
	
	public static boolean isMobileNO(String mobiles){    
		  
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");    
		  
		Matcher m = p.matcher(mobiles);    
		  
		return m.matches();    
		  
	}  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registration);
        initView();
        Log.d("Create", "msgTEST");
    }


    private void initView() {
		// TODO Auto-generated method stub
    	mRegistration_phone = (EditText) findViewById(R.id.Registration_phone);
    	mRegistration_verifycode = (EditText) findViewById(R.id.Registration_verifycode);
    	btn_verifycode = (Button) findViewById(R.id.btn3);
    	btn_verifycode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int numlength = mRegistration_phone.getText().toString().length();
				String phonenum = mRegistration_phone.getText().toString();
				if (numlength==11){
					if (isMobileNO(phonenum)){
//				sendRequestWithHttpClient();
//				Intent intent = new Intent(MainActivity.this, secondActivity.class);
//				startActivity(intent);
						
					}
					else {
						Toast.makeText(Registration.this, "手机号码有误，请重新输入", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(Registration.this, "手机号码有误，请重新输入", Toast.LENGTH_SHORT).show();
				}
			}
		}); 
    	mRegistration_password = (EditText) findViewById(R.id.Registration_password);
    	mRegistration_OK = (TextView) findViewById(R.id.Registration_OK);
    	mRegistration_OK.setOnClickListener(this); 
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn3){
			
			
		}
		if (v.getId() == R.id.Registration_OK) {
			Log.d("Create", "msgTEST");
			Intent i = new Intent(Registration.this, Login.class);
			Registration.this.startActivity(i);
			Registration.this.finish();
			Registration.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	
	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//第一步：创建HttpClient对象
					HttpClient httpClient = new DefaultHttpClient();
				try {
					//第二步：创建代表请求的对象,参数是访问的服务器地址
					HttpPost httpPost = new HttpPost("http://60.12.105.148:8080/external/server/CreateActivePhoneCard");
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
					// 先封装一个 JSON 对象
					JSONObject jsonObject = new JSONObject();  
					jsonObject.put("pin", "testapp_5"); 
					jsonObject.put("account", "Android_App");
//					jsonObject.put("feeRateGroup","10f/60s"); 
//					jsonObject.put("type", 1); /* */
					/*jsonObject.put("account","20150330"); 
					jsonObject.put("displayNumber", "20150330"); 
					jsonObject.put("password","123456"); 
					jsonObject.put("e164","20150330"); 
					jsonObject.put("feerateGroup","10f/60s"); 
					jsonObject.put("autoCreateAccount",true);  */
//					params.add(new BasicNameValuePair("autoCreateAccount","true"));
//					params.add(new BasicNameValuePair("e164","88888888"));
//					params.add(new BasicNameValuePair("account","88888888"));
//					params.add(new BasicNameValuePair("password","123456"));
//					params.add(new BasicNameValuePair("jsonString", jsonObject.toString()));
						Log.d("Create", jsonObject.toString());
//					httpPost.setEntity(new UrlEncodedFormEntity(params));
					// 绑定到请求 Entry 
					StringEntity se = new StringEntity(jsonObject.toString()); 
					httpPost.setEntity(se);  
//					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("Content-type", "text/html;charset=UTF-8");
//					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"utf-8");
//					httpPost.setEntity(entity);
					// 第三步：执行请求，获取服务器发还的相应对象
					HttpResponse httpResponse = httpClient.execute(httpPost);
						Log.d("Create", "Send");
						Log.d("Create", "GetResponse"+ httpResponse.getStatusLine().getStatusCode());
						//第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							Log.d("Create", "Response"+ 200);
							//第五步：从相应对象当中取出数据，放到entity当中
						HttpEntity entity2 = httpResponse.getEntity();
							Log.d("Create", "Response"+ entity2);
//							String response = null;
//						if(entity2 != null)
//		                {
							String response = EntityUtils.toString(entity2,"utf-8");//将entity当中的数据转换为字符串
//		                }
						//在子线程中将Message对象发出去
							Log.d("Create", "Response"+ response);
						 Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = response.toString();
						handler.sendMessage(message);
					} else {
						 Message message = new Message();
							message.what = REJECT_RESPONSE;
							handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}


