package edu.umn.aerowx;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

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

		try
		{
			requestMETAR(baseUrl);
		} catch (Exception e)
		{
			Log.i(MetarActivity.class.toString(), e.getMessage());
		}
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
		requestObject.put("wxid", wxid);
		requestObject.put("time", "");
		requestObject.put("server", "Metar");

		JSONObject responseArray = Utils.postJSON(baseUrl, requestObject);

		METARData metarData = new METARData(responseArray);
		Log.i(MetarActivity.class.toString(), "response: " + metarData);

		return metarData;
	}
}
