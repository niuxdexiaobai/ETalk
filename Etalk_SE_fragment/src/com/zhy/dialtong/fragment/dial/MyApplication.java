package com.zhy.dialtong.fragment.dial;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.yulore.superyellowpage.app.ApplicationMap;
import com.yulore.superyellowpage.impl.YulorePageConfiguration;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.LogLevel;

public class MyApplication extends android.app.Application{
	
private static final String TAG = "[ZHUANG]QuickCallApp";
	
	private static Context sApplicationContext = null;
	
	private List<ContactBean> contactBeanList;//
	
	public List<ContactBean> getContactBeanList() {//
		return contactBeanList;
	}
	public void setContactBeanList(List<ContactBean> contactBeanList) {//
		this.contactBeanList = contactBeanList;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("项目启动");
		
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
		
		sApplicationContext = getApplicationContext();
	}
	
	@Override
    public void onLowMemory(){
    	super.onLowMemory();
    	   MarketLog.w(TAG, "onLowMemory:");
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        
        if (LogLevel.MARKET) {
            MarketLog.w(TAG, "onTerminate: " );
        }        
        
        sApplicationContext = null;
    }
    
    public static Context getContextQuickCall(){    	
        return sApplicationContext;
    }

}
