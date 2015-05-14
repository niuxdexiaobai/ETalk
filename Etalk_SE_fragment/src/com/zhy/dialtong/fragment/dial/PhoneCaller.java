/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.dial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * 负责呼出电话的操作。
 * <p class="note"><strong>注意：</strong>
 * 		使用该类需要声明{@link android.Manifest.permission#CALL_PHONE CALL_PHONE}权限。
 * </p>
 * @author Muyangmin
 * @create 2014-7-30
 * @version 1.0
 */
/*
 * 采用策略模式设计，目前定义两种拨打电话的方式，但暂时仅使用系统自带方式。
 */
public final class PhoneCaller {
	
	private static final String LOG_TAG="CallPhone";
	
	private static Call caller = new SystemCall();
	
	static{
		caller  = new SystemCall();
		Log.i(LOG_TAG, "Phone Caller using System call.");
	}
	
	/**
	 * 拨打电话给指定号码。
	 * @param number 要拨打的号码。如果为null,则什么也不做
	 * @lastModify 2014-7-31 by Muyangmin
	 */
	public static void makePhoneCall(Context context, String number){
		if (context==null || number==null || number.length()==0){
			Log.v(LOG_TAG, "invalid parameters to make call.");
			return ;
		}
		caller.callPhone(context, number);
	}
	
	/**
	 * 拨打电话的接口。
	 * @author Muyangmin
	 * @create 2014-7-31
	 * @version 1.0
	 */
	private interface Call{
		void callPhone(Context context, String number);
	}

	/**
	 * 使用系统自带的界面（即使用默认Intent）拨打电话。
	 * @author Muyangmin
	 * @create 2014-7-31
	 * @version 1.0
	 */
	static final class SystemCall implements Call{

		@Override
		public void callPhone(Context context, String number) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
			context.startActivity(intent);
		}
		
	}

	/**
	 * 利用反射方式拨打电话[因目前未找到反射挂断电话的方法，该类暂不实现]。
	 * @author Muyangmin
	 * @create 2014-7-31
	 * @version 1.0
	 */
	static final class ReflectionCall implements Call{

		@Override
		public void callPhone(Context context, String number) {
			
		}
		
	}
}

