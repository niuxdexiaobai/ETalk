package com.zhy.dialtong.fragment;


import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.contacts.ActionSheet;
import com.zhy.dialtong.fragment.contacts.ActionSheet.ActionSheetListener;
import com.zhy.dialtong.fragment.contacts.Contact;
import com.zhy.dialtong.fragment.contacts.ContactInfo;
import com.zhy.dialtong.fragment.contacts.ContactReader;
import com.zhy.dialtong.fragment.contacts.TaggedContactPhoneNumber;
import com.zhy.dialtong.view.HomeTabHostAcitivity;

public class ConstactsDetailActivity extends FragmentActivity

 implements OnClickListener,ActionSheetListener{
	
	private TextView contacts_back,contacts_edit,contact_detail_name_show,contact_detail_num_show;
	private ImageView contacts_imageView;
	private ContactBean contactBean;
	private Context ctx;
	private int id;                  // id for contact reference
	private ImageButton star;
	
	private static final String ADD_CONTACTS = "添加联系人";
	private static final String DEL_CONTACTS = "删除联系人";
	private static final String EDIT_CONTACTS = "编辑联系人";
	
	private ContactInfo mContactInfo;
	
	private ImageButton mBackButton;
	private ImageButton mEditButton;
	private TextView mNameView;
	private TextView mCompanyView;
	private ImageView mPhotoView;
	private LinearLayout mContactInfoPhoneTitleView;
	private LinearLayout mContactInfoPhoneLayout;
	private LinearLayout mContactInfoMailTitleView;
	private LinearLayout mContactInfoMailLayout;
	private View mNoContactView;
	
	private PopupWindow mPopupWindow;
	
//	String[] EDIT_ITEM_1 = new String[] {ADD_CONTACTS,DEL_CONTACTS,EDIT_CONTACTS};
//	enum EDIT_ITEM {ADD_CONTACTS,DEL_CONTACTS,EDIT_CONTACTS};
	
	private static final int REQUEST_PHOTO = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_detail);//contact_info_layout,
		
		init();
//		buildLayout();
	}
	
	/*private void buildLayout(){
		mBackButton = (ImageButton) this.findViewById(R.id.button_back);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mEditButton = (ImageButton) this.findViewById(R.id.button_little_edit);
		mEditButton.setOnClickListener(new View.OnClickListener() {
			
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
		
		mNameView = (TextView) this.findViewById(R.id.name_view);
		mCompanyView = (TextView) this.findViewById(R.id.company_view);
		mPhotoView = (ImageView) this.findViewById(R.id.photo_view);
		mContactInfoPhoneTitleView = (LinearLayout) this.findViewById(R.id.contact_info_phone_title);
		mContactInfoPhoneLayout = (LinearLayout) this.findViewById(R.id.contact_info_phone_layout);
		mContactInfoMailTitleView = (LinearLayout) this.findViewById(R.id.contact_info_mail_title);
		mContactInfoMailLayout = (LinearLayout) this.findViewById(R.id.contact_info_mail_layout);
		mNoContactView = this.findViewById(R.id.no_contact_info_view);
		mNoContactView.setVisibility(View.GONE);
	}
	
	private void initPopupWindow(){
		LayoutInflater inflater = LayoutInflater.from(this); 
		View view = inflater.inflate(R.layout.popup_of_contact_info, null); 
		View buttonCreateQuick = view.findViewById(R.id.button_popup_create_new_quick_call);
		View buttonEditContact = view.findViewById(R.id.button_popup_edit_contact);
		View buttonDeleteContact = view.findViewById(R.id.button_popup_delete_contact);
		buttonCreateQuick.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				createNewQuickCall(mContactInfo.contactId, mContactInfo.displayName);
			}
		});
		
		buttonEditContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, mContactInfo.contactId);
				editContact(personUri);
			}
		});
		
		buttonDeleteContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, mContactInfo.contactId);
				showDeleteDialog(personUri);
			}
		});
		
		mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false); 
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable()); 
		mPopupWindow.setOutsideTouchable(true); 
		mPopupWindow.setFocusable(true); 	
	}
	
	private void createNewQuickCall(long peopleId, String name){
		List<TaggedContactPhoneNumber> list = ContactsUtils.getPersonalContactPhoneNumbers(this, peopleId);
		if(list == null || list.isEmpty()){
			if(LogLevel.DEV){
				DevLog.e(TAG, "sendMessage : list is null");
			}
			return;
		} else if (list.size() == 1){
			TaggedContactPhoneNumber numbers = list.get(0);
			Intent intent = new Intent(ContactInfoActivity.this, CreateQuickCallActivity.class);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, name);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, numbers.originalNumber);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, numbers.photo_id);
			ContactInfoActivity.this.startActivity(intent);
			return;
		}
		
		new PhoneSelectorDialog(this, list, name, PhoneSelectorDialog.TYPE_SELECT_CONTACT).show();
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
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setTitle(R.string.delete_contact);
		builder.setMessage(R.string.delete_contact_message);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				try {
					getContentResolver().delete(personUri, null, null);
				} catch (SQLiteException e) {
					if (LogLevel.DEV) {
						DevLog.e(TAG, "Error deleting contact.", e);
					} else if (LogLevel.MARKET) {
						MarketLog.e(TAG, "Cannot delete contact: SQLite error.");
					}
					DialogUtils.showQuickCallAlertDialog(ContactInfoActivity.this, R.string.delete_contact_failed_title, R.string.delete_contact_failed);
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
		DialogUtils.setDialogWidth(this, dialog);
	}*/
	
	private void init() {
		// TODO Auto-generated method stub
		
//		// Intent from past Activity
//		Intent intent = getIntent();
//				
//		// Get the contact from the intent 
//		id = (int) intent.getIntExtra("id",0);
//		
//		// Instantiate the DatabaseHandler
//		db = new DatabaseHandler(this);
//		contactBean = db.getContact(id);
		
		contacts_back = (TextView) findViewById(R.id.contacts_back);
		contacts_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent intent=new Intent(ConstactsDetailActivity.this,HomeTabHostAcitivity.class);
				startActivityForResult(intent, 1008);
//				finish();
			}
		});
		
		/*contacts_edit = (TextView) findViewById(R.id.contacts_edit);
		contacts_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				//�����2�д�������ʾ��ios��item���ֵģ����ǲ�֪����ô���õ���¼�
//				setTheme(R.style.ActionSheetStyleIOS7);
//				showActionSheet();
				Intent edit=new Intent(ConstactsDetailActivity.this,ConstactsEditActivity.class);
//				if(tv_add_edit.getText().toString().trim().equals("新建")){	
//					edit.putExtra("status", false);
//				}else{
//					edit.putExtra("status", true);
//				}
				edit.putExtra("phoneNumber", getIntent().getStringExtra("number"));
				edit.putExtra("name", getIntent().getStringExtra("name"));
//				edit.putExtra("name", getIntent().getStringExtra("name"));
				startActivity(edit);
			}
		});*/
		
		
		contact_detail_name_show = (TextView) findViewById(R.id.contact_detail_name_show);
		String name=getIntent().getStringExtra("name");
		contact_detail_name_show.setText(name);
		
		contact_detail_num_show = (TextView) findViewById(R.id.contact_detail_num_show);
		String number=getIntent().getStringExtra("number");
		contact_detail_num_show.setText(number);
		contact_detail_num_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + getIntent().getStringExtra("number"));
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				startActivity(it);
			}
		});
		
//		contactBean = new ContactBean();
//		
//		star = (ImageButton) findViewById(R.id.star);
//		if (contactBean.getStarred() == 1){
//			star.setImageResource((R.drawable.star_clicked));
//		}else
//		{
//			star.setImageResource((R.drawable.star));
//		}
		// Getting a reference to star image view and setting the correct
		// version represent if contact is a favorite or not
//		ImageView star = (ImageView) findViewById(R.id.star);
//		star.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (contactBean.getFavourite() == 0) {
//					star.setImageResource((R.drawable.star));
//					addKeepedContacts(getIntent().getStringExtra("ID"));
//				} else {
//					star.setImageResource((R.drawable.star_clicked));
//					removeKeepedContacts(getIntent().getStringExtra("ID"));
//				}
//			}
//		});
//		contactBean = new ContactBean();
		
//		contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
		
		// getting a reference to the imageView and setting it. 
//		Intent intent = getIntent();
//		id = (int) intent.getIntExtra("id",0);
//		Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);//λ��
//		//�����Ƭreturns the photo as a byte stream.
//		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
//		if (null == input){
//			contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
//		}else {
//		Bitmap contactPhoto = BitmapFactory.decodeStream(input);
//		byte[] photo = getIntent().getByteArrayExtra("photo");
//		Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
		contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
		ContactReader reader = ContactReader.getInstance(this);
		Contact contact = reader.getContactByNumber(number);
		Bitmap bitmap = reader.getContactPhoto(contact.getId());
		if (bitmap!=null){
			Drawable avatar = new BitmapDrawable(getResources(), bitmap);
			contacts_imageView.setBackgroundDrawable(avatar);
		}
		else{
			contacts_imageView.setImageResource(R.drawable.user);
		}
//		contacts_imageView.setImageBitmap(contactPhoto);
		}
//		ContactBean contactbean = (ContactBean) getIntent().getSerializableExtra("photo");
		
//		contacts_imageView.setImageBitmap(contactbean.);
//		contacts_imageView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Photo p = contactBean.getPhoto();
//				if (p == null)
//					return;
//				
//				FragmentManager fm = getSupportFragmentManager();
//				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
//				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
//			}
//		});
//		contacts_imageView.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(ConstactsDetailActivity.this, ConstactsImageCameraActivity.class);
//				startActivityForResult(i, REQUEST_PHOTO);
//				return true;
//			}
//		});

//	}
	
	/**
	* ��ӵ��ղؼ�
	* @param _id
	*/
	/*@SuppressWarnings("deprecation")
	public void addKeepedContacts(long _id){
	ContentResolver contentResolver = getContentResolver();
	Cursor cusor = null;
	@SuppressWarnings("deprecation")
	String[] projection = new String[] { Contacts.People._ID, Contacts.People.NAME, Contacts.People.NUMBER };
	cusor = contentResolver.query(Contacts.People.CONTENT_URI, projection, Contacts.People._ID + "= ", new String[] { _id + "" }, Contacts.People.NAME + " ASC");
	    cusor.moveToFirst();
	ContentValues values = new ContentValues();
	Uri uri = Uri.withAppendedPath(Contacts.People.CONTENT_URI, cusor.getString(cusor.getColumnIndex(Contacts.People._ID)));
	// values.put(Contacts.People.NAME, newName);
	values.put(Contacts.People.STARRED, 1);
	// values.put(Contacts.Phones.NUMBER, newPhoneNum);
	contentResolver.update(uri, values, null, null);
	Toast.makeText(this, this.getResources().getString(R.string.add_succeed), Toast.LENGTH_SHORT).show();
	}
	
	*//**
	* ���ղؼ����Ƴ�
	* @param _id
	*/
	/*@SuppressWarnings("deprecation")
	public void removeKeepedContacts(long _id){
	ContentResolver contentResolver = getContentResolver();
	Cursor cusor = null;
	@SuppressWarnings("deprecation")
	String[] projection = new String[] { Contacts.People._ID, Contacts.People.NAME, Contacts.People.NUMBER };
	    cusor = contentResolver.query(Contacts.People.CONTENT_URI, projection, Contacts.People._ID + "= ", new String[] { _id + "" }, Contacts.People.NAME + " ASC");
	cusor.moveToFirst();
	ContentValues values = new ContentValues();
	Uri uri = Uri.withAppendedPath(Contacts.People.CONTENT_URI, cusor.getString(cusor.getColumnIndex(Contacts.People._ID)));
	// values.put(Contacts.People.NAME, newName);
	values.put(Contacts.People.STARRED, 0);
	// values.put(Contacts.Phones.NUMBER, newPhoneNum);
	contentResolver.update(uri, values, null, null);
	new getKeepedContactsTask().execute((Void)null);
	Toast.makeText(ContactActivity.this, ContactActivity.this.getResources().getString(R.string.remove_succeed), Toast.LENGTH_SHORT).show();
	}*/
	
	public void ClickedImage(View view) {
		ImageView imageView = (ImageView) findViewById(R.id.star);
		if (ContactsContract.Contacts.STARRED +" =  1 " != null) {//,contactBean.getStarred() == 1
//			contactBean.setStarred(0);
//			contactBean.setFavourite(0);
//			ContentValues v = new ContentValues();
//			v.put(ContactsContract.Contacts.STARRED,0);
//			getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, 
//					v, ContactsContract.Contacts.Data._ID+"=?", 
//					new String[]{getIntent().getStringExtra("number")+""});
			imageView.setImageResource((R.drawable.star));
		} else {
			Log.v("star", "isclick");
//			contactBean.setFavourite(1);
//			contactBean.setStarred(1);
//			ContentValues v = new ContentValues();
//			v.put(ContactsContract.Contacts.STARRED,0);
//			getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, 
//					v, ContactsContract.Contacts.Data.DATA1+"=?", 
//					new String[]{getIntent().getStringExtra("number")+""});
			imageView.setImageResource((R.drawable.star_clicked));
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(1008 == requestCode){
			ContactsActivity.init();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void showActionSheet() {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("ȡ��")
				.setOtherButtonTitles(ADD_CONTACTS, DEL_CONTACTS, EDIT_CONTACTS)//, "Item4"
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "click item index = " + index,
//				0).show();
		
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		// TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "dismissed isCancle = " + isCancle, 0).show();
//		 if (EDIT_ITEM_1)
	}

}
