/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhy.dialtong.utils.widgets;

import com.zhy.dialtong.fragment.dial.MyApplication;


public class DensityUtils {
	
	public static int dp_px(float dpValue) {  
        final float scale = MyApplication.getContextQuickCall().getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
	
	public static int px_dp(float pxValue) {  
        final float scale = MyApplication.getContextQuickCall().getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 

}
