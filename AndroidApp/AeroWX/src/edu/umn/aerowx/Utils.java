/**
 * 
 */
package edu.umn.aerowx;

import java.io.BufferedReader;
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
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

/**
 * Common Utility Routines.
 * 
 * @author Wayne Johnson
 * 
 */
public class Utils
{

	/** Client to send out HTTP requests with */
	static HttpClient client = new DefaultHttpClient();

	/**
	 * Post a request to the server and get a response.
	 * 
	 * @param baseUrl
	 *            URL of server
	 * @param requestArray
	 *            parameters for request
	 * @return object containing either a JSONObject or JSONArray.
	 * @throws Exception
	 *             if we get an IOException from the HTTP client, or a bad JSON
	 *             response.
	 */
	public static Object postJSON(String baseUrl, JSONArray requestArray)
			throws Exception
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

		if (statusCode != 200)
		{
			throw new Exception("Server returned error " + statusCode + ": "
					+ statusLine.getReasonPhrase());
		} else
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

			Object object = new JSONTokener(builder.toString()).nextValue();
			if (!(object instanceof JSONArray)
					&& !(object instanceof JSONObject))
			{
				throw new Exception(
						"Server returned invalid response containing " + object);
			}

			Log.i(MetarActivity.class.getName(), "JSON response: " + object);
			return object;
		}
	}
}
