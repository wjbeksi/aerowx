/**
 * 
 */
package edu.umn.aerowx;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author waynej
 * 
 */
public class MetarVisibility
{
	/** Visibility distance */
	public String distance;
	/** reason for obscurity (fog, smoke, etc) */
	String obscurity;

	public MetarVisibility()
	{
	}

	public MetarVisibility(JSONObject jsonObject) throws JSONException
	{
		distance = jsonObject.getString("distance");
		obscurity = jsonObject.getString("obscurity");
	}

	public Object toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("distance", distance);
		object.put("obscurity", obscurity);
		return object;
	}

	@Override
	public String toString()
	{
		return "Visibility [distance=" + distance + ", obscurity=" + obscurity
				+ "]";
	}

}
