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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
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
	 * Android 3.0 and above require that network threads be run off the UI
	 * thread. This class defines a task that will do the network I/O safely.
	 */
	static class RequestTask extends AsyncTask<Object, Void, String>
	{
		@Override
		protected String doInBackground(Object... params)
		{
			String baseUrl = (String) params[0];
			StringEntity entity = (StringEntity) params[1];

			HttpPost httpPost = new HttpPost(baseUrl);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse response;
			try
			{
				response = client.execute(httpPost);
			} catch (ClientProtocolException e)
			{
				return null;
			} catch (IOException e)
			{
				return null;
			}
			StatusLine statusLine = response.getStatusLine();

			int statusCode = statusLine.getStatusCode();
			Log.i(MetarActivity.class.toString(), "statusCode: " + statusCode);

			StringBuilder builder = new StringBuilder();
			if (statusCode != 200)
			{
				// TODO Issue error return
			} else
			{

				HttpEntity entity2 = response.getEntity();
				InputStream content = null;
				try
				{
					content = entity2.getContent();
				} catch (IllegalStateException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				try
				{
					while ((line = reader.readLine()) != null)
					{
						builder.append(line);
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return builder.toString();
		}
	}

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

		Log.i(Utils.class.toString(), "postJSON(" + baseUrl + ", "
				+ requestArray + ")");

		StringEntity entity = new StringEntity(requestArray.toString());

		RequestTask requestTask = new RequestTask();
		requestTask.execute(baseUrl, entity);
		String response = requestTask.get();

		Object object = new JSONTokener(response).nextValue();
		if (!(object instanceof JSONArray) && !(object instanceof JSONObject))
		{
			throw new Exception("Server returned invalid response containing "
					+ object);
		}

		Log.i(Utils.class.getName(), "JSON response: " + object);
		return object;
	}
}
