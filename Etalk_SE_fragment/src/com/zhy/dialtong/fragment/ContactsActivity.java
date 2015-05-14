package com.zhy.dialtong.fragment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.dialtong.ClearEditText;
import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.CharacterParser;
import com.zhy.dialtong.fragment.contacts.ContactHomeAdapter;
import com.zhy.dialtong.fragment.contacts.PinyinComparator;
import com.zhy.dialtong.fragment.contacts.SortModel;
import com.zhy.dialtong.view.QuickAlphabeticBar;

public class ContactsActivity extends Activity{
	
	private ClearEditText mClearEditText;
	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;
	
	private QuickAlphabeticBar alpha;
	private TextView dialog;
	private ListView sortListView;
	private TextView contacts_add;
	private static AsyncQueryHandler asyncQuery;
	
	private List<ContactBean> list;
	
	private Map<Integer, ContactBean> contactIdMap = null;
	
	private ContactHomeAdapter adapter;
	
	private LayoutInflater inflater;
	
	private LinearLayout mIndexerLayout;
	private RelativeLayout mSectionToastLayout;
    private TextView mSectionToastText;
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int lastSelectedPosition = -1;
    private AlphabetIndexer mIndexer;
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.contact_select_item_page, null));
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		mIndexerLayout = (LinearLayout) findViewById(R.id.indexer_layout);
		mSectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        mSectionToastText = (TextView) findViewById(R.id.section_toast_text);
        for(int i = 0; i < alphabet.length(); i++) {
            TextView letterTextView = new TextView(this);
            letterTextView.setText(alphabet.charAt(i)+"");
            letterTextView.setTextSize(14f);
            letterTextView.setGravity(Gravity.CENTER);
            LayoutParams params = new LinearLayout.LayoutParams(28, 0, 1.0f);
            letterTextView.setLayoutParams(params);
            letterTextView.setPadding(4, 0, 2, 0);
            mIndexerLayout.addView(letterTextView);
        }
        
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		sortListView = (ListView) findViewById(R.id.acbuwa_list);
		
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();
	}
	
private OnTouchListener mOnTouchListener = new OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float alphabetHeight = mIndexerLayout.getHeight();
            float y = event.getY();
            int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
            if (sectionPosition < 0) {
                sectionPosition = 0;
            } else if (sectionPosition > 26) {
                sectionPosition = 26;
            }
            if(lastSelectedPosition != sectionPosition) {
                if(-1 != lastSelectedPosition){
                   ((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                lastSelectedPosition = sectionPosition;
            }
            String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
            int position = mIndexer.getPositionForSection(sectionPosition);
            TextView textView = (TextView) mIndexerLayout.getChildAt(sectionPosition);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    sortListView.setSelection(position);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
                    textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
                    mSectionToastLayout.setVisibility(View.VISIBLE);
                    mSectionToastText.setText(sectionLetter);
                    sortListView.setSelection(position);
                    break;
                case MotionEvent.ACTION_UP:
                    mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mSectionToastLayout.setVisibility(View.GONE);
                default:
                    mSectionToastLayout.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
        
    };

	public static void init() {
		// TODO Auto-generated method stub
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; 
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
				ContactsContract.CommonDataKinds.Phone.STARRED,
		};
		asyncQuery.startQuery(0, null, uri, projection, null, null,"sort_key COLLATE LOCALIZED asc"); 
	}
	
	/**
	 * ���ݿ��첽��ѯ��AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		
		/**
		 * ��ѯ�����Ļص�����
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				
				contactIdMap = new HashMap<Integer, ContactBean>();
				
				list = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);
					int starred = cursor.getInt(7);

					if (contactIdMap.containsKey(contactId)) {
						
					}else{
						
						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// ȥ��������й����������־�����������û��Ӱ�졣
//						cb.setPhoneNum(number.substring(3));
//					} else {
						cb.setPhoneNum(number);
//					}
						cb.setSortKey(sortKey);
						cb.setContactId(contactId);
						cb.setPhotoId(photoId);
						cb.setLookUpKey(lookUpKey);
						cb.setStarred(starred);
//						cb.setDialcount(times);
						list.add(cb);
						
						contactIdMap.put(contactId, cb);
						
						mIndexer = new AlphabetIndexer(cursor, 1, alphabet);
						adapter.setIndexer(mIndexer);
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}
	
	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactHomeAdapter(this, list);
		sortListView.setAdapter(adapter);
        mIndexerLayout.setOnTouchListener(mOnTouchListener);
//		alpha.init(ContactsActivity.this);
//		alpha.setListView(sortListView);
//		alpha.setHight(alpha.getHeight());
//		alpha.setVisibility(View.VISIBLE);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				ContactBean cb = (ContactBean) adapter.getItem(position);
//				showContactDialog(lianxiren1, cb, position);
//				String number=callRecords.get(((ContactBean)adapter.getItem(position)).getPhoneNum());
				Intent intent=new Intent(ContactsActivity.this,ConstactsDetailActivity.class);
				intent.putExtra("number", ((ContactBean)adapter.getItem(position)).getPhoneNum());
				intent.putExtra("name", ((ContactBean)adapter.getItem(position)).getDisplayName());
//				intent.putExtra("photo",((ContactBean)adapter.getItem(position)).getPhoto());
				intent.putExtra("ID",((ContactBean)adapter.getItem(position)).getContactId()); 
				startActivity(intent);
			}
		});
//		sortListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				ContactBean cb = (ContactBean) adapter.getItem(position);
//				showContactDialog(lianxiren1, cb, position);
//				return true;
//			}
//		});
	}
//	
//	private String[] lianxiren1 = new String[] { "����绰", /*"���Ͷ���",*/ "�鿴��ϸ",/*"�ƶ�����","�Ƴ�Ⱥ��",*/"ɾ��" };
//	
//	//Ⱥ����ϵ�˵���ҳ
//		private void showContactDialog(final String[] arg ,final ContactBean cb, final int position){
//			new AlertDialog.Builder(this).setTitle(cb.getDisplayName()).setItems(arg,
//					new DialogInterface.OnClickListener(){
//				public void onClick(DialogInterface dialog, int which){
//
//					Uri uri = null;
//
//					switch(which){
//
//					case 0://��绰
//						String toPhone = cb.getPhoneNum();
//						uri = Uri.parse("tel:" + toPhone);
//						Intent it = new Intent(Intent.ACTION_CALL, uri);
//						startActivity(it);
//						break;
//
////					case 1://����Ϣ
////
////						String threadId = getSMSThreadId(cb.getPhoneNum());
////						
////						Map<String, String> map = new HashMap<String, String>();
////						map.put("phoneNumber", cb.getPhoneNum());
////						map.put("threadId", threadId);
////						BaseIntentUtil.intentSysDefault(HomeContactActivity.this, MessageBoxList.class, map);
////						break;
//
//					case 1:// �鿴��ϸ       �޸���ϵ������
//
//						uri = ContactsContract.Contacts.CONTENT_URI;
//						Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
//						Intent intent2 = new Intent();
//						intent2.setAction(Intent.ACTION_VIEW);
//						intent2.setData(personUri);
//						startActivity(intent2);
//						break;
//
//					//case 3: �ƶ�����
//
//						//					Intent intent3 = null;
//						//					intent3 = new Intent();
//						//					intent3.setClass(ContactHome.this, GroupChoose.class);
//						//					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//						//					intent3.putExtra("��ϵ��", contactsID);
//						//					Log.e("contactsID", "contactsID---"+contactsID);
//						//					ContactHome.this.startActivity(intent3);
////						break;
//
//					//case 4: �Ƴ�Ⱥ��
//
//						//					moveOutGroup(getRaw_contact_id(contactsID),Integer.parseInt(qzID));
////						break;
//
//					case 2:// ɾ��
//
//						showDelete(cb.getContactId(), position);
//						break;
//					}
//				}
//			}).show();
//		}
//
//		// ɾ����ϵ�˷���
//		private void showDelete(final int contactsID, final int position) {
//			new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("�Ƿ�ɾ������ϵ��")
//			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					//Դ��ɾ��
//					Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
//					Uri lookupUri = ContactsContract.Contacts.getLookupUri(ContactsActivity.this.getContentResolver(), deleteUri);
//					if(lookupUri != Uri.EMPTY){
//						ContactsActivity.this.getContentResolver().delete(deleteUri, null, null);
//					}
//					adapter.remove(position);
//					adapter.notifyDataSetChanged();
//					Toast.makeText(ContactsActivity.this, "����ϵ���Ѿ���ɾ��.", Toast.LENGTH_SHORT).show();
//				}
//			})
//			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//				}
//			}).show();
//		}

	
	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<ContactBean> filterDateList = new ArrayList<ContactBean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list;
		} else {
			filterDateList.clear();
			for (ContactBean ContactBean : list) {
				String name = ContactBean.getDisplayName();
				if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(ContactBean);
				}
			}
		}

		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
	/*private Map<String,String> getAllCallRecords() {
		Map<String,String> temp = new HashMap<String, String>();
		Cursor c = getContentResolver().query(	//������Ϣ����ϵ��
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (c.moveToFirst()) {
			do {
				// ��ȡ��ϵ��ID
				String contactId = c.getString(c
						.getColumnIndex(ContactsContract.Contacts._ID));
				// ��ȡ��ϵ������
				String name = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// ��ȡ��ϵ�˵绰����
				int phoneCount = c
						.getInt(c
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String number=null;
				if (phoneCount > 0) {
					// 
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						number = phones
								.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}
					phones.close();
				}
				temp.put(name, number);
			} while (c.moveToNext());
		}
		c.close();
		return temp;
	}*/

}