package edu.umn.aerowx;

import java.util.Arrays;

import org.json.JSONArray;
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
	public Wind wind;

	/** Visibility */
	public Visibility visibility;

	/** Weather */
	Weather weather;

	/** */
	CloudLevel clouds[];

	/** various optional remarks */
	String remarks;

	/**
	 * Constructor for empty METARData
	 */
	public METARData()
	{
		super();
		wind = new Wind();
		visibility = new Visibility();
		weather = new Weather();
		clouds = new CloudLevel[0];
	}

	/**
	 * Constructor to create METARData from JSONObject.
	 * 
	 * @param object
	 *            JSON object from which to create METARData object.
	 * 
	 * @throws JSONException
	 *             when JSON data is invalid.
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
			wind = new Wind(object.getJSONObject("wind"));
			visibility = new Visibility(object.getJSONObject("visibility"));
			weather = new Weather(object.getJSONObject("weather"));
			clouds = getCloudsArray(object.getJSONArray("clouds"));
			remarks = object.optString("remarks");
		} catch (JSONException e)
		{
			throw e;
		}
	}

	/**
	 * Serialize a METARData object into JSON
	 * 
	 * @return JSONObject containing all the stuff.
	 * 
	 * @throws JSONException
	 *             When JSON barfs on bad data.
	 */
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
			object.put("clouds", toJSONArray(clouds));
		}
		object.put("remarks", remarks);

		return object;
	}

	/** Quintessential toString method */
	@Override
	public String toString()
	{
		return "METARData [wxid=" + wxid + ", time=" + time + ", temp=" + temp
				+ ", dewpoint=" + dewpoint + ", pressure=" + pressure
				+ ", obsType=" + obsType + ", wind=" + wind + ", visibility="
				+ visibility + ", weather=" + weather + ", clouds="
				+ Arrays.toString(clouds) + ", remarks=" + remarks + "]";
	}

	/**
	 * Subclass for Wind information
	 */
	public class Wind
	{
		/** direction */
		public String direction;
		/** speed */
		public String speed;
		/** optional wind gust speed */
		String gust;

		/**
		 * Constructor for empty Wind data
		 */
		public Wind()
		{
		}

		/**
		 * Constructor to create Wind from JSONObject.
		 * 
		 * @param object
		 *            JSON object from which to create Wind object.
		 * 
		 * @throws JSONException
		 *             when JSON data is invalid.
		 */
		public Wind(JSONObject jsonObject) throws JSONException
		{
			direction = jsonObject.getString("direction");
			speed = jsonObject.getString("speed");
			gust = jsonObject.optString("gust");
		}

		/**
		 * Serialize a Wind object into JSON
		 * 
		 * @return JSONObject containing all the stuff.
		 * 
		 * @throws JSONException
		 *             When JSON barfs on bad data.
		 */
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

		/** Quintessential toString method */
		@Override
		public String toString()
		{
			return "Wind [direction=" + direction + ", speed=" + speed
					+ ", gust=" + gust + "]";
		}
	}

	/**
	 * Subclass for Visibility information
	 */
	public class Visibility
	{
		/** Visibility distance */
		public String distance;

		/** reason for obscurity (fog, smoke, etc) */
		String obscurity;

		/**
		 * Constructor for empty Visibility data
		 */
		public Visibility()
		{
		}

		/**
		 * Constructor to create Visibility from JSONObject.
		 * 
		 * @param object
		 *            JSON object from which to create Visibility object.
		 * 
		 * @throws JSONException
		 *             when JSON data is invalid.
		 */
		public Visibility(JSONObject jsonObject) throws JSONException
		{
			distance = jsonObject.getString("distance");
			obscurity = jsonObject.optString("obscurity");
		}

		/**
		 * Serialize a Visibility object into JSON
		 * 
		 * @return JSONObject containing all the stuff.
		 * 
		 * @throws JSONException
		 *             When JSON barfs on bad data.
		 */
		public Object toJSONObject() throws JSONException
		{
			JSONObject object = new JSONObject();
			object.put("distance", distance);
			object.put("obscurity", obscurity);
			return object;
		}

		/** Quintessential toString method */
		@Override
		public String toString()
		{
			return "Visibility [distance=" + distance + ", obscurity="
					+ obscurity + "]";
		}
	}

	/**
	 * Subclass for Weather information
	 */
	public class Weather
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

		/**
		 * Constructor for empty Weather data
		 */
		public Weather()
		{
		}

		/**
		 * Constructor to create Weather from JSONObject.
		 * 
		 * @param object
		 *            JSON object from which to create Weather object.
		 * 
		 * @throws JSONException
		 *             when JSON data is invalid.
		 */
		public Weather(JSONObject jsonObject) throws JSONException
		{
			intensity = jsonObject.optString("intensity");
			description = jsonObject.optString("description");
			precipitation = jsonObject.optString("precipitation");
			misc = jsonObject.optString("misc");
		}

		/**
		 * Serialize a Weather object into JSON
		 * 
		 * @return JSONObject containing all the stuff.
		 * 
		 * @throws JSONException
		 *             When JSON barfs on bad data.
		 */
		public Object toJSONObject() throws JSONException
		{
			JSONObject object = new JSONObject();
			object.put("intensity", intensity);
			object.put("description", description);
			object.put("precipitation", precipitation);
			return object;
		}

		/** Quintessential toString method */
		@Override
		public String toString()
		{
			return "Weather [intensity=" + intensity + ", description="
					+ description + ", precipitation=" + precipitation
					+ ", obscuration=" + obscuration + ", misc=" + misc + "]";
		}
	}

	/**
	 * Subclass for CloudLevel information
	 */
	public class CloudLevel
	{
		/** */
		String coverage;
		/** */
		String height;
		/** */
		String type;

		/**
		 * Constructor for empty Weather data
		 */
		public CloudLevel()
		{
			super();
		}

		/**
		 * Constructor to create CloudLevel from JSONObject.
		 * 
		 * @param object
		 *            JSON object from which to create CloudLevel object.
		 * 
		 * @throws JSONException
		 *             when JSON data is invalid.
		 */
		public CloudLevel(JSONObject jsonObject) throws JSONException
		{
			coverage = jsonObject.getString("coverage");
			height = jsonObject.getString("height");
			type = jsonObject.getString("type");
		}

		/**
		 * Serialize a CloudLevel object into JSON
		 * 
		 * @return JSONObject containing all the stuff.
		 * 
		 * @throws JSONException
		 *             When JSON barfs on bad data.
		 */
		public Object toJSONObject() throws JSONException
		{
			JSONObject object = new JSONObject();
			object.put("coverage", coverage);
			object.put("height", height);
			object.put("type", type);
			return object;
		}

		/** Quintessential toString method */
		@Override
		public String toString()
		{
			return "Clouds [coverage=" + coverage + ", height=" + height
					+ ", type=" + type + "]";
		}

	}

	/**
	 * Serialize a CloudLevel array into JSON
	 * 
	 * @param clouds
	 *            cloud level array
	 * @return JSONObject containing all the stuff.
	 * @throws JSONException
	 *             When JSON barfs on bad data.
	 */
	public JSONArray toJSONArray(CloudLevel[] clouds) throws JSONException
	{
		JSONArray jsonArray = new JSONArray();

		for (CloudLevel cloud : clouds)
		{
			jsonArray.put(cloud.toJSONObject());
		}
		return jsonArray;
	}

	/**
	 * Create CloudLevel array from JSON
	 * 
	 * @param jsonArray
	 *            JSONArray containing cloud level data
	 * @return CloudLevel array.
	 * @throws JSONException
	 *             when JSON data is invalid.
	 */
	public CloudLevel[] getCloudsArray(JSONArray jsonArray)
			throws JSONException
	{
		int length = jsonArray.length();
		CloudLevel clouds[] = new CloudLevel[length];

		for (int i = 0; i < jsonArray.length(); i++)
		{
			clouds[i] = new METARData.CloudLevel(
					(JSONObject) (jsonArray.get(i)));
		}
		return clouds;
	}
}
