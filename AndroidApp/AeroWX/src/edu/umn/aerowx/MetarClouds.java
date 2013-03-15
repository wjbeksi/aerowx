package edu.umn.aerowx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class MetarClouds
{
	/** */
	String coverage;
	/** */
	String height;
	/** */
	String type;

	public MetarClouds()
	{
		super();
	}
	
	public MetarClouds(JSONObject jsonObject) throws JSONException
	{
		coverage = jsonObject.getString("coverage");
		height = jsonObject.getString("height");
		type = jsonObject.getString("type");
	}

	public static JSONArray toJSONArray(MetarClouds[] clouds) throws JSONException
	{
		JSONArray jsonArray=new JSONArray();
		
		for (MetarClouds cloud:clouds)
		{
			jsonArray.put(cloud.toJSONObject());
		}
		return jsonArray;
	}

	public static MetarClouds[] getCloudsArray(JSONArray jsonArray)
			throws JSONException
	{
		int length = jsonArray.length();
		MetarClouds clouds[] = new MetarClouds[length];
	
		for (int i = 0; i < jsonArray.length(); i++)
		{
			clouds[i] = new MetarClouds((JSONObject) (jsonArray.get(i)));
		}
		return clouds;
	}

	public Object toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("coverage", coverage);
		object.put("height", height);
		object.put("type", type);
		return object;
	}

	@Override
	public String toString()
	{
		return "Clouds [coverage=" + coverage + ", height=" + height
				+ ", type=" + type + "]";
	}

}

