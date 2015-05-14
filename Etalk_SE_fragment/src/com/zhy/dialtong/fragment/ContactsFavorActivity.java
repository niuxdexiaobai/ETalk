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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.dialtong.ClearEditText;
import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.CharacterParser;
import com.zhy.dialtong.fragment.contacts.ContactHomeAdapter;
import com.zhy.dialtong.fragment.contacts.PinyinComparator;
import com.zhy.dialtong.view.QuickAlphabeticBar;

public class ContactsFavorActivity extends Activity{
	
	public static final String TAG = "FavoriteContacts";
	
	private ClearEditText mClearEditText;
	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;
	private TextView dialog;
	private QuickAlphabeticBar alpha;
	
	private ListView sortListView;
	private static AsyncQueryHandler asyncQuery;
	
	private List<ContactBean> list;
	
	private Map<Integer, ContactBean> contactIdMap = null;//
	
	private ContactHomeAdapter adapter;
	
	private LayoutInflater inflater;
	
	private Map<String,String> callRecords;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.contact_select_item_page, null));
		
		// ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
				
		pinyinComparator = new PinyinComparator();
				
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
			filterData(s.toString());//����
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	});
				
//		dialog = (TextView) findViewById(R.id.fast_position);
				
		sortListView = (ListView) findViewById(R.id.acbuwa_list);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
		}
		});
				
		// Getting a List of the favorites
//		list = new ArrayList<ContactBean>();
//		List<ContactBean> newList = new ArrayList<ContactBean>();
//		for ( ContactBean contact : list ) {
//			if (contact.getFavourite() == 1) {
//				newList.add(contact);
//			}
//		}
//		list = newList;
				
//		alpha = (QuickAlphabeticBar)this.findViewById(R.id.fast_scroller);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();
//		getKeepedContacts();
//		populateContactList();
	}
	
	// Populate the contact list
//		private void populateContactList() {
//			Uri uri = ContactsContract.Contacts.CONTENT_URI;
//			String[] projection = new String[] { ContactsContract.Contacts._ID,//λ��
//					ContactsContract.Data.PHOTO_ID,//��Ƭ
//					ContactsContract.Contacts.DISPLAY_NAME };//����
//			String selection = ContactsContract.Contacts.STARRED;//�ղ�
//			String[] selectionArgs = null;
//			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME//ѡ����֮�����е�˳��
//					+ " COLLATE LOCALIZED ASC";
//			Cursor myCursor = getContentResolver().query(uri, projection,
//					selection, selectionArgs, sortOrder);
//			startManagingCursor(myCursor);//��ʼ��

//			String[] fields = new String[] { ContactsContract.Data.DISPLAY_NAME };
//			int[] names = new int[] { R.id.nametitle };
//			MyCursorAdapter myAdapter = new MyCursorAdapter(
//					getApplicationContext(), R.layout.contacts_item_sort_listview, myCursor,
//					fields, names);
//			ListView ConList = (ListView) findViewById(R.id.acbuwa_list);
//			ConList.setAdapter(myAdapter);
			
//			String[] fields1 = new String[] { ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
//			int[] names1 = new int[] { R.id.numbershow };
//			MyCursorAdapter myAdapter1 = new MyCursorAdapter(
//					getApplicationContext(), R.layout.contacts_item_sort_listview, myCursor,
//					fields1, names1);
//			ListView ConList1 = (ListView) findViewById(R.id.acbuwa_list);
//			ConList1.setAdapter(myAdapter1);

			// handle list select
//			OnItemClickListener listener = new OnItemClickListener() {//��Item��ȡ����֮���绰
//				public void onItemClick(AdapterView<?> a, View v, int position,
//						long id) {
//					Log.d(TAG, "onItemClick, id = " + id);
//					// vars
//					String phone;
//
//					// get contact's phone numbers
//					Cursor cur = getPhonesById(id);
//
//					// check number of phones for contact
//					if (cur.getCount() == 0) // no phone numbers
//					{
////						toast(getString(R.string.phone_chooser_no_numbers));
//						cur.close();
//						return;
//					} else if (cur.getCount() > 1) // more then 1 number
//					{
//						// look for super primary entry
//						phone = lookForPrimaryPhone(cur);
//						if (phone != null) // call if found
//						{
//							callNumber(phone);
//						} else // if not found choose default and check again
//						{
////							launchPhoneChooser(id);
//							
//						}
//					} else // if only one number call it
//					{
//						cur.moveToNext();
//						callNumber(cur//��ȡ�����벢����callNumber��������ϵͳ���Ž��в���
//								.getString(cur
//										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//					}
//					cur.close();
//					
//				}
//			};
//
//			OnItemLongClickListener listenerLong = new OnItemLongClickListener() {//����Item��ȡ����
//				public boolean onItemLongClick(AdapterView<?> a, View v,
//						int position, long id) {
//					Log.d(TAG, "onItemLongClick, id = " + id);
//					// get contact's phone numbers
//					Cursor cur = getPhonesById(id);
//
//					// check number of phones for contact
//					if (cur.getCount() > 1) // more then 1 number
//					{
////						launchPhoneChooser(id);
//					}
//					if (cur.getCount() == 0) {
////						toast(getString(R.string.phone_chooser_no_numbers));
//					}
//					if (cur.getCount() == 1) {
////						toast(getString(R.string.phone_chooser_one_number));
//					}
//					cur.close();
//					return true;
//				}
//			};
//
//			ConList.setOnItemLongClickListener(listenerLong);
//			ConList.setOnItemClickListener(listener);
//		}
		
		
		
//		private Cursor getPhonesById(long id)
//		{
//			ContentResolver cr = getContentResolver();
//			Cursor cur = cr.query(
//					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//					null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//							+ " = ?", new String[] { String.valueOf(id) },
//					null);
//			return cur;
//		}
		
		// looks for primary phone and return number
		// returns null if not found
//		private String lookForPrimaryPhone(Cursor cur) {
//			while (cur.moveToNext()) {
//				if (cur.getInt(cur
//						.getColumnIndex(ContactsContract.Data.IS_SUPER_PRIMARY)) == 1) {
//					return cur
//							.getString(cur
//									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//				}
//			}
//			return null;
//		}
		
		// calls a number
//		private void callNumber(String phone) {
//			toast("Calling " + phone);
//			try {
//				Intent intent = new Intent(Intent.ACTION_CALL);
//				intent.setData(Uri.parse("tel:" + phone));
//				finish();
//				startActivity(intent);
//			} catch (Exception e) {
//				toast("Problem calling number.");
//			}
//
//		}
		
		// launch phone number chooser activity
//		public void launchPhoneChooser(long ContactId) {
//			
//			Intent intent = new Intent(this, PhoneChooser.class);
//			Log.d("QuickConn", "launchPhoneChooser, id = " + ContactId);
//			intent.putExtra("ContactId", ContactId);
//			startActivity(intent);
//			
//		}
		
		
		
		
		
		// easily create toast
//		private void toast(String str) {
//			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//		}
		
		
		
		
		
		// text a number
//		private void textNumber(String phone) {
//			toast("Texting " + phone);
//			try {
//				Intent sendIntent= new Intent(Intent.ACTION_VIEW); 
//				sendIntent.putExtra("address",  phone); 
//				sendIntent.setType("vnd.android-dir/mms-sms"); 
//				sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//				sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//				startActivity(sendIntent); 
//			} catch (Exception e) {
//				toast("Problem Texting number.");
//			}
//
//		}
		
		// click text button
//		void onItemTxtClick(long id)
//		{
//			Log.d(TAG, "onItemTxtClick, id = " + id);
//			// vars
//			String phone;
//
//			// get contact's phone numbers
//			Cursor cur = getPhonesById(id);
//
//			// check number of phones for contact
//			if (cur.getCount() == 0) // no phone numbers
//			{
//				toast("û�к������ѡ��");
//				cur.close();
//				return;
//			} else if (cur.getCount() > 1) // more then 1 number
//			{
//				// look for super primary entry
//				phone = lookForPrimaryPhone(cur);
//				if (phone != null) // text if found
//				{
//					textNumber(phone);
//				} else // if not found choose default and check again
//				{
////					launchPhoneChooser(id);	
//				}
//			} else // if only one number text it
//			{
//				cur.moveToNext();
//				textNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//			}
//			cur.close();
//		}
		
//		public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
//	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
//	                .getHeight(), Config.ARGB_8888);
//	        Canvas canvas = new Canvas(output);
//
//	        final int color = 0xff424242;
//	        final Paint paint = new Paint();
//	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//	        final RectF rectF = new RectF(rect);
//	        final float roundPx = pixels;
//
//	        paint.setAntiAlias(true);
//	        canvas.drawARGB(0, 0, 0, 0);
//	        paint.setColor(color);
//	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//	        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//	        return output;
//	    }
	
		
		// class to retrieve contact photos
//		class MyCursorAdapter extends SimpleCursorAdapter {
//			private Cursor cCursor;
//			public Context cContext;
//
//			public MyCursorAdapter(Context context, int layout, Cursor c,String[] from, int[] to) {
//				super(context, layout, c, from, to);
//				cCursor = c;
//				cContext = context;
//			}
//			
//					
//			@Override
//			public void bindView(View view, Context context, Cursor cursor) {
//
//				// get contact id
//				final long id = (long) cCursor.getInt(cCursor.getColumnIndex(ContactsContract.Contacts._ID));
//				
//				TextView nametitle = (TextView) view.findViewById(R.id.nametitle);
//				// get image view
//				ImageView imageViewPhoto = (ImageView) view.findViewById(R.id.userImg);
////				ImageView imageViewTxt = (ImageView) view.findViewById(R.id.contactTxtImage);
//				// get contact's photo
//				Bitmap photo = BitmapFactory.decodeStream(openPhoto(id));
//				if (photo!=null)
//				{
//					if (photo.getWidth()<60)
//					{
//						photo = Bitmap.createScaledBitmap(photo, 90, 90, true);
//					}
//					photo = getRoundedCornerBitmap(photo,18);
//				}
//				// set image to view
//					imageViewPhoto.setImageBitmap(photo);
//				
//
//				// set listener for text image
////				imageViewTxt.setOnClickListener(new View.OnClickListener() {
//		//
////					@Override
////					public void onClick(View view) {
////						Log.d(TAG, "List Txt click, id = " + String.valueOf(id));
////						onItemTxtClick(id);
////					}				
////				});
//
//				super.bindView(view, context, cursor);
//			}
					

//			private InputStream openPhoto(long contactId) {
//				Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
//				InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cContext.getContentResolver(),
//										contactUri);
//				return input;
//			}
//
//		}
		
//	/**
//	* ����ղؼе���ϵ��
//	*/
//	private void getKeepedContacts(){
////		list = new ArrayList<ContactBean>();
//	Cursor cur = getContentResolver().query(  
//	                ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.STARRED + " =  1 " , null, null);  
//	        startManagingCursor(cur);  
//	        int num = cur.getCount();
//	        System.out.println(num + "");
//	        int count = 0;
//	        while (cur.moveToNext()) {  
//	        count ++;
//	   
//	            long id = cur.getLong(cur.getColumnIndex("_id"));  
//	            Cursor pcur = getContentResolver().query(  
//	                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
//	                    null,  
//	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="  
//	                            + Long.toString(id), null, null);  
//	   
//	            // ��������������  
//	            String phoneNumbers = "";  
//	            while (pcur.moveToNext()) {  
//	                String strPhoneNumber = pcur  
//	                        .getString(pcur  
//	                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
//	                phoneNumbers += strPhoneNumber + ":";  
//	            }  
//	            phoneNumbers += "\n";  
//	            pcur.close();
//	            String name = cur.getString(cur.getColumnIndex("display_name"));
//	            ContactBean cb = new ContactBean();
//	            cb.setDisplayName(name);
//	            cb.setPhoneNum(phoneNumbers);
//	            list.add(cb);
////	            contactNameList.add(name);
////	            contactNumList.add(phoneNumbers);
//	        }  
//	        cur.close();
//	}
//	
//	
//	
//	
	public static void init() {
		// TODO Auto-generated method stub
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // ��ϵ�˵�Uri
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		};
		String selection = ContactsContract.Contacts.STARRED;
		
		// ��ѯ����
		asyncQuery.startQuery(0, null, uri, projection, selection, null,
				"sort_key COLLATE LOCALIZED asc"); // ����sort_key�����ѯ
		
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
				
				contactIdMap = new HashMap<Integer, ContactBean>();//�½�һ��hashMap���������ϵ������
				
				list = new ArrayList<ContactBean>();//�½�һ��ֻ֧��ContactBean�������б�
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);

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
						list.add(cb);
						
						contactIdMap.put(contactId, cb);
						
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
		alpha.init(ContactsFavorActivity.this);
		alpha.setListView(sortListView);
		alpha.setHight(alpha.getHeight());
		alpha.setVisibility(View.VISIBLE);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				ContactBean cb = (ContactBean) adapter.getItem(position);
//				showContactDialog(lianxiren1, cb, position);
//				String number=callRecords.get(((ContactBean)adapter.getItem(position)).getPhoneNum());
				Intent intent=new Intent(ContactsFavorActivity.this,ConstactsDetailActivity.class);
				intent.putExtra("number", ((ContactBean)adapter.getItem(position)).getPhoneNum());
				intent.putExtra("name", ((ContactBean)adapter.getItem(position)).getDisplayName());
//				intent.putExtra("photo", );
				startActivity(intent);
			}
		});
	}
	
	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<ContactBean> filterDateList = new ArrayList<ContactBean>();
		
//		for ( ContactBean contact : list ) {
//			if (contact.getFavourite() == 1) {
//				filterDateList.add(contact);
//			}
//		}
//		list = filterDateList;

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
	
//	//Results of the search bar
//    private void showResults(String string) {
//		List<ContactBean> newList = new ArrayList<ContactBean>();
//		for ( ContactBean contact : callRecords.getAllCallRecords() ) {
//			if (contact.getFavourite() == 1) {
//				newList.add(contact);
//			}
//		}
//		contactList = newList;
//
//    	ListHelper helperClass = new ListHelper(contactList,sort);
//		contactList = helperClass.search(string, contactList);  		
//    		
//    		SetUpListView();
//    	}
    
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
