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
	public Weather weather;

	/** */
	public CloudLevel clouds[];

	/** various optional remarks */
	public String remarks;

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




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(clouds);
		result = prime * result
				+ ((dewpoint == null) ? 0 : dewpoint.hashCode());
		result = prime * result + ((obsType == null) ? 0 : obsType.hashCode());
		result = prime * result
				+ ((pressure == null) ? 0 : pressure.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((temp == null) ? 0 : temp.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result
				+ ((visibility == null) ? 0 : visibility.hashCode());
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		result = prime * result + ((wind == null) ? 0 : wind.hashCode());
		result = prime * result + ((wxid == null) ? 0 : wxid.hashCode());
		return result;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		METARData other = (METARData) obj;
		if (!Arrays.equals(clouds, other.clouds))
		{
			return false;
		}
		if (dewpoint == null)
		{
			if (other.dewpoint != null)
			{
				return false;
			}
		} else if (!dewpoint.equals(other.dewpoint))
		{
			return false;
		}
		if (obsType == null)
		{
			if (other.obsType != null)
			{
				return false;
			}
		} else if (!obsType.equals(other.obsType))
		{
			return false;
		}
		if (pressure == null)
		{
			if (other.pressure != null)
			{
				return false;
			}
		} else if (!pressure.equals(other.pressure))
		{
			return false;
		}
		if (remarks == null)
		{
			if (other.remarks != null)
			{
				return false;
			}
		} else if (!remarks.equals(other.remarks))
		{
			return false;
		}
		if (temp == null)
		{
			if (other.temp != null)
			{
				return false;
			}
		} else if (!temp.equals(other.temp))
		{
			return false;
		}
		if (time == null)
		{
			if (other.time != null)
			{
				return false;
			}
		} else if (!time.equals(other.time))
		{
			return false;
		}
		if (visibility == null)
		{
			if (other.visibility != null)
			{
				return false;
			}
		} else if (!visibility.equals(other.visibility))
		{
			return false;
		}
		if (weather == null)
		{
			if (other.weather != null)
			{
				return false;
			}
		} else if (!weather.equals(other.weather))
		{
			return false;
		}
		if (wind == null)
		{
			if (other.wind != null)
			{
				return false;
			}
		} else if (!wind.equals(other.wind))
		{
			return false;
		}
		if (wxid == null)
		{
			if (other.wxid != null)
			{
				return false;
			}
		} else if (!wxid.equals(other.wxid))
		{
			return false;
		}
		return true;
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
		public String gust;

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

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + ((gust == null) ? 0 : gust.hashCode());
			result = prime * result + ((speed == null) ? 0 : speed.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Wind other = (Wind) obj;
			if (direction == null)
			{
				if (other.direction != null)
				{
					return false;
				}
			} else if (!direction.equals(other.direction))
			{
				return false;
			}
			if (gust == null)
			{
				if (other.gust != null)
				{
					return false;
				}
			} else if (!gust.equals(other.gust))
			{
				return false;
			}
			if (speed == null)
			{
				if (other.speed != null)
				{
					return false;
				}
			} else if (!speed.equals(other.speed))
			{
				return false;
			}
			return true;
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
		public String obscurity;

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

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((distance == null) ? 0 : distance.hashCode());
			result = prime * result
					+ ((obscurity == null) ? 0 : obscurity.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Visibility other = (Visibility) obj;
			if (distance == null)
			{
				if (other.distance != null)
				{
					return false;
				}
			} else if (!distance.equals(other.distance))
			{
				return false;
			}
			if (obscurity == null)
			{
				if (other.obscurity != null)
				{
					return false;
				}
			} else if (!obscurity.equals(other.obscurity))
			{
				return false;
			}
			return true;
		}
	}

	/**
	 * Subclass for Weather information
	 */
	public class Weather
	{
		/** (- for light, empty for moderate, + for heavy) */
		public String intensity;
		/** */
		public String description;
		/** */
		public String precipitation;
		/** */
		public String obscuration;
		/** */
		public String misc;

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

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((description == null) ? 0 : description.hashCode());
			result = prime * result
					+ ((intensity == null) ? 0 : intensity.hashCode());
			result = prime * result + ((misc == null) ? 0 : misc.hashCode());
			result = prime * result
					+ ((obscuration == null) ? 0 : obscuration.hashCode());
			result = prime * result
					+ ((precipitation == null) ? 0 : precipitation.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Weather other = (Weather) obj;
			if (description == null)
			{
				if (other.description != null)
				{
					return false;
				}
			} else if (!description.equals(other.description))
			{
				return false;
			}
			if (intensity == null)
			{
				if (other.intensity != null)
				{
					return false;
				}
			} else if (!intensity.equals(other.intensity))
			{
				return false;
			}
			if (misc == null)
			{
				if (other.misc != null)
				{
					return false;
				}
			} else if (!misc.equals(other.misc))
			{
				return false;
			}
			if (obscuration == null)
			{
				if (other.obscuration != null)
				{
					return false;
				}
			} else if (!obscuration.equals(other.obscuration))
			{
				return false;
			}
			if (precipitation == null)
			{
				if (other.precipitation != null)
				{
					return false;
				}
			} else if (!precipitation.equals(other.precipitation))
			{
				return false;
			}
			return true;
		}
	}

	/**
	 * Subclass for CloudLevel information
	 */
	public class CloudLevel
	{
		/** */
		public String coverage;
		/** */
		public String height;
		/** */
		public String type;

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

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((coverage == null) ? 0 : coverage.hashCode());
			result = prime * result
					+ ((height == null) ? 0 : height.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			CloudLevel other = (CloudLevel) obj;
			if (coverage == null)
			{
				if (other.coverage != null)
				{
					return false;
				}
			} else if (!coverage.equals(other.coverage))
			{
				return false;
			}
			if (height == null)
			{
				if (other.height != null)
				{
					return false;
				}
			} else if (!height.equals(other.height))
			{
				return false;
			}
			if (type == null)
			{
				if (other.type != null)
				{
					return false;
				}
			} else if (!type.equals(other.type))
			{
				return false;
			}
			return true;
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
