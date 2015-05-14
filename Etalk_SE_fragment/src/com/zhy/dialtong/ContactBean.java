package com.zhy.dialtong;

import java.io.Serializable;

import android.provider.ContactsContract.CommonDataKinds.Photo;


@SuppressWarnings("serial")
public class ContactBean implements Serializable{//implements Serializable

	private long contactId;
	private String displayName;
	private String phoneNum;
	private String sortKey;
	private Long photoId;
	private String lookUpKey;
	private int starred = 0; // if == 1, true if == 0, false
	private int selected = 0;
	private String formattedNumber;
	private String pinyin;
	private byte[] Photo;
	private String dialcount;
	
	public long getContactId() {//联系人ID
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getDisplayName() {//姓名
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPhoneNum() {//号码
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getSortKey() {//String
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	public Long getPhotoId() {//照片ID
		return photoId;
	}
	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}
	public String getLookUpKey() {//String
		return lookUpKey;
	}
	public void setLookUpKey(String lookUpKey) {//设置抬头键
		this.lookUpKey = lookUpKey;
	}
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}
	public String getFormattedNumber() {
		return formattedNumber;
	}
	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public byte[] getPhoto() {
		return Photo;
	}

	public void setPhoto(byte[] photo) {
		Photo = photo;
	}
	public int getStarred() {
		return starred;
	}

	public void setStarred(int starred) {
		this.starred = starred;
	}
	public String getDialcount() {
		return dialcount;
	}
	public void setDialcount(String dialcount) {
		this.dialcount = dialcount;
	}
	
}
