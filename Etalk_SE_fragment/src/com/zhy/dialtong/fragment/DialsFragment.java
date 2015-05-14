/*package com.zhy.dialtong.fragment;

import java.util.List;

import com.zhuang.quickcall.logging.MarketLog;
import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.QuickCallConstants;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.dial.MyApplication;
import com.zhy.dialtong.fragment.dial.T9Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class DialsFragment extends Fragment{
	private ImageButton add,delete,detail_number,dial;
	private View mTipsView;
	private EditText show_number;
	private Context context;
	private ListView show_relate_num;
	
	private T9Adapter t9Adapter;
	
	private static List<ContactBean> contactBeanList;//
	
	public static List<ContactBean> getContactBeanList() {//
		return contactBeanList;
	}
	public void setContactBeanList(List<ContactBean> contactBeanList) {//
		this.contactBeanList = contactBeanList;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dial_page_rl, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		add = (ImageButton) view.findViewById(R.id.add_to_contacts);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
				startActivityForResult(intent, 1008);
			}
		});
		
		delete = (ImageButton) view.findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String p = show_number.getText().toString();
				if(p.length()>0){
					show_number.setText(p.substring(0, p.length()-1));
				}
				
			}
		});
		delete.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				show_number.setText("");
				return false;
			}
		});
		
		detail_number = (ImageButton) view.findViewById(R.id.detail_number);
		detail_number.setVisibility(View.INVISIBLE);
		detail_number.setOnClickListener(new OnClickListener() {//
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,DialShowFilterNumberActivity.class);
				intent.putExtra("photonumber", show_number.getText().toString());
				startActivity(intent);
			}
		});
		
		show_relate_num = (ListView) view.findViewById(R.id.show_relate_num);
		
		show_number = (EditText) view.findViewById(R.id.show_number);
		show_number.setInputType(InputType.TYPE_NULL);
		show_number.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int inType = show_number.getInputType();
				show_number.setInputType(InputType.TYPE_NULL);
				show_number.onTouchEvent(event);
				show_number.setInputType(inType);
				return false;
			}
		});
		show_number.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(null == DialsFragment.getContactBeanList() || DialsFragment.getContactBeanList().size()<0 || "".equals(s.toString())){
					Log.v("TextNotChange", "istrue");
					show_relate_num.setVisibility(View.INVISIBLE);
					detail_number.setVisibility(View.INVISIBLE);
				}else{
					if(null == t9Adapter){
						t9Adapter = new T9Adapter(getActivity());
						t9Adapter.assignment(DialsFragment.getContactBeanList());
						show_relate_num.setAdapter(t9Adapter);
						show_relate_num.setTextFilterEnabled(true);
						detail_number.setVisibility(View.VISIBLE);
					}
				else{
					show_relate_num.setVisibility(View.VISIBLE);
					detail_number.setVisibility(View.VISIBLE);
					t9Adapter.getFilter().filter(s);
				}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				show_number.setSelection(show_number.getText().length());
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

}
*/