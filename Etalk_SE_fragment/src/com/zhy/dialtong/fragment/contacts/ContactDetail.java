/**
 * @encoding UTF-8
 */
package com.zhy.dialtong.fragment.contacts;

import java.util.HashMap;

import android.net.Uri;

/**
 * 包含联系人所有详细信息的实体类。
 * <p>对于联系人的一般属性，如照片，铃声，姓名等，作为一般的属性存储。
 * 		扩展的属性以JSON字符串存储，使用时需要用反射的方式获取。</p>
 * <p>对于外部存储对象铃声和头像，从GETTER方法获得的结果为其Uri，使用时只需要转换为Uri格式即可。</p>
 * @see {@linkplain Uri}
 * @author Muyangmin
 * @create 2014-8-3
 * @version 1.0
 */
public final class ContactDetail {
	private long id;										//联系人ID
	private String lookupKey;								//查找键
	private String name;									//名字
	private boolean starred;								//是否标记为星标
	private HashMap<String, String> phone;					//电话号码列表，Map<号码，类型>
	private HashMap<Long, String> group;					//所属群组列表，Map<ID，标题>
	private String ringtone;								//来电铃声Uri
	private String photo;									//头像Uri
	private String extProperties;							//扩展属性，以JSON字符串的方式存储
	/**
	 * 判断该联系人是否有电话号码。
	 * @return 如果有则返回true。
	 * @lastModify 2014-8-13 by Muyangmin
	 */
	public boolean hasPhoneNumber(){
		return phone==null;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLookupKey() {
		return lookupKey;
	}
	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获得联系人的电话列表，列表的内容使用HashMap的方式存储，其Key为号码，Value为类型。<br/>
	 * 使用时，应该先取得Key的Iterator，再取得对应号码的类型。
	 * <p><code>
	 *		//detail is a valid object.					<br/>
	 * 		HashMap<String, String> p = detail.getPhone();<br/>
	 * 		Iterator<String> iter = p.keySet().iterator();<br/>
	 * 		while (iter.hasNext()){<br/>
	 * 			String num = iter.next();<br/>
	 * 			String type = p.get(num);<br/>
	 *			//...<br/>
	 * 		}<br/>
	 * </code></p>
	 */
	public HashMap<String, String> getPhone() {
		return phone;
	}
	public void setPhone(HashMap<String, String> phone) {
		this.phone = phone;
	}
	public String getRingtone() {
		return ringtone;
	}
	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}
	public String getExtProperties() {
		return extProperties;
	}
	public void setExtProperties(String extProperties) {
		this.extProperties = extProperties;
	}
	public HashMap<Long, String> getGroup() {
		return group;
	}
	public void setGroup(HashMap<Long, String> group) {
		this.group = group;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContactDetail [id=").append(id).append(", lookupKey=")
				.append(lookupKey).append(", name=").append(name)
				.append(", starred=").append(starred).append(", phone=")
				.append(phone).append(", group=").append(group)
				.append(", ringtone=").append(ringtone).append(", photo=")
				.append(photo).append(", extProperties=").append(extProperties)
				.append("]");
		return builder.toString();
	}
	public boolean isStarred() {
		return starred;
	}
	public void setStarred(boolean starred) {
		this.starred = starred;
	}
}
