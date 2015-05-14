package com.zhy.dialtong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.zhy.dialtong.R;

public class MoreWifiSettingPage extends Activity{
	
	private Button listButton,backtosys_wifi_settingButton;
	private Button startButton=null;    
	private Button stopButton=null;    
	private Button checkButton=null;    
	WifiManager wifiManager=null;    
	     /** Called when the activity is first created. */    
	     @Override    
	     public void onCreate(Bundle savedInstanceState) {    
	         super.onCreate(savedInstanceState);
	         requestWindowFeature(Window.FEATURE_NO_TITLE);
	         setContentView(R.layout.more_wifi_setting_page);    
	         startButton=(Button)findViewById(R.id.startButton);    
	         stopButton=(Button)findViewById(R.id.stopButton);    
	         checkButton=(Button)findViewById(R.id.checkButton); 
	         listButton = (Button) findViewById(R.id.listButton);
	         backtosys_wifi_settingButton = (Button) findViewById(R.id.backtosys_wifi_settingButton);
	         startButton.setOnClickListener(new startButtonListener());    
	         stopButton.setOnClickListener(new stopButtonListener());    
	         checkButton.setOnClickListener(new checkButtonListener());   
	         listButton.setOnClickListener(new listButtonListener());
	         backtosys_wifi_settingButton.setOnClickListener(new backtosys_wifi_settingButtonListener());
	     }    
	     class startButtonListener implements OnClickListener    
	     {    
	     
	         /* (non-Javadoc)  
	          * @see android.view.View.OnClickListener#onClick(android.view.View)  
	          */    

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wifiManager=(WifiManager)MoreWifiSettingPage.this.getSystemService(Context.WIFI_SERVICE);    
	             wifiManager.setWifiEnabled(true);    
	             System.out.println("wifi state --->"+wifiManager.getWifiState());    
	             Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ����"/*+wifiManager.getWifiState()*/, Toast.LENGTH_SHORT).show();    
				
			}    
	             
	     }    
	     class stopButtonListener implements OnClickListener    
	     {    
	     
	         /* (non-Javadoc)  
	          * @see android.view.View.OnClickListener#onClick(android.view.View)  
	          */    
	         @Override    
	         public void onClick(View v) {    
	             // TODO Auto-generated method stub    
	             wifiManager=(WifiManager)MoreWifiSettingPage.this.getSystemService(Context.WIFI_SERVICE);    
	             wifiManager.setWifiEnabled(false);    
	             System.out.println("wifi state --->"+wifiManager.getWifiState());    
	             Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ����"/*+wifiManager.getWifiState()*/, Toast.LENGTH_SHORT).show();    
	         }    
	             
	     }    
	     class checkButtonListener implements OnClickListener    
	     {    
	     
	         /* (non-Javadoc)  
	          * @see android.view.View.OnClickListener#onClick(android.view.View)  
	          */    
	         @Override    
	         public void onClick(View v) {    
	             // TODO Auto-generated method stub    
	             wifiManager=(WifiManager)MoreWifiSettingPage.this.getSystemService(Context.WIFI_SERVICE);              
	             System.out.println("wifi state --->"+wifiManager.getWifiState());    
	             //Toast.makeText(MoreWifiSettingPage.this, "��ǰ����״̬Ϊ��"+wifiManager.getWifiState(),Toast.LENGTH_SHORT).show();
	             if (wifiManager.getWifiState() == 0) {
	            	 Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ�����ڹر�",
	            			 Toast.LENGTH_SHORT).show();} 
	             else if (wifiManager.getWifiState() == 1){
	            	 Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ��������",Toast.LENGTH_SHORT).show();}
	             else if (wifiManager.getWifiState() == 2){
	            	 Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ�����ڴ�",Toast.LENGTH_SHORT).show();}
	             else if (wifiManager.getWifiState() == 3){
	            	 Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ������",Toast.LENGTH_SHORT).show();}
	             else {
	            	 Toast.makeText(MoreWifiSettingPage.this, "��ǰWiFi״̬Ϊ��������δ֪״̬",Toast.LENGTH_SHORT).show();}
	             }
	         }  
	     
	     class listButtonListener implements OnClickListener    
	     {    
	    	 /* (non-Javadoc)  
	          * @see android.view.View.OnClickListener#onClick(android.view.View)  
	          */    
	         @Override    
	         public void onClick(View v) {    
	             // TODO Auto-generated method stub
	        	 startActivity(new Intent(MoreWifiSettingPage.this, WifiListActivity.class));
	     		overridePendingTransition(android.R.anim.slide_in_left,
	     				android.R.anim.slide_out_right);
	         }
	     }
	     
	     class backtosys_wifi_settingButtonListener implements OnClickListener    
	     {    
	    	 /* (non-Javadoc)  
	          * @see android.view.View.OnClickListener#onClick(android.view.View)  
	          */    
	         @Override    
	         public void onClick(View v) {    
	             // TODO Auto-generated method stub
	        	 Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);//��ϵͳWiFi����ҳ��
				 startActivity(intent);
	         }
	     }
}  


