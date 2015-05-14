package com.zhy.dialtong.utils.widgets;

import com.zhuang.quickcall.logging.MarketLog;
import com.zhy.dialtong.LogLevel;

import android.app.Application;
import android.content.Context;

public class QuickCallApp  extends Application{

	private static final String TAG = "[ZHUANG]QuickCallApp";
	
	private static Context sApplicationContext = null;

	@Override
    public void onCreate() {
        super.onCreate();        
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
