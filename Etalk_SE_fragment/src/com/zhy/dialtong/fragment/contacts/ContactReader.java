/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.contacts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.AggregationSuggestions;
import android.provider.ContactsContract.Groups;
import android.util.Log;
import static android.provider.ContactsContract.CommonDataKinds.*;

/**
 * 提供联系人的读取方式。
 * @author Muyangmin
 * @create 2014-8-1
 * @version 1.0
 */
public final class ContactReader {	
	private static final String LOG_TAG = "ContactReader";
	private ContentResolver resolver;
	private static ContactReader instance = null;
	
	/**
	 * Singleton。
	 */
	private ContactReader(Context context){
		resolver = context.getContentResolver();
	}
	
	/**
	 * 获得Reader的实例。
	 * @param context 上下文信息。
	 * @return 返回Reader的实例。
	 * @lastModify 2014-8-1 by Muyangmin
	 */
	public static ContactReader getInstance(Context context){
		if (instance==null){
			synchronized (ContactReader.class) {
				if (instance == null){
					instance = new ContactReader(context);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获得系统所有的联系人一般信息。
	 * <p class="note"><strong>注意：</strong>
	 * 		 当某个联系人的电话号码超过一个时，只取第一个查询到的号码，其余丢弃。
	 * </p>
	 * @return 返回联系人的列表，按照中文拼音排序，无法获取拼音的，其排序值为#符号。
	 * @lastModify 2014-8-1 by Muyangmin
	 */
	//FIXME 增加常用号码机制，以代替现在的第一号码筛选。
	public ArrayList<Contact> getAllContacts(){
		String[] projecttion = new String[]{
//				Data.RAW_CONTACT_ID,
				Phone.CONTACT_ID,
				Phone.DISPLAY_NAME,
				Phone.LOOKUP_KEY,
				Phone.NUMBER,
				Phone.SORT_KEY_PRIMARY,
				Phone.STARRED
				};
		String selection = Contacts.IN_VISIBLE_GROUP+"=1 and "
				+Phone.HAS_PHONE_NUMBER +"=1 and "
				+Phone.DISPLAY_NAME+" IS NOT NULL";
		//直接按照SORT_KEY排序
		Cursor cursor = resolver.query(Phone.CONTENT_URI, 
				projecttion, selection, null, Phone.SORT_KEY_PRIMARY);
		if (cursor==null){
			handleNullCursor();
			return null;
		}
		ArrayList<Contact> list = new ArrayList<Contact>();
		HashSet<Long> idset = new HashSet<Long>();
		while (cursor.moveToNext()){
			Contact contact = parseCursorToContact(cursor);
			if (!idset.contains(contact.getId())){
				idset.add(contact.getId());
				list.add(contact);
			}
			//如果已经存在，则什么也不做，即丢弃了这样的结果。
		}
		Log.i(LOG_TAG, "查询到"+cursor.getCount()+"行，系统中有"+list.size()+"位联系人。");
		cursor.close();
		return list;
	}
	
	/**
	 * 获得与给定名字以任意模式（全拼、汉字、模糊拼音等）匹配的联系人信息。
	 * @param displayName 显示的名字或拼音
	 * @return 所有匹配人的数组
	 */
	public ArrayList<Contact> getLikelyContacts(String displayName){
		StringBuilder builder = new StringBuilder(50);
		for (int i=0; i<displayName.length(); i++){
			builder.append("%").append(displayName.charAt(i));
		}
		String regex = builder.append("%").toString();
		String[] projecttion = new String[]{
				Phone.CONTACT_ID,
				Phone.DISPLAY_NAME,
				Phone.LOOKUP_KEY,
				Phone.NUMBER,
				Phone.SORT_KEY_PRIMARY,
				Phone.STARRED
				};
		String selection = Contacts.IN_VISIBLE_GROUP+"=1 and "
				+Phone.HAS_PHONE_NUMBER +"=1 and "
				+Phone.DISPLAY_NAME+" IS NOT NULL and "
				+Phone.SORT_KEY_PRIMARY+" LIKE ?";
		//直接按照SORT_KEY排序
		Cursor cursor = resolver.query(Phone.CONTENT_URI, 
				projecttion, selection, new String[]{regex}, Phone.SORT_KEY_PRIMARY);
		if (cursor==null){
			handleNullCursor();
			return null;
		}
		ArrayList<Contact> list = new ArrayList<Contact>();
		HashSet<Long> idset = new HashSet<Long>();
		while (cursor.moveToNext()){
			Contact contact = parseCursorToContact(cursor);
			if (!idset.contains(contact.getId())){
				idset.add(contact.getId());
				list.add(contact);
			}
			//如果已经存在，则什么也不做，即丢弃了这样的结果。
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 根据电话号码查询联系人。
	 * @param number 联系人的电话号码
	 * @return 返回查询到的联系人;如果不存在或出错，返回null.
	 */
	public Contact getContactByNumber(String number){
		if (number==null || number.length()==0){
			return null;
		}
		String[] projecttion = new String[]{
				Phone.CONTACT_ID,
				Phone.DISPLAY_NAME,
				Phone.LOOKUP_KEY,
				Phone.NUMBER,
				Phone.SORT_KEY_PRIMARY,
				Phone.STARRED
				};
		String selection = Contacts.IN_VISIBLE_GROUP+"=1 and "
				+Phone.NUMBER +"=?";
		//直接按照SORT_KEY排序
		Cursor cursor = resolver.query(Phone.CONTENT_URI, 
				projecttion, selection, new String[]{number}, Phone.SORT_KEY_PRIMARY);
		if (cursor !=null && cursor.moveToNext()){
			Contact contact = parseCursorToContact(cursor);
			cursor.close();
			return contact;
		}
		return null;
	}
	
	//将Cursor转换成Contact对象
	private Contact parseCursorToContact(Cursor cursor){
		Contact contact = new Contact();
		long id = cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));
		contact.setId(id);
		contact.setLookupKey(cursor.getString(cursor.getColumnIndex(Phone.LOOKUP_KEY)));
		contact.setName(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
		contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
		contact.setSortKey(cursor.getString(cursor.getColumnIndex(Phone.SORT_KEY_PRIMARY)));
		contact.setStarred(cursor.getInt(cursor.getColumnIndex(Phone.STARRED))==1? true :false);
		return contact;
	}
	
	/**
	 * 获得可用的Group信息，注意：这里假定联系人都应该存储在手机上。
	 * @return 返回包含联系人群组的HashMap，其Key为Group的Id，Value为名字。
	 * @lastModify 2014-8-13 by Muyangmin
	 */
	public HashMap<Long, String> getAvailableGroups(){
		return getAvailableGroups("Phone");
	}
	
	/**
	 * 获得可用的Group信息。
	 * @return 返回包含联系人群组的HashMap，其Key为Group的Id，Value为名字。
	 * @lastModify 2014-8-13 by Muyangmin
	 */
	public HashMap<Long, String> getAvailableGroups(String accountName){
		Cursor cursor = resolver.query(Groups.CONTENT_URI,null,
				Groups.DELETED+"=0 and "+Groups.GROUP_VISIBLE+"=1 and " 
						+Groups.GROUP_IS_READ_ONLY+"=0 and "
						+Groups.ACCOUNT_NAME+"=?", 
				new String[]{accountName}, null);
		if (cursor==null){
			handleNullCursor();
			return null;
		}
		HashMap<Long, String> groups = new HashMap<Long, String>();
		while (cursor.moveToNext()){
			groups.put(cursor.getLong(cursor.getColumnIndex(Groups._ID)),
					cursor.getString(cursor.getColumnIndex(Groups.TITLE)));
		}
		Log.i(LOG_TAG, groups.toString());
		return groups;
	}
	
	/**
	 * 获取指定联系人的详细信息。
	 * @param lookupKey 指定联系人的查找键。
	 * @return 返回该联系人的详细信息；如有错误，返回null。
	 * @lastModify 2014-8-13 by Muyangmin
	 */
	public ContactDetail getContactDetail(String lookupKey){
		if (lookupKey == null){
			return null;
		}
		String selection = Contacts.IN_VISIBLE_GROUP+"=1 ";
		Cursor cursor = resolver.query(
				//因为加入的LookupKey必须是未编码的，因此没有使用Uri.withAppendedPath(Uri, String)方法。
				Uri.parse(Contacts.CONTENT_LOOKUP_URI.toString()+"/"+lookupKey),
				/*null*/new String[]{
					Contacts._ID, Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME,
					Contacts.STARRED,
					Contacts.HAS_PHONE_NUMBER,
					AggregationSuggestions.PHOTO_URI,
					AggregationSuggestions.CUSTOM_RINGTONE
				}, selection, null, null);
		if (cursor==null){
			handleNullCursor();
			return null;
		}
		if (cursor.getCount()>1){
			handleMultiCursor();
		}
		ContactDetail detail = new ContactDetail();
		if (cursor.moveToNext()){
			detail.setId(cursor.getLong(cursor.getColumnIndex(Contacts._ID)));
			detail.setStarred(cursor.getInt(cursor.getColumnIndex(Contacts.STARRED))==1? true : false);
			detail.setLookupKey(cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY)));
			detail.setName(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
			detail.setRingtone(cursor.getString(cursor.getColumnIndex(AggregationSuggestions.CUSTOM_RINGTONE)));
			detail.setPhoto(cursor.getString(cursor.getColumnIndex(AggregationSuggestions.PHOTO_URI)));
			//读取电话号码
			if (cursor.getInt(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER))==1){
				//如果没有号码，则detail的Phone字段不予赋值，默认为Null
				detail.setPhone(getPhoneNumber(lookupKey));
			}
			detail.setGroup(getContactGroup(lookupKey));
//			detail.setExtProperties(getExtProperties(lookupKey));
		}
		cursor.close();
		return detail;
	}
	
	/**
	 * 返回联系人头像。
	 * @param contactId 联系人的Id。
	 * @return 返回小型头像;如果未能查询到，则返回的是一个null值。
	 */
	public Bitmap getContactPhoto(long contactId){
		InputStream bis = Contacts.openContactPhotoInputStream(resolver, 
				ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId));
		if(bis==null){
			Log.i(LOG_TAG, "contact has no photo.");
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeStream(bis);
		return bitmap;
	}
	
	/**
	 * 读取指定联系人的全部电话号码。
	 * @param lookupKey 要查找的联系人
	 * @return 返回该联系人的所有电话号码，Key为号码，Value为类型。
	 * @lastModify 2014-8-13 by Muyangmin
	 */
	private HashMap<String, String> getPhoneNumber(String lookupKey){
		String[] projection = new String[]{
				Phone.NUMBER,Phone.TYPE,Phone.LABEL,
		};
		Cursor cursor = resolver.query(
				ContactsContract.Data.CONTENT_URI,	
				projection,
				Contacts.LOOKUP_KEY + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
				new String[]{lookupKey,Phone.CONTENT_ITEM_TYPE},
				null);
		if (cursor==null){
			handleNullCursor();
			return null;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		while (cursor.moveToNext()){
			String key = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			String value = "手机";
			int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
			//用户自定义类型
			if (type==Phone.TYPE_CUSTOM){
				value = cursor.getString(cursor.getColumnIndex(Phone.LABEL));
			}
			//系统预定义类型，如果没有则不修改
//			else if (PhoneNumberUtil.getPredefinedPhoneType(type)!=null){
//				value = PhoneNumberUtil.getPredefinedPhoneType(type);
//			}
			map.put(key,value);
		}
//		Log.i(LOG_TAG, CursorUtil.getCursorContents(cursor));
		cursor.close();
		return map;
	}
	
	/**
	 * 获取指定联系人的全部所属群组信息。
	 * @param lookupKey 联系人的查找键
	 * @return 返回该联系人的全部所属群组信息，Key为ID，Value为标题；如果没有数据或查询出错，返回null
	 * @lastModify 2014-8-14 by Muyangmin
	 */
	private HashMap<Long, String> getContactGroup(String lookupKey){
		/*
		 * 使用子查询得到该联系人所属的所有群组，再使用in 关键字在Group表中查询所有相关的行。
		 * Groups类中有一个CONTENT_SUMMARY_URI字段，说明为与Data连接的查询，但实际上并不包含lookup字段，
		 * 故无法使用它进行筛选。
		 */
		Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI,
				new String[]{GroupMembership.GROUP_ROW_ID}, 
				Contacts.LOOKUP_KEY + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
				new String[]{lookupKey, GroupMembership.CONTENT_ITEM_TYPE}, null);
		if(cursor==null){
			handleNullCursor();
			return null;
		}
		//没有找到数据
		if (cursor.getCount()<=0){
			Log.i(LOG_TAG, "no group rows selected in cursor. skip it.");
			return null;
		}
		StringBuilder sb = new StringBuilder("(");
		while (cursor.moveToNext()){
			sb	.append(cursor.getString(cursor.getColumnIndex(GroupMembership.GROUP_ROW_ID)))
				.append(",");
		}
		cursor.close();
		sb	.deleteCharAt(sb.length()-1)
			.append(")");
		//-------------下面才是真正的查询
		Cursor groupCursor = resolver.query(Groups.CONTENT_URI, 
				new String[]{Groups._ID, Groups.TITLE}, Groups._ID+" in "+sb.toString(), null, 
				Groups._ID);
		if (groupCursor==null){
			handleNullCursor();
			return null;
		}
		HashMap<Long , String> map = new HashMap<Long, String>();
		while (groupCursor.moveToNext()){
			map.put(groupCursor.getLong(groupCursor.getColumnIndex(Groups._ID)), 
					groupCursor.getString(groupCursor.getColumnIndex(Groups.TITLE)));
		}
		Log.i(LOG_TAG, map.toString());
		groupCursor.close();
		return map;
	}
	
	/**
	 * 获取额外的信息，包含IM、事件、组织等信息。
	 * @param lookupKey 查找键值
	 * @lastModify 2014-8-23 by Muyangmin
	 */
	/*private String getExtProperties(String lookupKey){
		String[] projection = new String[]{
				ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1,
				ContactsContract.Data.DATA2,//TYPE
				ContactsContract.Data.DATA3,//LABEL
				ContactsContract.Data.DATA4,//TITLE
				ContactsContract.Data.DATA5,//DEPARTMENT, PROTOCAL
				ContactsContract.Data.DATA6,//CUSTOM_PROTOCAL
				ContactsContract.Data.DATA9,//OFFICE_LOCATION
				};
		Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, 
				projection, 
				Contacts.LOOKUP_KEY + "=? and " 
				+ ContactsContract.Data.MIMETYPE + "!=? and "
				+ ContactsContract.Data.MIMETYPE + "!=?",
				new String[]{lookupKey, Phone.CONTENT_ITEM_TYPE, GroupMembership.CONTENT_ITEM_TYPE},
				null);
		if (cursor==null){
			return null;
		}
		JSONObject json = new JSONObject();
		try {
			while (cursor.moveToNext()){
				String mime = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
				if (mime.equals(Email.CONTENT_ITEM_TYPE)){
					parseEmailData(json, cursor);
				}
				else if (mime.equals(Nickname.CONTENT_ITEM_TYPE)){
					json.put(SupportedProperties.PROPERTY_NICKNAME, 
							cursor.getString(cursor.getColumnIndex(Nickname.NAME)));
				}
				else if (mime.equals(Note.CONTENT_ITEM_TYPE)){
					json.put(SupportedProperties.PROPERTY_NOTE, 
							cursor.getString(cursor.getColumnIndex(Note.NOTE)));
				}
				else if (mime.equals(Organization.CONTENT_ITEM_TYPE)){
					//组织信息，包含公司地址、部门、职称、办公地点
					parseOrganizationData(json, cursor);
				}
				else if (mime.equals(Website.CONTENT_ITEM_TYPE)){
					parseWebsteData(json, cursor);
//					json.put(SupportedProperties.PROPERTY_WEBSITE, 
//							cursor.getString(cursor.getColumnIndex(Website.URL)));
				}
				else if (mime.equals(Im.CONTENT_ITEM_TYPE)){
					parseImData(json, cursor);
				}
				else if (mime.equals(Event.CONTENT_ITEM_TYPE)){
					parseEventData(json, cursor);
				}
			}// while block end
		} catch (JSONException e) {
			Log.w(LOG_TAG, "Cannot retrive extended properties!"+e.getMessage());
		}
		cursor.close();
		return json.toString();
	}*/
	/**
	 * 将Cursor的当行数据作为邮箱类型存储到json中。注意，该方法不做任何参数检查。
	 */
	/*private void parseEmailData(JSONObject json, Cursor cursor)
			throws JSONException{
		int type = cursor.getInt(cursor.getColumnIndex(Email.TYPE));
		switch (type) {
		case Email.TYPE_HOME:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EMAIL, 
					cursor.getString(cursor.getColumnIndex(Email.ADDRESS)),
					SupportedProperties.EMAIL_HOME);
			break;
		case Email.TYPE_WORK:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EMAIL, 
					cursor.getString(cursor.getColumnIndex(Email.ADDRESS)),
					SupportedProperties.EMAIL_WORK);
			break;
		default:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EMAIL,
					cursor.getString(cursor.getColumnIndex(Email.ADDRESS)),
					cursor.getString(cursor.getColumnIndex(Email.LABEL)));
			break;
		}
	}*/
	
	/**
	 * 将Cursor的当行数据作为组织类型存储到json中。注意，该方法不做任何参数检查。
	 */
	/*private void parseOrganizationData(JSONObject json, Cursor cursor)
			throws JSONException{
		putSecondary(json, 
				SupportedProperties.PROPERTY_ORGANIZATION, 
				SupportedProperties.ORG_COMPANY, 
				cursor.getString(cursor.getColumnIndex(Organization.COMPANY)));
		putSecondary(json, 
				SupportedProperties.PROPERTY_ORGANIZATION, 
				SupportedProperties.ORG_DEPARTMENT, 
				cursor.getString(cursor.getColumnIndex(Organization.DEPARTMENT)));
		putSecondary(json, 
				SupportedProperties.PROPERTY_ORGANIZATION, 
				SupportedProperties.ORG_TITLE, 
				cursor.getString(cursor.getColumnIndex(Organization.TITLE)));
		putSecondary(json, 
				SupportedProperties.PROPERTY_ORGANIZATION, 
				SupportedProperties.ORG_LOCATION, 
				cursor.getString(cursor.getColumnIndex(Organization.OFFICE_LOCATION)));
	}*/ 
	/**
	 * 将Cursor的当行数据作为IM类型存储到json中。注意，该方法不做任何参数检查。
	 */
	/*private void parseImData(JSONObject json, Cursor cursor)
			throws JSONException {
		int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
		switch (protocal) {
		case Im.PROTOCOL_QQ:
			putSecondary(json, 
					SupportedProperties.PROPERTY_IM, 
					cursor.getString(cursor.getColumnIndex(Im.DATA)),
					SupportedProperties.IM_QQ);
			break;
		case Im.PROTOCOL_MSN:
			putSecondary(json, 
					SupportedProperties.PROPERTY_IM, 
					cursor.getString(cursor.getColumnIndex(Im.DATA)),
					SupportedProperties.IM_MSN);
			break;
		default:
			String actualProtocal = cursor.getString(cursor.getColumnIndex(Im.CUSTOM_PROTOCOL));
			//猜测微信协议
			if (actualProtocal.equals(SupportedProperties.IM_PROTOCAL_WECHAT)
					|| actualProtocal.equalsIgnoreCase(SupportedProperties.IM_PROTOCAL_WECHAT1)
					|| actualProtocal.equalsIgnoreCase(SupportedProperties.IM_PROTOCAL_WECHAT2)){
				putSecondary(json, 
						SupportedProperties.PROPERTY_IM, 
						cursor.getString(cursor.getColumnIndex(Im.DATA)),
						SupportedProperties.IM_WECHAT);
			}
			else {
				//自定义类型
				putSecondary(json, 
						SupportedProperties.PROPERTY_IM,
						cursor.getString(cursor.getColumnIndex(Im.DATA)),
						actualProtocal);
			}
			break;
		}
	}*/
	
	/**
	 * 将Cursor的当行数据作为Event类型存储到json中。注意，该方法不做任何参数检查。
	 */
	/*private void parseEventData(JSONObject json, Cursor cursor)
			throws JSONException{
		int eventType = cursor.getInt(cursor.getColumnIndex(Event.TYPE));
		switch (eventType) {
		case Event.TYPE_ANNIVERSARY:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EVENT, 
					cursor.getString(cursor.getColumnIndex(Event.START_DATE)),
					SupportedProperties.EVENT_ANNIVERSARY);
			break;
		case Event.TYPE_BIRTHDAY:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EVENT,
					cursor.getString(cursor.getColumnIndex(Event.START_DATE)), 
					SupportedProperties.EVENT_BIRTH);
			break;
		default:
			putSecondary(json, 
					SupportedProperties.PROPERTY_EVENT, 
					cursor.getString(cursor.getColumnIndex(Event.START_DATE)),
					//自定义类型
					cursor.getString(cursor.getColumnIndex(Event.LABEL)));
			break;
		}//switch Event 结束
	}*/
	
	//TODO fix parse website
	/*private void parseWebsteData(JSONObject json, Cursor cursor)
			throws JSONException{
		int type = cursor.getInt(cursor.getColumnIndex(Website.TYPE));
		String siteType = null;
		switch (type) {
		case Website.TYPE_HOMEPAGE:	siteType = SupportedProperties.WEBSITE_HOMEPAGE;	break;
		case Website.TYPE_BLOG:		siteType = SupportedProperties.WEBSITE_BLOG;		break;
		case Website.TYPE_WORK:		siteType = SupportedProperties.WEBSITE_WORK;		break;
		default:	siteType = cursor.getString(cursor.getColumnIndex(Website.LABEL));	break;
		}
		putSecondary(json, 
				SupportedProperties.PROPERTY_WEBSITE,
				cursor.getString(cursor.getColumnIndex(Website.URL)), 
				siteType);
	}*/
	
	/**
	 * 将指定的数据存储在JSON字符串的二级目录中。
	 * </p>
	 * 		例如这样的调用：<br/>
	 * 		<code>putSecondary(json, "EVENT", "birthday", "2014-08-23");</code><br/>
	 * 		方法将首先检查json字符串中是否存在EVENT对象，如果没有，则创建一个JSONObject对象；如果有，
	 * 视为JSONObject取出。获得这样的JSONObject对象之后，再将"2014-08-23"作为birthday的值放入JSONObject对象。
	 * 最后，将这样的JSONObject放回json中。
	 * </p>
	 * @see JSONObject#put(String, Object)
	 * @param json	要放置值的对象
	 * @param firstKey 一级键
	 * @param secondKey 二级键
	 * @param secondValue 二级值，如果为null，则不会发生任何操作
	 * @throws JSONException 尝试放入值时发生任何JSON异常则抛出
	 * @lastModify 2014-8-23 by Muyangmin
	 */
	private void putSecondary(JSONObject json, String firstKey, String secondKey, String secondValue) 
			throws JSONException{
		if (json==null || secondValue ==null){
			return ;
		}
		JSONObject temp;
		try{
			temp= json.getJSONObject(firstKey);	
		}catch (JSONException e){
			//如果不存在或不是JSONObject对象，则新建一个
			temp = new JSONObject();
		}
		temp.put(secondKey, secondValue);
		json.put(firstKey, temp);
	}
	
//	/**
//	 * 获得Sort-Key的第一个字母。
//	 * @param str SortKey字符串
//	 * @return 如果参数的第一个字符是A-Z,则返回该字母；否则返回#。
//	 * @lastModify 2014-8-1 by Muyangmin
//	 */
//	private String getSortKeyFirstLetter(String str){
//		//如果联系人名字是NULL，则返回的串将不含字符，会抛出NullPointerException，因此先行处理。
//		if(str==null){
//			return "#";
//		}
//		String key = str.substring(0, 1).toUpperCase(Locale.US);
//		if (key.matches("[A-Z]")){
//			return key;
//		}
//		else{
//			return "#";
//		}
//	}
	
	/**
	 * 统一处理空游标的情况。当前版本的策略为：打印日志。
	 */
	private final void handleNullCursor(){
		Log.w(LOG_TAG, "provider returns null cursor!");
	}
	/**
	 * 统一处理本该返回单行游标但实际得到多行的情况。当前版本的策略为：打印日志。
	 */
	private final void handleMultiCursor(){
		Log.w(LOG_TAG, "Warning: provider returns a multi-line cursor!");
	}
}
