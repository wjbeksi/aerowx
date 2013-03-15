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
public class MetarWind
{
	/** direction */
	public String direction;
	/** speed */
	public String speed;
	/** optional wind gust speed */
	String gust;

	public MetarWind()
	{
	}

	public MetarWind(JSONObject jsonObject) throws JSONException
	{
		direction = jsonObject.getString("direction");
		speed = jsonObject.getString("speed");
		gust = jsonObject.optString("gust");
	}

	public Object toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("direction", direction);
		object.put("speed", speed);
		if (gust != null)
		{
			object.put("gust", gust);
		}
		return object;
	}

	@Override
	public String toString()
	{
		return "Wind [direction=" + direction + ", speed=" + speed + ", gust="
				+ gust + "]";
	}
}
