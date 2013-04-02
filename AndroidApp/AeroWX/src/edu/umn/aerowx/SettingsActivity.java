package edu.umn.aerowx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends Activity
{

	SettingsData settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		settings=new SettingsData(this);
		
		// Create the wxid view
		TextView stationView = (TextView) findViewById(R.id.stationSetting);
		stationView.setText(settings.wxid);

		// Create the URL view
		TextView urlView = (TextView) findViewById(R.id.baseURLSetting);
		urlView.setText(settings.baseUrl);
	}
	
	public void doSave(View view)
	{
		TextView stationView = (TextView) findViewById(R.id.stationSetting);
		settings.wxid=stationView.getText().toString();

		TextView urlView = (TextView) findViewById(R.id.baseURLSetting);
		settings.baseUrl=urlView.getText().toString();
		
		settings.saveSettings();
		finish();
	}


}
