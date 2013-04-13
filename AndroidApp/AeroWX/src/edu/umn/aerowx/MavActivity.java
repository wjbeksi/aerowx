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
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import edu.umn.aerowx.MavData.Period;

public class MavActivity extends Activity
{

	SettingsData settings;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		settings = new SettingsData(this);

		setContentView(R.layout.activity_gfs);

		MavData gfs = null;
		try
		{
			gfs = requestGFS(settings.baseUrl);
		} catch (Exception e)
		{
			Log.i(MetarActivity.class.toString(), e.getMessage());
			errorDialog(e.getMessage());
			return;
		}

		((TextView) findViewById(R.id.station)).setText(gfs.wxid);

		for (int i = 0; i < 4; ++i)
		{
			displayPeriod(i, gfs.periods[i]);
		}
	}

	private void displayPeriod(int periodIndex, Period period)
	{
		TableRow dateRow = (TableRow) findViewById(R.id.periodDate);
		((TextView) dateRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.date);

		TableRow timeRow = (TableRow) findViewById(R.id.periodTime);
		((TextView) timeRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.hour + ":00");

		TableRow tempRow = (TableRow) findViewById(R.id.tempRow);
		((TextView) tempRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.temp);

		TableRow dewptRow = (TableRow) findViewById(R.id.dewptRow);
		((TextView) dewptRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.dewpoint);

		TableRow skyRow = (TableRow) findViewById(R.id.skyRow);
		((TextView) skyRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.cover);

		TableRow windRow = (TableRow) findViewById(R.id.windRow);
		((TextView) windRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.wind.direction + "@" + period.wind.speed);

		TableRow precipRow = (TableRow) findViewById(R.id.precipRow);
		((TextView) precipRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.pop6);

		TableRow thundRow = (TableRow) findViewById(R.id.thundRow);
		((TextView) thundRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.thund6);

		TableRow visiRow = (TableRow) findViewById(R.id.visRow);
		((TextView) visiRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.visibility);

		TableRow ceilRow = (TableRow) findViewById(R.id.ceilRow);
		((TextView) ceilRow.getVirtualChildAt(periodIndex + 1))
				.setText(period.ceiling);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gfs, menu);
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
	private MavData requestGFS(String baseUrl) throws Exception
	{
		MavData gfsData = null;

		Log.i(MetarActivity.class.toString(), "requestGFS(" + baseUrl + ")");

		JSONObject requestObject = new JSONObject();
		requestObject.put("location", settings.wxid);
		requestObject.put("time", "");
		requestObject.put("source", "mav");

		// The server want's it's request(s) in an array.
		JSONArray requestArray = new JSONArray();
		requestArray.put(requestObject);

		Object responseObject = Utils.postJSON(baseUrl, requestArray);

		// // At this point, all we know is that we received back a JSONArray or
		// // JSONObject
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
		} else if (object.has("mav"))
		{
			object = (JSONObject) object.get("mav");
		}

		gfsData = new MavData(object);
		Log.i(MetarActivity.class.toString(), "response: " + gfsData);
		return gfsData;
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
						MavActivity.this.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private void doSettings()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void doCurrent(View view)
	{
		Intent intent = new Intent(this, MetarActivity.class);
		startActivity(intent);
	}
}
