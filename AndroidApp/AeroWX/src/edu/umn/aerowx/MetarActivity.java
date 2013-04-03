package edu.umn.aerowx;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Main activity for AeroWX App
 * 
 * @author Wayne Johnson
 * 
 */

public class MetarActivity extends Activity
{

	SettingsData settings;

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		settings = new SettingsData(this);

		setContentView(R.layout.activity_main);

		METARData metar = null;
		try
		{
			metar = requestMETAR(settings.baseUrl);
		} catch (Exception e)
		{
			Log.i(MetarActivity.class.toString(), e.getMessage());
			errorDialog(e.getMessage());
			return;
		}

		// Create the wxid view
		TextView stationView = (TextView) findViewById(R.id.station);
		stationView.setText(metar.wxid);

		// Create the time view
		TextView timeView = (TextView) findViewById(R.id.time);
		timeView.setText(metar.time);

		// Create the temp view
		TextView tempView = (TextView) findViewById(R.id.temp);
		tempView.setText(metar.temp);
		
		// Create the dewpoint view
		TextView dewpointView = (TextView) findViewById(R.id.dewpoint);
		dewpointView.setText(metar.dewpoint);
		 
		// Create the pressure view
		TextView pressureView = (TextView) findViewById(R.id.pressure);
		pressureView.setText(metar.pressure);

		// Create the wind view
		TextView windView = (TextView) findViewById(R.id.wind);
		windView.setText(metar.wind);
		
		// Create the visibility view
		TextView visView = (TextView) findViewById(R.id.vis);
		visView.setText(metar.visibility);

		// Create the weather view
		TextView wxView = (TextView) findViewById(R.id.wx);
		wxView.setText(metar.weather);

		// Create the sky view
		TextView skyView = (TextView) findViewById(R.id.sky);
		skyView.setText(metar.sky);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.action_settings:
			doSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * request METAR data from server.
	 * 
	 * If the server returns an array, pluck an object out of it.
	 * 
	 * @param baseUrl
	 *            Base URL of server.
	 * @return METARData object
	 * @throws Exception
	 * 
	 * @throws IOException
	 *             on server error
	 */
	private METARData requestMETAR(String baseUrl) throws Exception
	{
		METARData metarData = null;

		Log.i(MetarActivity.class.toString(), "requestMETAR(" + baseUrl + ")");

		JSONObject requestObject = new JSONObject();
		requestObject.put("location", settings.wxid);
		requestObject.put("time", "");
		requestObject.put("source", "metar");

		// The server want's it's request(s) in an array.
		JSONArray requestArray = new JSONArray();
		requestArray.put(requestObject);

		Object responseObject = Utils.postJSON(baseUrl, requestArray);

		// At this point, all we know is that we received back a JSONArray or
		// JSONObject
		JSONObject object = null;
		if (responseObject instanceof JSONArray)
		{
			JSONArray array = (JSONArray) responseObject;
			if (array.length() < 1)
			{
				throw new Exception("Server returned empty JSON Array");
			}

			// Grab the first object in the array
			object = array.getJSONObject(0);
		} else
		{
			// It's just a plain old object. Treat it as METAR data.
			object = (JSONObject) responseObject;
		}

		// If the object has the key "error", throw up
		if (object.has("error"))
		{
			throw new Exception("Server returned error: "
					+ object.getString("error"));
		}

		metarData = new METARData(object);
		Log.i(MetarActivity.class.toString(), "response: " + metarData);
		return metarData;
	}

	/**
	 * Display an error dialog
	 * 
	 * @param message
	 *            Message to display with dialog
	 */
	public void errorDialog(String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle("Error");
		builder.setPositiveButton("Close",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// if this button is clicked, close
						// current activity
						// MetarActivity.this.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private void doSettings()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		int requestCode = 0;
		startActivityForResult(intent, requestCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = new Intent(this, MetarActivity.class);
		startActivity(intent);
	}

}
