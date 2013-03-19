package edu.umn.aerowx;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

/**
 * Main activity for AeroWX App
 * 
 * @author Wayne Johnson
 * 
 */

public class MetarActivity extends Activity
{

	// Temporary settings
	String baseUrl = "http://aerowx.dccmn.com/get_weather";
	String wxid = "kros";

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		METARData metar = null;
		try
		{
			metar=requestMETAR(baseUrl);
		} catch (Exception e)
		{
			Log.i(MetarActivity.class.toString(), e.getMessage());
		}
		
	    // Create the wxid view
	    TextView stationView = (TextView)findViewById(R.id.station);
	    stationView.setText(metar.wxid);

	    // Create the time view
	    TextView timeView = (TextView)findViewById(R.id.time);
	    timeView.setText(metar.time);

	    // Create the temp view
	    TextView tempView = (TextView)findViewById(R.id.temp);
	    tempView.setText(metar.temp);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * request METAR data from server.
	 * 
	 * @param baseUrl
	 *            Base URL of server.
	 * @return METARData object
	 * 
	 * @throws IOException
	 *             on server error
	 * @throws JSONException
	 *             when we receive a bad JSON message
	 */
	private METARData requestMETAR(String baseUrl) throws IOException,
			JSONException
	{
		Log.i(MetarActivity.class.toString(), "requestMETAR(" + baseUrl + ")");

		JSONObject requestObject = new JSONObject();
		requestObject.put("location", wxid);
		requestObject.put("time", "");
		requestObject.put("source", "metar");
		JSONArray requestArray = new JSONArray();
		requestArray.put(requestObject);
		JSONObject responseArray = Utils.postJSON(baseUrl, requestArray);

		METARData metarData = new METARData(responseArray);
		Log.i(MetarActivity.class.toString(), "response: " + metarData);

		return metarData;
	}
}
