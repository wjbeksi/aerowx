package edu.umn.aerowx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

	/** Client to send out requests with */
	HttpClient client = new DefaultHttpClient();

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Test MetarData for now.

		try
		{
			METARData.testMetarData();
		} catch (Exception e1)
		{
			Log.i(MetarActivity.class.toString(), e1.getMessage());
		}

		try
		{
			readMETAR(baseUrl);
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

	public void readMETAR(String baseUrl) throws IOException, JSONException
	{
		Log.i(MetarActivity.class.toString(), "readMETAR(" + baseUrl);

		JSONObject requestObject = new JSONObject();
		requestObject.put("wxid", wxid);
		requestObject.put("time", "");
		requestObject.put("server", "Metar");

		JSONObject responseArray = postJSON(baseUrl, requestObject);

		METARData metarData = new METARData(responseArray);
	}

	private JSONObject postJSON(String baseUrl, JSONObject requestObject)
			throws IOException, JSONException
	{

		Log.i(MetarActivity.class.toString(), "postJSON(" + baseUrl + ", "
				+ requestObject + ")");

		StringEntity entity = new StringEntity(requestObject.toString());

		HttpPost httpPost = new HttpPost(baseUrl);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		HttpResponse response = client.execute(httpPost);

		StatusLine statusLine = response.getStatusLine();

		int statusCode = statusLine.getStatusCode();
		Log.i(MetarActivity.class.toString(), "statusCode: " + statusCode);

		if (statusCode == 200)
		{
			StringBuilder builder = new StringBuilder();

			HttpEntity entity2 = response.getEntity();
			InputStream content = entity2.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}

			JSONObject jsonArray = new JSONObject(builder.toString());
			Log.i(MetarActivity.class.getName(), "JSON response: " + jsonArray);
			return jsonArray;
		}
		return null;
	}

}
