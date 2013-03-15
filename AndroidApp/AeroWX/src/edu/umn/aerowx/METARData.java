package edu.umn.aerowx;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class manages data coming from the METAR weather server.
 * 
 * @author Wayne Johnson
 *
 */
public class METARData
{
	/* METAR Data */
	/** Weather Station ID (4 letters) */
	public String wxid;

	/** Time/Date of observation (in GMT) */
	public String time;

	/** Temperature */
	public String temp;

	/** Dewpoint */
	public String dewpoint;

	/** Air pressure/altimeter setting */
	public String pressure;

	/** Type of observation (i.e. automatic) */
	public String obsType;

	/** Surface wind */
	public MetarWind wind;

	/** Visibility */
	public MetarVisibility visibility;

	/** Weather */
	MetarWeather weather;

	/** */
	MetarClouds clouds[];

	/** various optional remarks */
	String remarks;

	/**
	 * Constructor for empty METARData
	 */
	public METARData()
	{
		super();
		wind=new MetarWind();
		visibility=new MetarVisibility();
		weather=new MetarWeather();
		clouds=new MetarClouds[0];
	}

	/**
	 * Constructor to create METARData from JSONObject.
	 * 
	 * @param object JSON object from which to create METARData object.
	 * 
	 * @throws JSONException when JSON data is invalid.
	 */
	public METARData(JSONObject object) throws JSONException
	{
		super();

		try
		{
			// All these are required in a METAR message
			wxid = object.getString("wxid");
			time = object.getString("time");
			temp = object.getString("temp");
			dewpoint = object.getString("dewpoint");
			pressure = object.getString("pressure");
			obsType = object.getString("type");
			wind = new MetarWind(object.getJSONObject("wind"));
			visibility = new MetarVisibility(object.getJSONObject("visibility"));
			weather = new MetarWeather(object.getJSONObject("weather"));
			clouds = MetarClouds.getCloudsArray(object.getJSONArray("clouds"));
			remarks = object.getString("remarks");
		} catch (JSONException e)
		{
			throw e;
		}
	}

	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("wxid", wxid);
		object.put("time", time);
		object.put("temp", temp);
		object.put("dewpoint", dewpoint);
		object.put("pressure", pressure);
		object.put("type", obsType);
		if (wind != null)
		{
			object.put("wind", wind.toJSONObject());
		}
		if (visibility != null)
		{
			object.put("visibility", visibility.toJSONObject());
		}
		if (weather != null)
		{
			object.put("weather", weather.toJSONObject());
		}
		if (clouds != null)
		{
			object.put("clouds", MetarClouds.toJSONArray(clouds));
		}
		object.put("remarks", remarks);

		return object;
	}

	@Override
	public String toString()
	{
		return "METARData [wxid=" + wxid + ", time=" + time + ", temp=" + temp
				+ ", dewpoint=" + dewpoint + ", pressure=" + pressure
				+ ", obsType=" + obsType + ", wind=" + wind + ", visibility="
				+ visibility + ", weather=" + weather + ", clouds="
				+ Arrays.toString(clouds) + ", remarks=" + remarks + "]";
	}
}

