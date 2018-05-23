package com.akaxin.platform.storage.api;

public interface IPhoneDao {

	public boolean setStringValue(String key, String value, int expireTime);

	public String getStringValue(String key);

	public long delStringValue(String key);

}
