/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.recentcall;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;
import android.util.Log;

import static android.provider.CallLog.Calls.*;

/**
 * 读取通话记录的类�?
 * <p class="note"><strong>注意�?</strong>使用该类的方法要求具�?
 * {@link android.Manifest.permission#READ_CALL_LOG READ_CALL_LOG}权限�?</p>
 * @author Muyangmin
 * @create 2014-7-29
 * @version 1.0
 */
public final class CallLogManager {
	
	private static final String LOG_TAG = "CallLogReader";
	
	private ContentResolver resolver;
	/*
	 * Dev Note�? 该类的SQL选择投影�?般是选择�?有列，因此省略投影字段，直接选择�?有�??
	 */
//	private String[] callLogColumns = new String[]{
//			TYPE, NUMBER, /*NUMBER_PRESENTATION, COUNTRY_ISO, */
//			DATE, DURATION, NEW, CACHED_NAME,
//			CACHED_NUMBER_TYPE, CACHED_NUMBER_LABEL,
//			IS_READ
//	};

	public CallLogManager(Context context) {
		super();
		resolver = context.getContentResolver();
	}
	
	/**
	 * 将游标的当前行转换为�?个�?�话记录对象�?
	 * <p class="note"><strong>注意�?</strong>该方法不会检查Cursor是否为空�?
	 * 也不会调�? cursor.{@link Cursor#moveToNext() moveToNext()};
	 * </p>
	 * @param cursor 待转换的游标。调用前必须将游标移动到合�?�的行！
	 * @return 转换后的CallRecord对象，该对象不会为空�?
	 * @lastModify 2014-7-30 by Muyangmin
	 */
	private CallRecord parseCursorToCallRecord(Cursor cursor){
		CallRecord record = new CallRecord();
		int type = cursor.getInt(cursor.getColumnIndex(TYPE));
		record.setType(type ==INCOMING_TYPE ? CallType.CALL_INCOMING
						:type==MISSED_TYPE?CallType.CALL_MISSED
								:CallType.CALL_OUTGOING);
		record.setCacheName(cursor.getString(cursor.getColumnIndex(CACHED_NAME)));
		record.setCacheNumLabel(cursor.getString(cursor.getColumnIndex(CACHED_NUMBER_LABEL)));
		record.setCacheNumType(cursor.getString(cursor.getColumnIndex(CACHED_NUMBER_TYPE)));
		record.setDate(cursor.getLong(cursor.getColumnIndex(DATE)));
		record.setDuration(cursor.getLong(cursor.getColumnIndex(DURATION)));
		record.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
		record.setNew(cursor.getInt(cursor.getColumnIndex(NEW))==1? true : false);
		record.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
		record.setRead(cursor.getInt(cursor.getColumnIndex(IS_READ))==1? true : false);
		return record;
	}
	
	/**
	 * 删除�?有的通话记录�?
	 */
	public void deleteAllCallRecords(){
		int i=resolver.delete(Calls.CONTENT_URI, null, null);
		Log.v(LOG_TAG, i+" rows deleted.");
	}
	
	/**
	 * 删除指定条目�?
	 */
	public void deleteCallRecord(CallRecord record){
		if (record==null || record.getId()<=0){
			return ;
		}
		int i = resolver.delete(Calls.CONTENT_URI, _ID+"=?", new String[]{""+record.getId()});
		Log.v(LOG_TAG, i+" rows deleted.");
	}
	
	/**
	 * 删除指定号码的所有�?�话记录�?
	 * @param phone 电话号码
	 */
	public void deleteCallRecord(String phone){
		if (phone ==null){
			return ;
		}
		int i = resolver.delete(Calls.CONTENT_URI, NUMBER+"=?", new String[]{phone});
		Log.v(LOG_TAG, i+" rows deleted.");
	}

	/**
	 * 获取系统�?有的通话记录�?
	 * @return 返回通话记录的列表；如果查询出错，返回null�?
	 * @lastModify 2014-7-30 by Muyangmin
	 */
	public ArrayList<CallRecord> getAllCallRecords() {
		Cursor cursor = resolver.query(Calls.CONTENT_URI, 
				null , //callLogColumns,
				null, null, 		//selection
				Calls.DEFAULT_SORT_ORDER);
		if (cursor==null){
			Log.w(LOG_TAG, "calllog provider return null cursor!");
			return null;
		}
		ArrayList<CallRecord> list = new ArrayList<CallRecord>();
		while (cursor.moveToNext()){
			CallRecord record = parseCursorToCallRecord(cursor);
			list.add(record);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获得指定类型的�?�话记录�?
	 * @param type 通话记录类型�?
	 * @return 返回该类型�?�话记录的列表�?�如果查询失败，返回null�?
	 * @see CallType
	 * @lastModify 2014-7-30 by Muyangmin
	 */
	public ArrayList<CallRecord> getSpecificTypeCallRecords(CallType type) {
		String[] args;
		switch (type) {
		case CALL_INCOMING:	args = new String[]{""+INCOMING_TYPE};	break;
		case CALL_OUTGOING:	args = new String[]{""+OUTGOING_TYPE};	break;
		case CALL_MISSED:	args = new String[]{""+MISSED_TYPE};	break;
		default: args=new String[]{}; break;
		}
		Cursor cursor = resolver.query(Calls.CONTENT_URI, 
				null , //callLogColumns,
				TYPE+"=?", args, 		//selection
				Calls.DEFAULT_SORT_ORDER);
		if (cursor==null){
			Log.w(LOG_TAG, "calllog provider return null cursor!");
			return null;
		}
		ArrayList<CallRecord> list = new ArrayList<CallRecord>();
		while (cursor.moveToNext()){
			CallRecord record = parseCursorToCallRecord(cursor);
			list.add(record);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 返回指定号码的�?�话记录�?
	 * @param number 电话号码，不能为null�?
	 * @return 返回与该号码的�?�话记录；如果没有记录或查询失败，返回null�?
	 * @lastModify 2014-7-30 by Muyangmin
	 */
	public ArrayList<CallRecord> getSpecificNumberCallRecords(String number) {
		if (number==null){
			Log.w(LOG_TAG, "param phone number is null, check it and fire the bug!");
			return null;
		}
		Cursor cursor = resolver.query(Calls.CONTENT_URI, 
				null , //callLogColumns,
				NUMBER+"=?", new String[]{number}, 		//selection
				Calls.DEFAULT_SORT_ORDER);
		if (cursor==null){
			Log.w(LOG_TAG, "calllog provider return null cursor!");
			return null;
		}
		ArrayList<CallRecord> list = new ArrayList<CallRecord>();
		while (cursor.moveToNext()){
			CallRecord record = parseCursorToCallRecord(cursor);
			list.add(record);
		}
		cursor.close();
		return list;
	}
	/**
	 * 辨识通话记录的类型�??
	 * @author Muyangmin
	 * @create 2014-7-30
	 * @version 1.0
	 */
	public static enum CallType{
		CALL_INCOMING, CALL_OUTGOING, CALL_MISSED
	}
}
