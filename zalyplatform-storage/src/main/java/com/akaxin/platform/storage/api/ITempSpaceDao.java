package com.akaxin.platform.storage.api;

public interface ITempSpaceDao {

	public boolean setStringValue(String key, String value, int expireTime);

	public String getStringValue(String key);

}
