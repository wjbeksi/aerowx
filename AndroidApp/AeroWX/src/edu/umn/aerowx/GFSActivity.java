package edu.umn.aerowx;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.umn.aerowx.GFSMOSMAVData.Period;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class GFSActivity extends Activity
{

	// TODO: Temporary settings
	String baseUrl = "http://aerowx.dccmn.com/get_weather";
	String wxid = "kros";


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gfs);
		
		GFSMOSMAVData gfs = null;
		try
		{
			gfs = requestGFS(baseUrl);
		} catch (Exception e)
		{
			Log.i(MetarActivity.class.toString(), e.getMessage());
			errorDialog(e.getMessage());
			return;
		}
		
		((TextView) findViewById(R.id.station)).setText(gfs.wxid);
		((TextView) findViewById(R.id.time)).setText(gfs.time);
		((TextView) findViewById(R.id.high)).setText(gfs.high);
		((TextView) findViewById(R.id.low)).setText(gfs.low);
		
		for (int i=0; i<4; ++i)
		{
			displayPeriod(i, gfs.periods[i]);
		}
	}

	private void displayPeriod(int periodIndex, Period period)
	{
		TableRow dateRow=(TableRow) findViewById(R.id.periodDate);
		((TextView) dateRow.getVirtualChildAt(periodIndex+1)).setText(period.date);

		TableRow timeRow=(TableRow) findViewById(R.id.periodTime);
		((TextView) timeRow.getVirtualChildAt(periodIndex+1)).setText(period.hour+":00");

		TableRow tempRow=(TableRow) findViewById(R.id.tempRow);
		((TextView) tempRow.getVirtualChildAt(periodIndex+1)).setText(period.temp);

		TableRow dewptRow=(TableRow) findViewById(R.id.dewptRow);
		((TextView) dewptRow.getVirtualChildAt(periodIndex+1)).setText(period.dewpoint);

		TableRow skyRow=(TableRow) findViewById(R.id.skyRow);
		((TextView) skyRow.getVirtualChildAt(periodIndex+1)).setText(period.cover);

		TableRow windRow=(TableRow) findViewById(R.id.windRow);
		((TextView) windRow.getVirtualChildAt(periodIndex+1)).setText(period.wind.direction+"@"+period.wind.speed);

		TableRow precipRow=(TableRow) findViewById(R.id.precipRow);
		((TextView) precipRow.getVirtualChildAt(periodIndex+1)).setText(period.pop6);

		TableRow thundRow=(TableRow) findViewById(R.id.thundRow);
		((TextView) thundRow.getVirtualChildAt(periodIndex+1)).setText(period.thund6);

		TableRow visiRow=(TableRow) findViewById(R.id.visRow);
		((TextView) visiRow.getVirtualChildAt(periodIndex+1)).setText(period.visibility);
		
		TableRow ceilRow=(TableRow) findViewById(R.id.ceilRow);
		((TextView) ceilRow.getVirtualChildAt(periodIndex+1)).setText(period.ceiling);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gf, menu);
		return true;
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
	private GFSMOSMAVData requestGFS(String baseUrl) throws Exception
	{
		GFSMOSMAVData gfsData = null;

		Log.i(MetarActivity.class.toString(), "requestGFS(" + baseUrl + ")");

		JSONObject requestObject = new JSONObject();
		requestObject.put("location", wxid);
		requestObject.put("time", "");
		requestObject.put("source", "gfs");

		// The server want's it's request(s) in an array.
		JSONArray requestArray = new JSONArray();
		requestArray.put(requestObject);

//		Object responseObject = Utils.postJSON(baseUrl, requestArray);
//
//		// At this point, all we know is that we received back a JSONArray or
//		// JSONObject
		JSONObject object = null;
//		if (responseObject instanceof JSONArray)
//		{
//			JSONArray array = (JSONArray) responseObject;
//			if (array.length() < 1)
//			{
//				throw new Exception("Server returned empty JSON Array");
//			}
//
//			// Grab the first object in the array
//			object = array.getJSONObject(0);
//		} else
//		{
//			// It's just a plain old object. Treat it as METAR data.
//			object = (JSONObject) responseObject;
//		}

		// TODO: Fake GFS Data
		GFSMOSMAVData gfsMosMavData = new GFSMOSMAVData();

		// first the required fields
		gfsMosMavData.wxid = "kros";
		gfsMosMavData.time = "19:00";
		gfsMosMavData.high = "27";
		gfsMosMavData.low = "25";

		for (int i = 0; i < 4; ++i)
		{
			gfsMosMavData.periods[i]=gfsMosMavData.new Period();
			gfsMosMavData.periods[i].date = "1/1/2013";
			gfsMosMavData.periods[i].hour = "7";
			gfsMosMavData.periods[i].temp = "27";
			gfsMosMavData.periods[i].dewpoint = "27";
			gfsMosMavData.periods[i].cover = "clear";
			gfsMosMavData.periods[i].wind.direction = "S";
			gfsMosMavData.periods[i].wind.speed = "20";
			gfsMosMavData.periods[i].pop6 = "50";
			gfsMosMavData.periods[i].pop12 = "";
			gfsMosMavData.periods[i].qpf6 = "";
			gfsMosMavData.periods[i].qpf12 = "";
			gfsMosMavData.periods[i].thund6 = "10";
			gfsMosMavData.periods[i].thund12 = "";
			gfsMosMavData.periods[i].popz = "";
			gfsMosMavData.periods[i].pops = "";
			gfsMosMavData.periods[i].type = "";
			gfsMosMavData.periods[i].snow = "";
			gfsMosMavData.periods[i].visibility = "15";
			gfsMosMavData.periods[i].obscurity = "";
			gfsMosMavData.periods[i].ceiling = "16000";
		}

		object=gfsMosMavData.toJSONObject();
		
		// If the object has the key "error", throw up
		if (object.has("error"))
		{
			throw new Exception("Server returned error: "
					+ object.getString("error"));
		}

		gfsData = new GFSMOSMAVData(object);
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
						GFSActivity.this.finish();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

    /** Called when the user clicks the Send button */
    public void doCurrent(View view) {
        // Do something in response to button
    	
    	Intent intent = new Intent(this, MetarActivity.class);
        startActivity(intent);
    }

}
