package com.zhy.dialtong.fragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.QuickCallConstants;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.ContactsUtils;
import com.zhy.dialtong.fragment.dial.MyApplication;
import com.zhy.dialtong.fragment.dial.T9Adapter;
import com.zhy.dialtong.fragment.recentcall.CallLogBean;
import com.zhy.dialtong.fragment.recentcall.HomeDialAdapter;
import com.zhy.dialtong.sipua.sipua.SipdroidEngine;
import com.zhy.dialtong.sipua.sipua.UserAgent;
import com.zhy.dialtong.sipua.ui.Receiver;
import com.zhy.dialtong.sipua.ui.Settings;
import com.zhy.dialtong.view.FrameActivity;

public class DialFragment extends Activity implements OnClickListener{
	
	public static final boolean release = true;
	public static final boolean market = false;
	
	private AsyncQueryHandler asyncQuery;
	private List<CallLogBean> list;
	private ContactBean contactBean;
	
	private HomeDialAdapter adapter;
	
	private MyApplication application;
	private T9Adapter t9Adapter;
	private ListView show_relate_num;
	
	private ImageButton add,delete,detail_number,dial;
	private EditText show_number;
	private ImageButton one,two,three,four,five,six,seven,eight,nine,zero,poundkey,starkey;
	private ImageView accountsetting;
	
	private AudioManager am = null;
	private SoundPool spool;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	private static AlertDialog m_AlertDlg;
	private View mTipsView;
	
	private PopupWindow mPopupWindow;
	
	@Override
	public void onStart() {
		super.onStart();
		Receiver.engine(this).registerMore();//注册receiver，分配IP
	}
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dial_page_rl);
		
		
		application = (MyApplication)getApplication();
		
		/*mTipsView = this.findViewById(R.id.main_tips_layout);
		mTipsView.setVisibility(View.GONE);
		SharedPreferences setting = getSharedPreferences(QuickCallConstants.QC_SHARED_PREFERENCES_CONSTANTS, 0);  
		Boolean first_create = setting.getBoolean(QuickCallConstants.QC_IS_FIRST_CREATE, true);
		if (first_create) {
			mTipsView.setVisibility(View.VISIBLE);
			setting.edit().putBoolean(QuickCallConstants.QC_IS_FIRST_CREATE, false).commit();
		}
		
		mTipsView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTipsView.setVisibility(View.GONE);
				return false;
			}
		});*/
		
		accountsetting = (ImageView) findViewById(R.id.dialsetting);
		accountsetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mPopupWindow != null) {
					if (mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					} else {
						mPopupWindow.showAsDropDown(v);
					}
				}
			}
		});
		initPopupWindow();
		
		add = (ImageButton) findViewById(R.id.add_to_contacts);
		add.setVisibility(View.INVISIBLE);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
				startActivity(intent);
			}
		});
		
		delete = (ImageButton) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setVisibility(View.INVISIBLE);
		delete.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				show_number.setText("");
				return false;
			}
		});
		
		detail_number = (ImageButton) findViewById(R.id.detail_number);
		detail_number.setVisibility(View.INVISIBLE);
		detail_number.setOnClickListener(new OnClickListener() {//
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(DialFragment.this,DialShowFilterNumberActivity.class);
				intent.putExtra("photonumber", show_number.getText().toString());
				startActivity(intent);
			}
		});
		
		show_relate_num = (ListView) findViewById(R.id.show_relate_num);
		
		
		show_number = (EditText) findViewById(R.id.show_number);
		show_number.setInputType(InputType.TYPE_NULL);
//		show_number.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		show_number.setTextIsSelectable(true);
//		show_number.setCursorVisible(true);
		show_number.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int inType = show_number.getInputType();
				show_number.setInputType(InputType.TYPE_NULL);
				show_number.onTouchEvent(event);
				show_number.setInputType(inType);
//				show_number.setCursorVisible(true);
//				show_number.setFocusable(true);
//				show_number.setCursorVisible(true); 
				return false;
			}
		});
		show_number.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				ContactBean contactBean = new ContactBean();
				if(/*application.getContactBeanList().size()<3){*/null == application.getContactBeanList() || application.getContactBeanList().size()<0 || "".equals(s.toString())){
//					show_relate_num.setVisibility(View.VISIBLE);
					Log.v("TextNotChange", "istrue");
//					show_number.setCursorVisible(true);
					show_relate_num.setVisibility(View.INVISIBLE);
					detail_number.setVisibility(View.INVISIBLE);
					delete.setVisibility(View.INVISIBLE);
					add.setVisibility(View.INVISIBLE);
				}else{
//					if (application.getContactBeanList().size()<3){
//						show_relate_num.setVisibility(View.INVISIBLE);
//					}else {
					if(null == t9Adapter){
//						Log.v("TextChange", "Filling_t9Adapter");
						t9Adapter = new T9Adapter(DialFragment.this);
//						Log.v("TextChange", "Filled_t9Adapter");
						t9Adapter.assignment(application.getContactBeanList());
//						Log.v("TextChange", "get_contactBean");
						show_relate_num.setAdapter(t9Adapter);
//						Log.v("Show_relate_num", "SetAdapter");
						show_relate_num.setTextFilterEnabled(true);
//						Log.v("Show_relate_num", "Enable type filtering");
						detail_number.setVisibility(View.VISIBLE);
						delete.setVisibility(View.VISIBLE);
						add.setVisibility(View.VISIBLE);
//						show_number.setCursorVisible(true);
					}
				else{
//					show_number.setCursorVisible(true);
					show_relate_num.setVisibility(View.VISIBLE);
					detail_number.setVisibility(View.VISIBLE);
					delete.setVisibility(View.VISIBLE);
					add.setVisibility(View.VISIBLE);
					t9Adapter.getFilter().filter(s);
				}
				}
//				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				show_number.setSelection(show_number.getText().length());
			}
		});
		
		on(this,true);
		
		final Context mContext = this;
		
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Settings.PREF_NOPORT, Settings.DEFAULT_NOPORT)) {
			boolean ask = false;
    		for (int i = 0; i < SipdroidEngine.LINES; i++) {
    			String j = (i!=0?""+i:"");//?
    			if (PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_SERVER+j, Settings.DEFAULT_SERVER).equals(Settings.DEFAULT_SERVER)
    					&& PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_USERNAME+j, Settings.DEFAULT_USERNAME).length() != 0 &&
    					PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_PORT+j, Settings.DEFAULT_PORT).equals(Settings.DEFAULT_PORT))
    				ask = true;
    		}
    		if (ask)
			new AlertDialog.Builder(this)
				.setMessage(R.string.dialog_port)
	            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                		Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	                		for (int i = 0; i < SipdroidEngine.LINES; i++) {
	                			String j = (i!=0?""+i:"");//
	                			if (PreferenceManager.getDefaultSharedPreferences(mContext).getString(Settings.PREF_SERVER+j, Settings.DEFAULT_SERVER).equals(Settings.DEFAULT_SERVER)
	                					&& PreferenceManager.getDefaultSharedPreferences(mContext).getString(Settings.PREF_USERNAME+j, Settings.DEFAULT_USERNAME).length() != 0 &&
	                					PreferenceManager.getDefaultSharedPreferences(mContext).getString(Settings.PREF_PORT+j, Settings.DEFAULT_PORT).equals(Settings.DEFAULT_PORT))
	                				edit.putString(Settings.PREF_PORT+j, "5061");
	                		}
	                		edit.commit();
	                   		Receiver.engine(mContext).halt();
	               			Receiver.engine(mContext).StartEngine();
	                   }
	                })
	            .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	
	                    }
	                })
	            .setNegativeButton(R.string.dontask, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                		Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	                		edit.putBoolean(Settings.PREF_NOPORT, true);
	                		edit.commit();
	                    }
	                })
				.show();
		} else if (PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_PREF, Settings.DEFAULT_PREF).equals(Settings.VAL_PREF_PSTN) &&
				!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Settings.PREF_NODEFAULT, Settings.DEFAULT_NODEFAULT))
			new AlertDialog.Builder(this)
				.setMessage(R.string.dialog_default)
	            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                		Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	                		edit.putString(Settings.PREF_PREF, Settings.VAL_PREF_SIP);
	                		edit.commit();	
	                    }
	                })
	            .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	
	                    }
	                })
	            .setNegativeButton(R.string.dontask, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                		Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	                		edit.putBoolean(Settings.PREF_NODEFAULT, true);
	                		edit.commit();
	                    }
	                })
				.show();
		
		one = (ImageButton) findViewById(R.id.one);
		one.setOnClickListener(this);
		two = (ImageButton) findViewById(R.id.two);
		two.setOnClickListener(this);
		three = (ImageButton) findViewById(R.id.three);
		three.setOnClickListener(this);
		four = (ImageButton) findViewById(R.id.four);
		four.setOnClickListener(this);
		five = (ImageButton) findViewById(R.id.five);
		five.setOnClickListener(this);
		six = (ImageButton) findViewById(R.id.six);
		six.setOnClickListener(this);
		seven = (ImageButton) findViewById(R.id.seven);
		seven.setOnClickListener(this);
		eight = (ImageButton) findViewById(R.id.eight);
		eight.setOnClickListener(this);
		nine = (ImageButton) findViewById(R.id.nine);
		nine.setOnClickListener(this);
		zero = (ImageButton) findViewById(R.id.zero);
		zero.setOnClickListener(this);
		poundkey = (ImageButton) findViewById(R.id.poundkey);
		poundkey.setOnClickListener(this);
		starkey = (ImageButton) findViewById(R.id.starkey);
		starkey.setOnClickListener(this);
		
		dial = (ImageButton) findViewById(R.id.dial);
		dial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (show_number.getText().toString().length() >= 4) {
//					call(show_number.getText().toString());
					call_menu(show_number);
				}
			}
		});
		
				am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//�Ȼ��ϵͳ����Ƶ����

				spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 5);//SoundPool?����Ͳ�����Ƶ��Դ
				map.put(0, spool.load(this, R.raw.dtmf0, 0));//���ò��ż��̵İ�����
				map.put(1, spool.load(this, R.raw.dtmf1, 0));
				map.put(2, spool.load(this, R.raw.dtmf2, 0));
				map.put(3, spool.load(this, R.raw.dtmf3, 0));
				map.put(4, spool.load(this, R.raw.dtmf4, 0));
				map.put(5, spool.load(this, R.raw.dtmf5, 0));
				map.put(6, spool.load(this, R.raw.dtmf6, 0));
				map.put(7, spool.load(this, R.raw.dtmf7, 0));
				map.put(8, spool.load(this, R.raw.dtmf8, 0));
				map.put(9, spool.load(this, R.raw.dtmf9, 0));
				map.put(11, spool.load(this, R.raw.dtmf11, 0));
				map.put(12, spool.load(this, R.raw.dtmf12, 0));
				
	}
	
	private void initPopupWindow(){
		LayoutInflater inflater = LayoutInflater.from(this); 
		View view = inflater.inflate(R.layout.popup_of_accountsetting_info, null); 
		View buttonCreateQuick = view.findViewById(R.id.button_popup_create_new_quick_call);
		View buttonEditContact = view.findViewById(R.id.button_popup_edit_contact);
		View buttonDeleteContact = view.findViewById(R.id.button_popup_delete_contact);
		buttonCreateQuick.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
//				createNewQuickCall(mContactInfo.contactId, mContactInfo.displayName);
				startActivity(new Intent(DialFragment.this, SettingsAccount.class));
			}
		});
		
		buttonEditContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
//				Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, mContactInfo.contactId);
//				editContact(personUri);
				Intent sendIntent = new Intent();  
				sendIntent.setAction(Intent.ACTION_SEND);  
				sendIntent.putExtra(Intent.EXTRA_TEXT, "ETalk免费网络电话，通话清晰，免费去电显示号码，快来用吧！");  
				sendIntent.setType("text/plain");  
				startActivity(Intent.createChooser(sendIntent, "分享到"));  
			}
		});
		
		buttonDeleteContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
//				Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, mContactInfo.contactId);
//				showDeleteDialog(personUri);
			}
		});
		
		mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false); 
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable()); 
		mPopupWindow.setOutsideTouchable(true); 
		mPopupWindow.setFocusable(true); 	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (Receiver.call_state != UserAgent.UA_STATE_IDLE) Receiver.moveTop();
	}
	
	
	

	void call_menu(EditText show_number2)//AutoCompleteTextView弹出的错误提示
	{
		String target = show_number2.getText().toString();
		if (m_AlertDlg != null) 
		{
			m_AlertDlg.cancel();
		}
		if (target.length() == 0)//当输入的为空时，弹出一个警告对话
			m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage(R.string.empty)
				.setTitle(R.string.app_name)
//				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();
		else if (!Receiver.engine(this).call(target,true))//数据交换
			m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage(R.string.notfast)
				.setTitle(R.string.app_name)
//				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();
	}
	
	public static boolean on(Context context) {//get the value of the preference
		Log.d("service", "Sipdroid.on()");
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Settings.PREF_ON, Settings.DEFAULT_ON);
	}
	
	public static void on(Context context,boolean on) {//打开service
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
        Log.d("service", "Sipdroid.on(context,true)");
	}
	
	public static String getVersion() {
		return getVersion(Receiver.mContext);
	}
	
	public static String getVersion(Context context) {
		final String unknown = "Unknown";
		
		if (context == null) {
			return unknown;
		}
		
		try {
	    	String ret = context.getPackageManager()
			   .getPackageInfo(context.getPackageName(), 0)
			   .versionName;
	    	if (ret.contains(" + "))
	    		ret = ret.substring(0,ret.indexOf(" + "))+"b";
	    	return ret;
		} catch(NameNotFoundException ex) {}
		
		return unknown;		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.zero:
			if (show_number.getText().length() < 12) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.one:
			if (show_number.getText().length() < 12) {
				play(2);
				input(v.getTag().toString());
			}
			break;
		case R.id.two:
			if (show_number.getText().length() < 12) {
				play(3);
				input(v.getTag().toString());
			}
			break;
		case R.id.three:
			if (show_number.getText().length() < 12) {
				play(4);
				input(v.getTag().toString());
			}
			break;
		case R.id.four:
			if (show_number.getText().length() < 12) {
				play(5);
				input(v.getTag().toString());
			}
			break;
		case R.id.five:
			if (show_number.getText().length() < 12) {
				play(6);
				input(v.getTag().toString());
			}
			break;
		case R.id.six:
			if (show_number.getText().length() < 12) {
				play(7);
				input(v.getTag().toString());
			}
			break;
		case R.id.seven:
			if (show_number.getText().length() < 12) {
				play(8);
				input(v.getTag().toString());
			}
			break;
		case R.id.eight:
			if (show_number.getText().length() < 12) {
				play(9);
				input(v.getTag().toString());
			}
			break;
		case R.id.nine:
			if (show_number.getText().length() < 12) {
				play(10);
				input(v.getTag().toString());
			}
			break;
		case R.id.starkey:
			if (show_number.getText().length() < 12) {
				play(11);
				input(v.getTag().toString());
			}
			break;
		case R.id.poundkey:
			if (show_number.getText().length() < 12) {
				play(12);
				input(v.getTag().toString());
			}
			break;
		case R.id.delete:
			delete();
			break;
		/*case R.id.show_number:
			if (show_number.getText().toString().length() >= 4) {
				call(show_number.getText().toString());
			}
			break;*/
		default:
			break;
		}
		
	}
	
	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}
	
	private void input(String str) {
		String p = show_number.getText().toString();
		show_number.setText(p + str);
	}
	
	private void delete() {
		String p = show_number.getText().toString();
		if(p.length()>0){
			show_number.setText(p.substring(0, p.length()-1));
		}
	}
	
	private void call(String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
	}
	
	public void onDismiss(DialogInterface dialog) {
		onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
//		if(mTipsView.getVisibility() == View.VISIBLE){
//			mTipsView.setVisibility(View.GONE);
//			return false;
//		}
		return super.onKeyDown(keyCode, event);
	}
	
}
	

