/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.recentcall;

import java.io.Serializable;

import com.zhy.dialtong.fragment.recentcall.CallLogManager.CallType;


/**
 * 通话记录。为避免和系统API的{@link android.provider.CallLog CallLog}重名，更改为该名称�??
 * @author Muyangmin
 * @create 2014-7-29
 * @version 1.0
 */
public final class CallRecord implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String number;				//电话号码，The phone number as the user entered it.
//	private String presentation; 		//来电显示类型
//	private String country;				//ISO3166-1两位国家代码，为用户收到或发出该通话时所在地的代�?
/*
 * presentation字段�? API Level 19才可用�??
 * country字段被Android SDK隐藏了，暂不使用�?
 * 此外，还有以下字段被隐藏：cachePhoto�? cacheFormat�? cacheMacthNum，cacheNormalNum
 */
	private long date;					//发生该�?�话记录的时间，以毫秒为单位
	private long duration;				//持续时间，以秒为单位
	private boolean isNew;				//标记是否用户已知该�?�话记录
	private String cacheName;			//缓存的该号码联系人姓�?
	private String cacheNumType;		//缓存的号码类型（手机�? 工作，家庭�?��?�）
	private String cacheNumLabel;		//缓存的号码标�?
	private boolean isRead;				//标记用户是否已处理该通话记录
	private CallType type;				//记录类型
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	/**
	 * 获得通话时间，以秒为单位�?
	 */
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	public String getCacheNumType() {
		return cacheNumType;
	}
	public void setCacheNumType(String cacheNumType) {
		this.cacheNumType = cacheNumType;
	}
	public String getCacheNumLabel() {
		return cacheNumLabel;
	}
	public void setCacheNumLabel(String cacheNumLabel) {
		this.cacheNumLabel = cacheNumLabel;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CallRecord [id=").append(id).append(", number=")
				.append(number).append(", date=").append(date)
				.append(", duration=").append(duration).append(", isNew=")
				.append(isNew).append(", cacheName=").append(cacheName)
				.append(", cacheNumType=").append(cacheNumType)
				.append(", cacheNumLabel=").append(cacheNumLabel)
				.append(", isRead=").append(isRead).append(", type=")
				.append(type).append("]");
		return builder.toString();
	}
	public CallType getType() {
		return type;
	}
	public void setType(CallType type) {
		this.type = type;
	}
}
