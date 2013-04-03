package edu.umn.aerowx;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsData
{
	public String baseUrl;
	public String wxid;
	SharedPreferences settings;

	/**
	 * 
	 */
	public SettingsData(Activity activity)
	{
		super();

		settings = PreferenceManager.getDefaultSharedPreferences(activity);

		baseUrl = settings.getString("baseURL",
				"http://aerowx.dccmn.com/get_weather");
		wxid = settings.getString("wxid", "KROS");
	}

	public void saveSettings()
	{
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("baseURL", baseUrl);
		editor.putString("wxid", wxid);
		editor.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SettingsData ["
				+ (baseUrl != null ? "baseUrl=" + baseUrl + ", " : "")
				+ (wxid != null ? "wxid=" + wxid : "") + "]";
	}

}
