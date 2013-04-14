package edu.umn.aerowx;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdfDate = new SimpleDateFormat("MMMMM dd");
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");

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

	private String convertCover(MavData.Cover cover)
	{
		switch (cover)
		{
		case CLEAR: return "clear";
		case FEW: return "few";
		case SCATTERED: return "scattered";
		case BROKEN: return "broken";
		case OVERCAST: return "overcast";
		}
		return "unknown";
	}
	
	private String convertVisibility(MavData.Visibility visibility)
	{
		switch (visibility)
		{
		case V1: return "<1/2mi";
		case V2: return "1/2-1mi";
		case V3: return "1-2mi";
		case V4: return "2-3mi";
		case V5: return "3-5mi";
		case V6: return "6mi";
		case V7: return ">6mi";
		}
		return "unknown";
	}

	/**
	 * Convenience method to set text into the view rows
	 * @param id	id of row of view
	 * @param periodIndex period (column) of view
	 * @param value value to set.
	 */
	void setView(int id, int periodIndex, String value)
	{
		TableRow row = (TableRow) findViewById(id);
		if (row != null)
		{
			TextView view=(TextView)row.getVirtualChildAt(periodIndex + 1);
			if (view != null)
			{
				view.setText(value);
			}
		}
	}

	enum Month
	{
		JAN, FEB, MAR, APR, MAY, JUNE, JULY, AUG, SEP, OCT, NOV, DEC;
	}
	
	@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
	private void displayPeriod(int periodIndex, Period period)
	{
		// Convert the Date/Time in the MAV data to local time.
		// To quote an entry in stackoverflow: 
		// 		"I pity the fool who has to do dates in Java."
		
		// Get the timezone of the MAV data (GMT) and a calendar to match
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar periodCalendar=Calendar.getInstance(tz);
		// Reset minutes since it's always on the hour
		periodCalendar.set(Calendar.MINUTE, 0);

		// Split up the period.date to month and day and put those and the hour
		// into our Calendar.
		String[] split=period.date.split(" +");
		if (split.length==2)
		{
			try
			{
				int month=Month.valueOf(split[0]).ordinal();
				periodCalendar.set(Calendar.MONTH, month);
				int day=Integer.parseInt(split[1]);
				periodCalendar.set(Calendar.DAY_OF_MONTH, day);
				int hour=Integer.parseInt(period.hour);
				periodCalendar.set(Calendar.HOUR_OF_DAY, hour);
			}
			catch (Exception e)
			{
				// If the parsing failed, we don't care
			}
		}
		
		// Now extract the local time as a Date class
		Date local=periodCalendar.getTime();
		
		// Format for display
		String date=sdfDate.format(local);
		String time=sdfTime.format(local);
		setView(R.id.periodDate, periodIndex, date);
		setView(R.id.periodTime, periodIndex, time);
		
		setView(R.id.tempRow, periodIndex, period.temp);
		setView(R.id.dewptRow, periodIndex, period.dewpoint);
		setView(R.id.skyRow, periodIndex, convertCover(period.cover));
		setView(R.id.windRow, periodIndex, period.wind.direction + "@" + period.wind.speed);
		setView(R.id.precipRow, periodIndex, period.pop6);
		setView(R.id.thundRow, periodIndex, period.thund6);
		setView(R.id.visRow, periodIndex, convertVisibility(period.visibility));
		setView(R.id.ceilRow, periodIndex, period.ceiling);
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
