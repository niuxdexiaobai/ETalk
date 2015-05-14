package com.zhy.dialtong.fragment.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.LogLevel;
import com.zhy.dialtong.QuickCallConstants;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.ConstactsDetailActivity;
import com.zhy.dialtong.fragment.ContactsFavorActivity;
import com.zhy.dialtong.fragment.contacts.ContactsProjection.PersonalContacts;
import com.zhy.dialtong.utils.widgets.ContactAlphaView;
import com.zhy.dialtong.utils.widgets.ContactAlphaView.OnAlphaChangedListener;
import com.zhy.dialtong.utils.widgets.DialUtils;
import com.zhy.dialtong.utils.widgets.DialogUtils;
import com.zhy.dialtong.utils.widgets.EmailSender;
import com.zhy.dialtong.utils.widgets.ImageLoader;
import com.zhy.dialtong.utils.widgets.QuickCallAlertDialog;

import net.sourceforge.pinyin4j.PinyinHelper;


import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ContactsFragment extends Activity implements OnAlphaChangedListener {

	private static final String TAG = "[ZHUANG]ContactsFragment";
	
	private static final int QUERY_COMPLETE = 0;
	private static final int QUERY_NO_CONTACTS = 1;
	private static final int QUERY_NO_RESULTS = 2;
	
	private static final int EDIT_CONTACT_REQUEST = 78;
	
	private static final int QUERY_TOKEN = 303;
	
	private static final String POUND_FOR_UNKNOWN = "#";
	
	private static final String FIRST_ENGLISH_LETTER_PATTERN = "^[A-Za-z]";
	
	private static final Pattern mPattern = Pattern.compile(FIRST_ENGLISH_LETTER_PATTERN);
	
	private static final String SORT_KEY_ORDER = "sort_key COLLATE LOCALIZED ASC";

    private static final String PERSONAL_QUERY_SELECTION =
    		ContactsContract.Contacts.DISPLAY_NAME   + " LIKE ? || '%'" + " OR " + 
    		ContactsContract.Contacts.DISPLAY_NAME   + " LIKE '%' || ' ' || ? || '%'" + " OR " + 
    		ContactsContract.Contacts.DISPLAY_NAME   + " LIKE '+' || ? || '%'" + " OR " + 
    		ContactsContract.Contacts.DISPLAY_NAME   + " LIKE '(' || ? || '%'";
	
    private ListView mContactsListView;
	private EditText mSearchTextView;
	private ContactAlphaView mContactAlphaView;
	private TextView mOverlayView;
	private View mEmptyView;
	private View mQueryProgressView;
	private TextView mEmptyTextView;
	private ImageButton mSearchClearButton;
	private ImageView mAddcontact;
	
	private WindowManager windowManager;
	private OverlayThread overlayThread;
	
	protected ContactsAdapter mContactsAdapter;
	private QueryContactsHandler mQueryContactsHandler;
	
	private boolean isHasCreated = false;
	
	private AlphabetIndexer mAlphabetIndexer;
	private static final String ALPHABET_INDEXS = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static AsyncQueryHandler asyncQuery;
	private Map<Integer, ContactBean> contactIdMap = null;
	private List<ContactBean> list;
	private GridviewAdapter adapter;
	private GridView gridView;
//	@Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if(LogLevel.MARKET){
//			MarketLog.i(TAG, "onAttach...");
//		}
//        
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_fragment_layout);
		mAddcontact = (ImageView) findViewById(R.id.dialsetting);
		mAddcontact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
				startActivityForResult(intent, 1008);
			}
		});
		
//		gridView = (GridView) findViewById(R.id.gridView1);
//		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
//		init();
		
		mContactsListView = (ListView) findViewById(R.id.contacts_listview);
		
		mSearchTextView = (EditText) findViewById(R.id.search_edittext);
		mSearchTextView.addTextChangedListener(mSearchTextWatcher);
		
		mContactAlphaView = (ContactAlphaView) findViewById(R.id.contact_alpha_view);
		mContactAlphaView.setOnAlphaChangedListener(this);
		
		mEmptyView = findViewById(R.id.empty_layout);
		mQueryProgressView = findViewById(R.id.query_proLoading);
		mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
		mEmptyView.setVisibility(View.VISIBLE);
		mQueryProgressView.setVisibility(View.VISIBLE);
		mEmptyTextView.setVisibility(View.GONE);
		
		mSearchClearButton = (ImageButton) findViewById(R.id.button_search_clear);
		mSearchClearButton.setVisibility(View.GONE);
		mSearchClearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSearchTextView.getEditableText().clear();
			}
		});
		
		mContactsListView.setOnTouchListener(mOnTouchListener);
		mEmptyView.setOnTouchListener(mOnTouchListener);
		
		mContactsListView.setOnItemClickListener(onItemClickListener);
		mContactsListView.setOnItemLongClickListener(onItemLongClickListener);
		mQueryContactsHandler = new QueryContactsHandler(this);
		mContactsAdapter = new ContactsAdapter(this);
		mContactsListView.setAdapter(mContactsAdapter);
		
		overlayThread = new OverlayThread();
		
		isHasCreated = true;
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if(LogLevel.MARKET){
//			MarketLog.i(TAG, "onCreateView...");
//		}
//		View v = inflater.inflate(R.layout.contacts_fragment_layout, container, false);
//		buildLayout(v);
//		
//		return v;
//	}

//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		if(LogLevel.MARKET){
//			MarketLog.i(TAG, "onViewCreated...");
//		}
//		super.onViewCreated(view, savedInstanceState);
//	}
	
	@Override
	public void onResume(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
		super.onResume();
		
		initOverlay();

		if(isHasCreated){
			startQuery();
			isHasCreated = false;
		}
		
	}
	
	@Override
	public void onPause(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
		super.onPause();
		try {
			windowManager.removeViewImmediate(mOverlayView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		super.onDestroy();
		if (mQueryContactsHandler != null) {
			mQueryContactsHandler.cancelOperation(QUERY_TOKEN);
			mQueryContactsHandler = null;
		}
		
	}
	
	/*public static void init() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; 
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
		
		asyncQuery.startQuery(0, null, uri, projection, selection, null,
				"sort_key COLLATE LOCALIZED asc"); 
	}
	
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		
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

					if (contactIdMap.containsKey(contactId)) {
						
					}else{
						
						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
//					if (number.startsWith("+86")) {
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
		adapter = new GridviewAdapter(ContactsFragment.this, list);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent=new Intent(ContactsFragment.this,ConstactsDetailActivity.class);
//				intent.putExtra("number", ((ContactBean)adapter.getItem(position)).getPhoneNum());
//				intent.putExtra("name", ((ContactBean)adapter.getItem(position)).getDisplayName());
//				startActivity(intent);
			}
		});
	}*/
	
	private void startQuery(){
		if(isInSearching()){
			startSearchQuery(getSearchText());
		} else {
			startEmptyQuery();
		}
	}
	
	private void startEmptyQuery(){
		
		mSearchClearButton.setVisibility(View.GONE);
		if (mQueryContactsHandler == null) {
			if(LogLevel.DEV){
				DevLog.e(TAG, "startEmptyQuery() failed, mQueryContactsHandler is null.");
			}
			return;
		}
		mQueryContactsHandler.cancelOperation(QUERY_TOKEN);
		
		Uri uri = ContactsUtils.getPeopleUri();
		mQueryContactsHandler.startQuery(QUERY_TOKEN, null, uri, 
				ContactsProjection.PersonalContacts.PERSONAL_CONTACTS_SUMMARY_PROJECTION, 
				null, 
				null, 
				SORT_KEY_ORDER);
	}
	
	private void startSearchQuery(String search){
		if(mContactsAdapter == null){
			if(LogLevel.DEV){
				DevLog.e(TAG, "startSearchQuery() failed, mContactsAdapter is null.");
			}
			return;
		}
		
		mSearchClearButton.setVisibility(isInSearching() ? View.VISIBLE : View.GONE);
		mContactsAdapter.getFilter().filter(search);
		if(mContactsAdapter.getCount() > 0){
			mContactsListView.setSelection(0);
		}
	}
	
	private Handler mStopLoadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch(msg.what){
			case QUERY_COMPLETE:
				mEmptyView.setVisibility(View.GONE);
				mQueryProgressView.setVisibility(View.GONE);
				mEmptyTextView.setVisibility(View.GONE);
				break;
			case QUERY_NO_CONTACTS:
				mEmptyView.setVisibility(View.VISIBLE);
				mQueryProgressView.setVisibility(View.GONE);
				mEmptyTextView.setVisibility(View.VISIBLE);
				mEmptyTextView.setText(R.string.no_contacts);
				break;
			case QUERY_NO_RESULTS:
				mEmptyView.setVisibility(View.VISIBLE);
				mQueryProgressView.setVisibility(View.GONE);
				mEmptyTextView.setVisibility(View.VISIBLE);
				mEmptyTextView.setText(R.string.no_results_found);
				break;
			}
		}
		
	};
	
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mOverlayView = (TextView) inflater.inflate(R.layout.overlay, null);
		mOverlayView.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) ContactsFragment.this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(mOverlayView, lp);
	}
	
	private void buildLayout(View v){

		if(LogLevel.DEV){
			DevLog.d(TAG, "Build layout.");
		}
		
		
	}
	
	private final class QueryContactsHandler extends AsyncQueryHandler {
		
		public QueryContactsHandler(Context context) {
			super(context.getContentResolver());
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor == null || cursor.isClosed() || !cursor.moveToFirst()) {
				mStopLoadHandler.sendEmptyMessage(QUERY_NO_CONTACTS);
				return;
			}
			
			if (!ContactsFragment.this.isFinishing()) {
				mAlphabetIndexer = new AlphabetIndexer(cursor, PersonalContacts.SORT_KEY_INDEX, ALPHABET_INDEXS);
				
				if(cursor.getCount() > 0) {
        			mStopLoadHandler.sendEmptyMessage(QUERY_COMPLETE);
        		} else {
        			mStopLoadHandler.sendEmptyMessage(QUERY_NO_CONTACTS);
        		}
				
				mContactsAdapter.changeCursor(cursor);
			} else {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}
	
	protected String getTitle(String displayName) {
		
		if(TextUtils.isEmpty(displayName)){
			return POUND_FOR_UNKNOWN;
		}
		
		Matcher matcher = mPattern.matcher(displayName);
		if (!matcher.find()) {
			char[] chars = displayName.toCharArray();
			String str = pinyin(chars[0]);
			if(TextUtils.isEmpty(str)){
				return POUND_FOR_UNKNOWN;
			}
			if(!mPattern.matcher(str).find()){
				return POUND_FOR_UNKNOWN;
			} else {
				return str.trim().substring(0, 1).toUpperCase();
			}
			
		} else {
			return displayName.trim().substring(0, 1).toUpperCase();
		}
	}
	
	private String pinyin(char c) {
		String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
		if (pinyins == null) {
			return null;
		}
		return pinyins[0];
	}

	protected String getDisplayName(Cursor c) {
		
		String displayName = c.getString(ContactsProjection.PersonalContacts.DISPLAY_NAME_COLUMN_INDEX);
		if(TextUtils.isEmpty(displayName)) {
			return "";
		}
		
		return displayName;
	}
	
	private class ContactItemView{
		public TextView sectionView;
		public TextView nameView;
		public ImageView photoView;
		
	}
	
	protected class ContactsAdapter extends ResourceCursorAdapter {

		private Context mContext;
		private ImageLoader mImageLoader;
		
        public ContactsAdapter(Context context) {
			super(context, R.layout.contacts_list_item_layout, null);
			this.mContext = context;
			mImageLoader = new ImageLoader(context, ImageLoader.TYPE_CONTACTS);
		}
        

		@Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (LogLevel.MARKET) {
				MarketLog.d(TAG, "runQueryOnBackgroundThread(): start... constraint = " + constraint);
			}
			String what = constraint.toString();
			Cursor cursor = null;
        	Uri uri = ContactsUtils.getPeopleUri();
        	cursor = mContext.getContentResolver().query(
        			uri, 
        			ContactsProjection.PersonalContacts.PERSONAL_CONTACTS_SUMMARY_PROJECTION, 
        			PERSONAL_QUERY_SELECTION, 
        			new String[]{what, what, what, what}, 
        			SORT_KEY_ORDER);
        	
        	if(cursor == null || cursor.isClosed() || cursor.getCount() == 0){
        		if(TextUtils.isEmpty(what)) {
        			mStopLoadHandler.sendEmptyMessage(QUERY_NO_CONTACTS);
        		} else {
        			mStopLoadHandler.sendEmptyMessage(QUERY_NO_RESULTS);
        		}
        	} else {
        		mStopLoadHandler.sendEmptyMessage(QUERY_COMPLETE);
        	}
        	return cursor;
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = super.newView(context, cursor, parent);
			final ContactItemView cache = new ContactItemView();
			cache.sectionView = (TextView) view.findViewById(R.id.first_alpha_text);
			cache.nameView = (TextView) view.findViewById(R.id.name);
			cache.photoView = (ImageView) view.findViewById(R.id.image_view);
			
			view.setTag(cache);
			
			return view;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null || cursor.isClosed()){
				return;
			}
			final ContactItemView cache = (ContactItemView) view.getTag();
			String name = cursor.getString(PersonalContacts.DISPLAY_NAME_COLUMN_INDEX);
			cache.nameView.setText(TextUtils.isEmpty(name) ? "" : name);
			
			if(isInSearching()){
				cache.sectionView.setVisibility(View.GONE);
			} else {
				int position = cursor.getPosition();
				int section = mAlphabetIndexer.getSectionForPosition(position);
				if (position == mAlphabetIndexer.getPositionForSection(section)) {
					cache.sectionView.setText(getTitle(name));
					cache.sectionView.setVisibility(View.VISIBLE);
					cache.sectionView.setOnClickListener(null);
				} else {
					cache.sectionView.setVisibility(View.GONE);
				}
			}
			
			long photoId = cursor.getLong(PersonalContacts.PHOTO_ID_COLUMN_INDEX);
			if(photoId <= 0){
				cache.photoView.setImageResource(R.drawable.btn_head);
				return;
			}
			mImageLoader.displayImage(String.valueOf(photoId), cache.photoView);
			
		}
	}

	private Handler mOverlayHandler = new Handler();

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			mOverlayView.setVisibility(View.GONE);
		}

	}

	@Override
	public void OnAlphaChanged(String s, int index) {
		try{
			if (s != null && s.trim().length() > 0) {
				hideSoftInputFromWindow();
				mOverlayView.setText(s);
				mOverlayView.setVisibility(View.VISIBLE);
				mOverlayHandler.removeCallbacks(overlayThread);
				mOverlayHandler.postDelayed(overlayThread, 700);
				int position = mAlphabetIndexer.getPositionForSection(index);
				mContactsListView.setSelection(position);
			} 
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private TextWatcher mSearchTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
		
		@Override
		public void afterTextChanged(Editable edit) {
			String searchText = edit.toString();
			if(TextUtils.isEmpty(searchText)){
				mContactAlphaView.setVisibility(View.VISIBLE);
				startEmptyQuery();
			} else {
				mAlphabetIndexer = null;
				mContactAlphaView.setVisibility(View.GONE);
				startSearchQuery(searchText);
			}
		}
	};
	
	private boolean isInSearching(){
		if(mSearchTextView == null){
			return false;
		}
		
		return !TextUtils.isEmpty(mSearchTextView.getEditableText().toString());
	}
	
	private String getSearchText(){
		if(mSearchTextView == null){
			return "";
		}
		
		return mSearchTextView.getEditableText().toString();
	}
	
	private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			return hideSoftInputFromWindow();
		}
	};
	
	private boolean hideSoftInputFromWindow() {
		if (mSearchTextView != null) {
			mSearchTextView.clearFocus();
		}
		InputMethodManager imm = (InputMethodManager) ContactsFragment.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && ContactsFragment.this.getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(ContactsFragment.this.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		}
		return false;
	}
	
	
	private OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(mContactsAdapter == null){
				if(LogLevel.MARKET){
					MarketLog.e(TAG, "onListItemClick mContactsAdapter = null");
				}
				return;
			}
			
			Cursor cursor = (Cursor) mContactsAdapter.getItem(position);
			if (cursor == null) {
				return;
			}
			
			long personId = cursor.getLong(ContactsProjection.PersonalContacts.ID_COLUMN_INDEX);
			if(LogLevel.DEV){
				DevLog.d(TAG, "onListItemClick personId : " + personId);
			}
			switchContactInfo(personId);
		}
	};
	private OnItemLongClickListener	onItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(mContactsAdapter == null){
				if(LogLevel.MARKET){
					MarketLog.e(TAG, "onItemLongClickListener mContactsAdapter = null");
				}
				return false;
			}
			
			Cursor cursor = (Cursor) mContactsAdapter.getItem(position);
			if (cursor == null) {
				return false;
			}
//			showLongClickDialog(cursor);
			return false;
		}
	};
	
	/*private void showLongClickDialog(final Cursor cursor){
		final String displayName = cursor.getString(ContactsProjection.PersonalContacts.DISPLAY_NAME_COLUMN_INDEX);
		final long peopleId = cursor.getLong(ContactsProjection.PersonalContacts.ID_COLUMN_INDEX);
		int hasPhone = cursor.getInt(ContactsProjection.PersonalContacts.HAS_PHONE_NUMBER_COLUMN_INDEX);
		List<EmailContact> mailList = ContactsUtils.getEmailAddresses(getActivity(), peopleId);
		final Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, peopleId);
		
//		if(LogLevel.DEV){
//			DevLog.d(TAG, "showLongClickDialog phoneId : " + peopleId);
//		}
		
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_long_click_contact_layout, null);
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.icon_dialog_title_for_menu);
		builder.setTitle(displayName);
		builder.setContentView(v);
		
		final QuickCallAlertDialog dialog = builder.create();
		Button callBtn = (Button) dialog.findViewById(R.id.button_call_contact);
		Button sendMessageBtn = (Button) dialog.findViewById(R.id.button_send_message);
		Button viewContactBtn = (Button) dialog.findViewById(R.id.button_view_contact);
		Button sendEmailBtn = (Button) dialog.findViewById(R.id.button_send_email);
		Button editBtn = (Button) dialog.findViewById(R.id.button_edit_contact);
		Button deleteBtn = (Button) dialog.findViewById(R.id.button_delete_contact);
		Button createQuickCallBtn = (Button) dialog.findViewById(R.id.button_create_new_quick_call);
		
		if(hasPhone != 0){
			callBtn.setVisibility(View.VISIBLE);
			sendMessageBtn.setVisibility(View.VISIBLE);
			createQuickCallBtn.setVisibility(View.VISIBLE);
			callBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callContact(peopleId, displayName);
				}
			});
			
			sendMessageBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					sendMessage(peopleId, displayName);
				}
			});
			
		} else {
			createQuickCallBtn.setVisibility(View.GONE);
			callBtn.setVisibility(View.GONE);
			sendMessageBtn.setVisibility(View.GONE);
		}
		
		if(mailList.size() > 0){
			sendEmailBtn.setVisibility(View.VISIBLE);
			sendEmailBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					sendPersonalEmail(cursor, peopleId);
				}
			});
			
		} else {
			sendEmailBtn.setVisibility(View.GONE);
		}
		
		viewContactBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				switchContactInfo(peopleId);
			}
		});
		
		editBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				editContact(personUri);
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showDeleteDialog(personUri);
			}
		});
		
		createQuickCallBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				createNewQuickCall(peopleId, displayName);
			}
		});
		
		dialog.show();
		DialogUtils.setDialogWidth(getActivity(), dialog);
	}*/
	
	/*private void createNewQuickCall(long peopleId, String name){
		List<TaggedContactPhoneNumber> list = ContactsUtils.getPersonalContactPhoneNumbers(getActivity(), peopleId);
		if(list == null || list.isEmpty()){
//			if(LogLevel.DEV){
//				DevLog.e(TAG, "calContact : list is null");
//			}
			return;
		} else if (list.size() == 1){
			TaggedContactPhoneNumber numbers = list.get(0);
			if(QuickCallUtils.hasQuickTraceSet(getActivity(), numbers.originalNumber)){
				QuickCallInfo info = QuickCallUtils.getQuickCallInfoByNumber(getActivity(), numbers.originalNumber);
				if(info != null){
					Toast.makeText(getActivity(), R.string.this_number_has_been_set, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getActivity(), QuickCallChangeActivity.class);
					intent.putExtra(QuickCallConstants.EXTRA_SELECT_QUICK_CALL_ID, info._id);
					getActivity().startActivity(intent);
					return;
				}
			} 
				
			Intent intent = new Intent(getActivity(), CreateQuickCallActivity.class);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, name);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, numbers.originalNumber);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, numbers.photo_id);
			getActivity().startActivity(intent);
			
			return;
		}
		
		new PhoneSelectorDialog(getActivity(), list, name, PhoneSelectorDialog.TYPE_SELECT_CONTACT).show();
	}*/
	private void callContact(long peopleId, String name){
		List<TaggedContactPhoneNumber> list = ContactsUtils.getPersonalContactPhoneNumbers(ContactsFragment.this, peopleId);
		if(list == null || list.isEmpty()){
			if(LogLevel.DEV){
				DevLog.e(TAG, "calContact : list is null");
			}
			return;
		} else if (list.size() == 1){
			TaggedContactPhoneNumber tagNum = list.get(0);
			if(tagNum == null){
				return;
			}
			DialUtils.callNumber(ContactsFragment.this, tagNum.originalNumber);
			return;
		}
		
		new PhoneSelectorDialog(ContactsFragment.this, list, name, PhoneSelectorDialog.TYPE_CALL).show();
	}
	
	private void sendMessage(long peopleId, String name){
		List<TaggedContactPhoneNumber> list = ContactsUtils.getPersonalContactPhoneNumbers(ContactsFragment.this, peopleId);
		if(list == null || list.isEmpty()){
			if(LogLevel.DEV){
				DevLog.e(TAG, "senMessage : list is null");
			}
			return;
		} else if (list.size() == 1){
			TaggedContactPhoneNumber tagNum = list.get(0);
			if(tagNum == null){
				return;
			}
			DialUtils.sendMessage(ContactsFragment.this, tagNum.originalNumber);
			return;
		}
		
		new PhoneSelectorDialog(ContactsFragment.this, list, name, PhoneSelectorDialog.TYPE_MESSAGE).show();
	}
	
	private boolean sendPersonalEmail(Cursor cursor, long personId) {
		if (personId == -1) {
			return false;
		}

		List<EmailContact> emails = ContactsUtils.getEmailAddresses(ContactsFragment.this, personId);
		if (emails.size() == 0) {
			return false;
		} else if (emails.size() == 1) {
			EmailSender.sendEmail(ContactsFragment.this, new String[]{emails.get(0).emailAddress}, null, null);
		} else {
			EmailSelectorDialog emailDialog = new EmailSelectorDialog(ContactsFragment.this, emails);
			emailDialog.show();
		}

		return true;
	}
	
	private void editContact(final Uri personUri) {
		Intent intent = new Intent(Intent.ACTION_EDIT, personUri);
		try {
			startActivityForResult(Intent.createChooser(intent, getString(R.string.menu_editContact)), EDIT_CONTACT_REQUEST);
		} catch (android.content.ActivityNotFoundException e) {
			if (LogLevel.MARKET) {
				MarketLog.e(TAG, "editItem(): " + e.getMessage());
			}
		}
	}
	
	private void showDeleteDialog(final Uri personUri){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(ContactsFragment.this);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setTitle(R.string.delete_contact);
		builder.setMessage(R.string.delete_contact_message);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				try {
					ContactsFragment.this.getContentResolver().delete(personUri, null, null);
					startQuery();
				} catch (SQLiteException e) {
					if (LogLevel.DEV) {
						DevLog.e(TAG, "Error deleting contact.", e);
					} else if (LogLevel.MARKET) {
						MarketLog.e(TAG, "Cannot delete contact: SQLite error.");
					}
					DialogUtils.showQuickCallAlertDialog(ContactsFragment.this, R.string.delete_contact_failed_title, R.string.delete_contact_failed);
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		DialogUtils.setDialogWidth(ContactsFragment.this, dialog);
	}
		
	private void switchContactInfo(long personId){
		Intent intent = new Intent(ContactsFragment.this, ContactInfoActivity.class);
		intent.putExtra(QuickCallConstants.EXTRA_CONTACT_PERSON_ID, personId);
		ContactsFragment.this.startActivity(intent);
		ContactsFragment.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
