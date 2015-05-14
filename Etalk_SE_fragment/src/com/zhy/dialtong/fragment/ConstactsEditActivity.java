package com.zhy.dialtong.fragment;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.dialtong.ContactBean;
import com.zhy.dialtong.R;
import com.zhy.dialtong.view.HomeTabHostAcitivity;

public class ConstactsEditActivity extends Activity implements OnClickListener{
	
	private TextView contacts_back,contacts_check;
	private EditText edit_phonenumber,edit_name;
	private Button contacts_delete;
	private ImageView contacts_imageView,star;
	
	private ContactBean contactBean;
	
	private String phoneNumber = null;
	private String constactName = null;
	
	private Context context;
	
	private Uri imageUri;
	
	private static final int CAMERA_PIC_REQUEST = 2500;
	private static final int SYS_PICTURE = 2600;
	private static final int CROP_PHOTO = 2400;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail_edit_page);

//		status = getIntent().getBooleanExtra("status", false);
		phoneNumber = getIntent().getStringExtra("phoneNumber");
		constactName = getIntent().getStringExtra("name");

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		contacts_back = (TextView) findViewById(R.id.contacts_back);
		contacts_back.setOnClickListener(this);
		
		contacts_check = (TextView) findViewById(R.id.contacts_check);
		contacts_check.setOnClickListener(this);
		
		edit_phonenumber = (EditText) findViewById(R.id.edit_phonenumber);
		edit_phonenumber.setOnClickListener(this);
		
		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_name.setOnClickListener(this);
		
		contacts_delete = (Button) findViewById(R.id.contacts_delete);
		contacts_delete.setOnClickListener(this);
		
//		star = (ImageView) findViewById(R.id.star);
//		contactBean = new ContactBean();
//		if (contactBean.getFavourite() == 0) {
//			star.setImageResource((R.drawable.star));
//		} else {
//			star.setImageResource((R.drawable.star_clicked));
//		}
//		star.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				contactBean = new ContactBean();
//				if (contactBean.getFavourite() == 0) {
//					star.setImageResource((R.drawable.star));
//					contactBean.setFavourite(0);
//				} else {
//					star.setImageResource((R.drawable.star_clicked));
//					contactBean.setFavourite(1);
//				}
//				
//			}
//		});
		
		contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
		contacts_imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//		        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
				showEditPhoto(editphoto);
			}
		});
		
		
		editConstacts();
	}
	
	private String[] editphoto = new String[] { "拍照", "本地图片"};
	
	private void showEditPhoto(final String[] arg ){
		new AlertDialog.Builder(this).setTitle("请选择").setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				Uri uri = null;
				switch(which){
				case 0:
//					File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");
//					try {
//						if (outputImage.exists()) {
//							outputImage.delete();
//						}
//						outputImage.createNewFile();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					imageUri = Uri.fromFile(outputImage);
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			        break;
				case 1:
//					File outputImage1 = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
//					try {
//						if (outputImage1.exists()) {
//							outputImage1.delete();
//						}
//						outputImage1.createNewFile();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					imageUri = Uri.fromFile(outputImage1);
					Intent sysphotoIntent = 
					new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					sysphotoIntent.putExtra("crop", true);
					sysphotoIntent.putExtra("scale", true);
					sysphotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(sysphotoIntent, CROP_PHOTO);
					break;
				}
			}
		}).show();
	}

	private void editConstacts() {
		// TODO Auto-generated method stub
		if (constactName != null) {
			Cursor cur = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					ContactsContract.Contacts.DISPLAY_NAME + "=?",
					new String[] { constactName },
					ContactsContract.Contacts.DISPLAY_NAME
							+ " COLLATE LOCALIZED ASC");
			if (cur.moveToFirst()) {
				String contactId = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String disPlayName = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				edit_name.setText(disPlayName);
				int phoneCount = cur
						.getInt(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (phoneCount > 0) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						int count = 0;
//						do {
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String phoneType = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//							if (count == 0) {
								edit_phonenumber.setText(phoneNumber);
//							} else if (count == 1) {
//								et_tellphone.setText(phoneNumber);
//							}
							count++;
//				} while (phones.moveToNext());
			}
		}
			}
		}
				
		
	}
	
//	public void ClickedImage(View view) {
//		ImageView imageView = (ImageView) findViewById(R.id.star);
//		
//		if (contactBean.getFavourite() == 1) {
//			imageView.setImageResource((R.drawable.star));
//			contactBean.setFavourite(0);
//		} else {
//			imageView.setImageResource((R.drawable.star_clicked));
//			contactBean.setFavourite(1);
//		}
//		
//	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	switch (resultCode) { 
    	case CAMERA_PIC_REQUEST: 
    		if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
//    			Intent intent = new Intent("com.android.camera.action.CROP");
//    			intent.setDataAndType(imageUri, "image/*");
//    			intent.putExtra("scale", true);
//    			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//    			startActivityForResult(intent, CROP_PHOTO);
    			Bitmap image = (Bitmap) data.getExtras().get("data");
    			contacts_imageView = (ImageView) findViewById(R.id.contacts_imageView);
    			contacts_imageView.setImageBitmap(image);
    		}
    		break;
    	case CROP_PHOTO:
    		if (resultCode == RESULT_OK) {
    			try {
    				Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
    				contacts_imageView.setImageBitmap(bitmap);
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			}
    		}
    		break;
//    	case Activity.RESULT_OK://������ɵ��ȷ��
//    		String sdStatus = Environment.getExternalStorageState(); 
//    		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
//    			Log.v("TestFile", "SD card is not avaiable/writeable right now."); 
//    			return;
//    		}
//    		Bundle bundle = data.getExtras(); 
//    		Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ 
//    		FileOutputStream b = null; 
//    		File file = new File("/ext_sd/DTContactPhoto/"); 
//    		file.mkdirs();// �����ļ��У�����Ϊpk4fun 
    		// ��Ƭ��������Ŀ���ļ����£��Ե�ǰʱ�����ִ�Ϊ���ƣ�����ȷ��ÿ����Ƭ���Ʋ���ͬ����������������Demo�������Ƭ���ƶ�д���ˣ���ᷢ���������ն���
    		//�ţ���һ���ܻ��ǰһ����Ƭ���ǡ�ϸ�ĵ�ͬѧ��������������ַ�����������ϡ��ɣͣǡ������ȣ�Ȼ��ͻᷢ��sd����myimage����ļ����£�
    		//�ᱣ��ոյ�������ĳ�������Ƭ����Ƭ���Ʋ����ظ��� 
//    		String str = null; 
//    		Date date = null; 
//    		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// ��ȡ��ǰʱ�䣬��һ��ת��Ϊ�ַ��� 
//    		date = new Date(resultCode); 
//    		str = format.format(date); 
//    		String fileName = "/sdcard/myImage/" + str + ".jpg"; 
//    		sendBroadcast(fileName); 
//    		try {
//    			b = new FileOutputStream(fileName); 
//    			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// ������д���ļ� 
//    		} catch (FileNotFoundException e) { 
//    			e.printStackTrace(); 
//    		} finally { 
//    			try { 
//    				b.flush(); 
//    				b.close(); 
//    			} catch (IOException e) { 
//    				e.printStackTrace(); 
//    			}
//    		}break; 
    		
//    	case Activity.RESULT_CANCELED:// ȡ�� 
//    		break; 
//    	}
//    	break; 
//    	switch (resultCode) { 
    	case SYS_PICTURE: 
//    	case Activity.RESULT_OK: { 
    		if (requestCode == SYS_PICTURE && resultCode == RESULT_OK) {
    			
    		
    	Uri uri = data.getData(); 
    	Cursor cursor = context.getContentResolver().query(uri, null, 
    	null, null, null); 
    	cursor.moveToFirst(); 
    	String imgNo = cursor.getString(0); // ͼƬ��� 
    	String imgPath = cursor.getString(1); // ͼƬ�ļ�·�� 
    	String imgSize = cursor.getString(2); // ͼƬ��С 
    	String imgName = cursor.getString(3); // ͼƬ�ļ��� 
    	cursor.close(); 
    	// Options options = new BitmapFactory.Options(); 
    	// options.inJustDecodeBounds = false; 
    	// options.inSampleSize = 10; 
    	// Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options); 
//    	} 
//    	break; 
//    	case Activity.RESULT_CANCELED:// ȡ�� 
//    	break; 
//    	} 
    		}
    	break; 
    	}
    	} 
        // Checks to make sure the right Request and there is a proper result to store
//        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            ImageView imageview = (ImageView) findViewById(R.id.contacts_imageView);
//            imageview.setImageBitmap(image);}
//        }

	private void sendBroadcast(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contacts_delete:
			AlertDialog.Builder delete = new Builder(ConstactsEditActivity.this);
			delete.setMessage("ȷ��ɾ����ϵ��?");
			delete.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								delete();
								Toast.makeText(ConstactsEditActivity.this,
										"��ϵ����ɾ��", Toast.LENGTH_SHORT).show();
								finish();
							} catch (Exception e) {
								Toast.makeText(ConstactsEditActivity.this,
										"ɾ��ʧ��", Toast.LENGTH_SHORT).show();
							}

						}
					});
			delete.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}

					});
			delete.show();
			break;
		case R.id.contacts_check:
			AlertDialog.Builder update = new Builder(
					ConstactsEditActivity.this);
			update.setMessage("ȷ���޸���ϵ��?");
			update.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							try {
								update(//constactName,
										edit_name.getText().toString().trim(),
//										et_company.getText().toString()
//												.trim(),
//										et_post.getText().toString().trim(),
//										et_mobilephone.getText().toString()
//												.trim(), 
										edit_phonenumber
												.getText().toString()
												.trim());
//												et_email.getText()
//												.toString().trim(), et_qq
//												.getText().toString()
//												.trim(), et_address
//												.getText().toString()
//												.trim(), et_remark
//												.getText().toString()
//												.trim()
								Toast.makeText(ConstactsEditActivity.this,
										"�޸ĳɹ�", Toast.LENGTH_SHORT)
										.show();
								finish();
							} catch (Exception e) {
								Toast.makeText(ConstactsEditActivity.this,
										"�޸�ʧ��", Toast.LENGTH_SHORT)
										.show();
							}

						}
					});
			update.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
						}

					});
			update.show();
			break;
		case R.id.contacts_back:
			finish();
			Intent edit=new Intent(ConstactsEditActivity.this,HomeTabHostAcitivity.class);//onPageSelected(2)
			startActivity(edit);
			break;
								
		default:
			break;
								
			
		}
		
	}
	
	private void update( String name, 
			String mobilephone) {
		Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
 				new String[] { Data.RAW_CONTACT_ID },
				ContactsContract.Contacts.DISPLAY_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		String id = cursor
				.getString(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
		cursor.close();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		// name
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?",
						new String[] { String.valueOf(id),
								StructuredName.CONTENT_ITEM_TYPE })
				.withValue(StructuredName.DISPLAY_NAME, name).build());
		// ��˾
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?",
//						new String[] { String.valueOf(id),
//								Organization.CONTENT_ITEM_TYPE })
//				.withValue(Organization.COMPANY, company).build());
		//��
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?",
//						new String[] { String.valueOf(id),
//								Organization.CONTENT_ITEM_TYPE })
//				.withValue(Organization.TITLE, post).build());
		// ��绰����
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?"
								+ " AND " + Phone.TYPE + "=?",
						new String[] { String.valueOf(id),
								Phone.CONTENT_ITEM_TYPE,
								String.valueOf(Phone.TYPE_MOBILE) })
				.withValue(Phone.NUMBER, mobilephone).build());
		//���ֻ�����
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?"
//								+ " AND " + Phone.TYPE + "=?",
//						new String[] { String.valueOf(id),
//								Phone.CONTENT_ITEM_TYPE,
//								String.valueOf(Phone.TYPE_HOME) })
//				.withValue(Phone.NUMBER, tellphone).build());

		// ��email
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?"
//								+ " AND " + Email.TYPE + "=?",
//						new String[] { String.valueOf(id),
//								Email.CONTENT_ITEM_TYPE,
//								String.valueOf(Email.TYPE_WORK) })
//				.withValue(Email.DATA, email).build());
		// ��qq
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?",
//						new String[] { String.valueOf(id),
//								Im.CONTENT_ITEM_TYPE })
//				.withValue(Im.DATA, qq).build());
		// ���ַ
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?",
//						new String[] { String.valueOf(id),
//								ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE })
//				.withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address).build());

		// �汸ע
//		ops.add(ContentProviderOperation
//				.newUpdate(ContactsContract.Data.CONTENT_URI)
//				.withSelection(
//						Data.RAW_CONTACT_ID + "=?" + " AND "
//								+ ContactsContract.Data.MIMETYPE + " = ?",
//						new String[] { String.valueOf(id),
//								Note.CONTENT_ITEM_TYPE })
//				.withValue(Note.NOTE, note).build());
		//����Ƭ
//		ops.add(ContentProviderOperation
//		.newUpdate(ContactsContract.Data.CONTENT_URI)
//		.withSelection(
//				Data.RAW_CONTACT_ID + "=?" + " AND "
//						+ ContactsContract.Data.MIMETYPE + " = ?",
//				new byte[] { String.valueOf(id),
//						Note.CONTENT_ITEM_TYPE })
//		.withValue(Note.NOTE, note).build());
//		ContentValues values = new ContentValues();
		// ����Ĳ��������RawContacts�������е�rawContactIdʹ������Զ���������ϵ�˵�rawContactId 
//		Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);  
		// ��data�����ͷ������  
//		Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
//		long rawContactId = ContentUris.parseId(rawContactUri);
//		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// ��Bitmapѹ����PNG���룬����Ϊ100%�洢 
//		sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//		byte[] avatar = os.toByteArray();
//		values.put(Data.RAW_CONTACT_ID, rawContactId); 
//		values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE); 
//		values.put(Photo.PHOTO, avatar);
//		getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  values); 
//
//		try {
//			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//		} catch (Exception e) {
//
//		}
	}

	/**
	 * 
	 */
	private void delete() {
		Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },
				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { constactName }, null);
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		if (cursor.moveToFirst()) {
			do {
				long Id = cursor.getLong(cursor
						.getColumnIndex(Data.RAW_CONTACT_ID));
				ops.add(ContentProviderOperation
						.newDelete(
								ContentUris.withAppendedId(
										RawContacts.CONTENT_URI, Id)).build());
				try {
					getContentResolver().applyBatch(ContactsContract.AUTHORITY,
							ops);
				} catch (Exception e) {

				}
			} while (cursor.moveToNext());
			cursor.close();
		}
	}

}
