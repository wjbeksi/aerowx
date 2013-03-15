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
public class MetarWeather 
{
	/** (- for light, empty for moderate, + for heavy) */
	String intensity;
	/** */
	String description;
	/** */
	String precipitation;
	/** */
	String obscuration;
	/** */
	String misc;

	public MetarWeather()
	{
	}

	public MetarWeather(JSONObject jsonObject) throws JSONException
	{
		intensity = jsonObject.optString("intensity");
		description = jsonObject.optString("description");
		precipitation = jsonObject.optString("precipitation");
		misc = jsonObject.optString("misc");
	}

	public Object toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("intensity", intensity);
		object.put("description", description);
		object.put("precipitation", precipitation);
		return object;
	}

	@Override
	public String toString()
	{
		return "Weather [intensity=" + intensity + ", description="
				+ description + ", precipitation=" + precipitation
				+ ", obscuration=" + obscuration + ", misc=" + misc + "]";
	}

}
