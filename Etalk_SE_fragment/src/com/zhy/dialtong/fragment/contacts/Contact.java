/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.contacts;

import java.util.Locale;

/**
 * 
 * @author Muyangmin
 * @create 2014-8-1
 * @version 1.0
 */
public final class Contact implements Cloneable, Comparable<Contact>{
	
	/**
	 * SORTKEY
	 */
	public static final String STAR_SORTKEY="鈽�";
	
	private long id;
	private String lookupKey;
	private String name;
	private String phoneNumber;
	private String sortKey;
	private boolean starred;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLookupKey() {
		return lookupKey;
	}
	public void setLookupKey(String lookup) {
		this.lookupKey = lookup;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * SortKey
	 */
	public String getSortKey() {
		return sortKey;
	}
	/**
	 * SortKey
	 * SortKey{@link #STAR_SORTKEY},
	 * @return A-Z,
	 */
	public Character getSortKeyCharacter(){
		if(sortKey==null){
			return '#';
		}
		if (sortKey.equals(STAR_SORTKEY)){
			return '*';
		}
		Character key = sortKey.substring(0, 1).toUpperCase(Locale.US).charAt(0);
		if ('A'<=key && key<='Z'){
			return key;
		}
		else{
			return '#';
		}
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	public boolean isStarred() {
		return starred;
	}
	public void setStarred(boolean starred) {
		this.starred = starred;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [id=").append(id).append(", lookup=")
				.append(lookupKey).append(", name=").append(name)
				.append(", phoneNumber=").append(phoneNumber)
				.append(", sortKey=").append(sortKey).append(",SortKeyFirst=")
				.append(getSortKeyCharacter()).append(", starred=")
				.append(starred).append("]");
		return builder.toString();
	}
	@Override
	public Contact clone(){
		
		try {
			return (Contact) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	//SortKey
	@Override
	public int compareTo(Contact another) {
		return this.sortKey.compareTo(another.sortKey);
//		return 0;
	}
	
}
