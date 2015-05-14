/*package com.zhy.dialtong.view;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhy.dialtong.LogLevel;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.DialsFragment;
import com.zhy.dialtong.fragment.contacts.ContactsFragment;
import com.zhy.dialtong.fragment.dial.MyApplication;
import com.zhy.dialtong.utils.widgets.FixedSpeedScroller;
import com.zhy.dialtong.utils.widgets.HomeBottomButton;

*//**
 * HomeFragmentActivity
 * @author gavin.zhuang 
 * 
 *//*
public class HomeFragmentActivity extends QuickCallFragmentActivity {
	
	private static final String TAG = "[ZHANG]HomeFragmentActivity";

	private static final int PAGE_DIAL_CALL  = 0;
	private static final int PAGE_CALL_LOG  = 1;
	private static final int PAGE_CONTACTS  = 2;
	
	private static final int PAGER_PAGES = 3;
	
	private DialsFragment mDialFragment;
	private CallLogFragment mCallLogFragment;
	private ContactsFragment mContactsFragment;
	
	private ViewPager mViewPager;
	private HomeBottomButton mButtonQuickCall;
    private HomeBottomButton mButtonCallLog;
    private HomeBottomButton mButtonContacts;
    private HomeBottomButton mButtonSettings;
    private TextView mTitleView;
	
	private ArrayList<Fragment> mFragmentsList;
    
    private int mCurrentPage = PAGE_DIAL_CALL;
	
	private static Object mSync = new Object();
    private static boolean isAlive = false;
    
    
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
		synchronized (mSync) {
			isAlive = true;
		}
		
		this.setContentView(R.layout.home_fragment_layout);
		
		buildLayout();
		listernButton();
	}
	
	public static boolean isAlive(){
    	synchronized (mSync) {
    		return isAlive;
		}    	
    }
	
	@Override
	public void onResume(){
		super.onResume();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
		StatService.onResume(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onStart...");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
		StatService.onPause(this);
	}
	
	@Override
	public void finish(){
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left, R.anim.back_out_right);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		
		synchronized (mSync) {
        	isAlive = false;
		}  
		
		super.leakCleanUpRootView();
	}
	
	private void buildLayout(){
		
		
    	mButtonCallLog = (HomeBottomButton) this.findViewById(R.id.home_bottom_button_calllog);
        mButtonContacts = (HomeBottomButton) this.findViewById(R.id.home_bottom_button_contacts);
        mButtonSettings = (HomeBottomButton) this.findViewById(R.id.home_bottom_button_settings);
        
		mViewPager = (ViewPager) this.findViewById(R.id.home_view_pager);
		try {  
            Field mScroller = null;  
            mScroller = ViewPager.class.getDeclaredField("mScroller");  
            mScroller.setAccessible(true);   
            FixedSpeedScroller scroller = new FixedSpeedScroller( mViewPager.getContext( ), 500);
            mScroller.set( mViewPager, scroller);  
        }catch(Exception e){
        	if(LogLevel.MARKET){
        		MarketLog.e(TAG, "Set viewpager scoller failed. e = " + e.toString());
        	}
        }
		
		initViewPager();
	}
	
	private void listernButton(){
		
		this.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mButtonQuickCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "User select quick call screen.");
				}
				
				if(mCurrentPage == PAGE_DIAL_CALL){
					return;
				}
				
				mViewPager.setCurrentItem(PAGE_DIAL_CALL);
				
			}
		});

		mButtonCallLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "User select discover screen.");
				}
				
				if(mCurrentPage == PAGE_CALL_LOG){
					return;
				}
				
				mViewPager.setCurrentItem(PAGE_CALL_LOG);
				
			}
		});
		
		mButtonContacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "User select contacts screen.");
				}
				
				if(mCurrentPage == PAGE_CONTACTS){
					return;
				}
				
				mViewPager.setCurrentItem(PAGE_CONTACTS);
				
			}
		});

	}
	
	private void initViewPager(){
        mFragmentsList = new ArrayList<Fragment>();

        mDialFragment = new DialsFragment();
        mCallLogFragment = new CallLogFragment();
        mContactsFragment = new ContactsFragment();

        mFragmentsList.add(mDialFragment);
        mFragmentsList.add(mCallLogFragment);
        mFragmentsList.add(mContactsFragment);
        
        mViewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager(), mFragmentsList));
        mViewPager.setOnPageChangeListener(new HomeOnPageChangeListener());
        mViewPager.setCurrentItem(PAGE_DIAL_CALL);
        mViewPager.setOffscreenPageLimit(PAGER_PAGES);
        mButtonQuickCall.setSelected(true);
	}
	
	private class HomeOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int index) {
            switch (index) {
            case PAGE_DIAL_CALL:
            	mButtonQuickCall.setSelected(true);
            	mButtonCallLog.setSelected(false);
                mButtonContacts.setSelected(false);
                mButtonSettings.setSelected(false);
                mTitleView.setText(R.string.home_bottom_quick_call);
                break;
            case PAGE_CALL_LOG:
            	mButtonQuickCall.setSelected(false);
            	mButtonCallLog.setSelected(true);
                mButtonContacts.setSelected(false);
                mButtonSettings.setSelected(false);
                mTitleView.setText(R.string.home_bottom_call_log);
                break;
            case PAGE_CONTACTS:
            	mButtonQuickCall.setSelected(false);
            	mButtonCallLog.setSelected(false);
                mButtonContacts.setSelected(true);
                mButtonSettings.setSelected(false);
                mTitleView.setText(R.string.home_bottom_contacts);
                break;
			default:
				break;
            }
            
            mCurrentPage = index;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
	
}
*/