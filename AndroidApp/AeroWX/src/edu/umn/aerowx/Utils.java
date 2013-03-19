/**
 * 
 */
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Common Utility Routines.
 * 
 * @author waynej
 *
 */
public class Utils
{

	/** Client to send out HTTP requests with */
	static HttpClient client = new DefaultHttpClient();

	public static JSONObject postJSON(String baseUrl, JSONArray requestArray)
			throws IOException, JSONException
	{

		Log.i(MetarActivity.class.toString(), "postJSON(" + baseUrl + ", "
				+ requestArray + ")");

		StringEntity entity = new StringEntity(requestArray.toString());

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
