/**
 * 
 */
package edu.umn.aerowx;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class manages data coming from the GFS MOS MAV weather server.
 * 
 * @author Wayne Johnson
 * 
 */
public class GFSMOSMAVData
{
	/* GFSMOSMAV Data */
	
	// * Weather Station ID (4 letters) */
	public String wxid;

	// * Time/Date of forecast (in GMT) */
	public String time;

	// * High temperature */
	public String high;

	// * low temperature */
	public String low;

	// * forecast periods */
	public Period periods[];

	/**
	 * Constructor for empty GFSMOSMAVData
	 */
	public GFSMOSMAVData()
	{
		super();
		periods=new Period[4];
	}

	/**
	 * Constructor to create GFSMOSMAVData from JSONObject.
	 * 
	 * @param object
	 *            JSON object from which to create GFSMOSMAVData object.
	 * @throws JSONException
	 */
	public GFSMOSMAVData(JSONObject object) throws JSONException
	{
		super();

		// All these are required in a METAR message
		wxid = object.getString("wxid");
		time = object.getString("time");
		high = object.getString("high");
		low = object.getString("low");

		periods = getPeriodsArray(object.getJSONArray("periods"));
	}

	/**
	 * Serialize GFSMOSMAVData object into JSON
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
		object.put("high", high);
		object.put("low", low);
		if (periods != null)
		{
			object.put("periods", toJSONArray(periods));
		}
		return object;
	}

	/** Quintessential toString method */
	@Override
	public String toString()
	{
		return "GFSData [wxid=" + wxid + ", time=" + time + ", high=" + high
				+ ", low=" + low + ", periods=" + Arrays.toString(periods)
				+ "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + Arrays.hashCode(periods);
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((wxid == null) ? 0 : wxid.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			Log.d(this.getClass().getName(), "this equals obj");
			return true;
		}
		if (obj == null)
		{
			Log.d(this.getClass().getName(), "obj==null");
			return false;
		}
		if (getClass() != obj.getClass())
		{
			Log.d(this.getClass().getName(), "getClass("+getClass()+") != obj.getClass("+obj.getClass()+")");
			return false;
		}
		GFSMOSMAVData other = (GFSMOSMAVData) obj;
		if (high == null)
		{
			if (other.high != null)
			{
				Log.d(getClass().getName(), "high==null && other.high="+other.high);
				return false;
			}
		} else if (!high.equals(other.high))
		{
			Log.d(getClass().getName(), "high="+high+" && other.high="+other.high);
			return false;
		}
		if (low == null)
		{
			if (other.low != null)
			{
				Log.d(getClass().getName(), "low==null && other.low="+other.low);
				return false;
			}
		} else if (!low.equals(other.low))
		{
			Log.d(getClass().getName(), "low="+low+" && other.low="+other.low);
			return false;
		}
		if (!Arrays.equals(periods, other.periods))
		{
			Log.d(getClass().getName(), "Arrays !=");
			return false;
		}
		if (time == null)
		{
			if (other.time != null)
			{
				Log.d(getClass().getName(), "time==null && other.time="+other.time);
				return false;
			}
		} else if (!time.equals(other.time))
		{
			Log.d(getClass().getName(), "time="+time+" && other.time="+other.time);
			return false;
		}
		if (wxid == null)
		{
			if (other.wxid != null)
			{
				Log.d(getClass().getName(), "wxid==null && other.wxid="+other.wxid);
				return false;
			}
		} else if (!wxid.equals(other.wxid))
		{
			Log.d(getClass().getName(), "wxid="+wxid+" && other.wxid="+other.wxid);
			return false;
		}
		return true;
	}

	/**
	 * Subclass for Period information
	 */
	public class Period
	{
		/** Date of period */
		public String date;

		/** Beginning hour of period */
		public String hour;

		/** Temperature */
		public String temp;

		/** Dewpoint */
		public String dewpoint;

		/** Cloud cover */
		public String cover;

		/** Surface wind */
		public Wind wind;

		/** Probability of precipitation for previous 6 hours */
		public String pop6;

		/** Probability of precipitation for previous 12 hours */
		public String pop12;

		/**
		 * Quantitative precipitation forecast (accumulation) for previous 6
		 * hours
		 */
		public String qpf6;

		/**
		 * Quantitative precipitation forecast (accumulation) for previous 12
		 * hours
		 */
		public String qpf12;

		/** Probability of thunderstorms for previous 6 hours */
		public String thund6;

		/** Probability of thunderstorms for previous 12 hours */
		public String thund12;

		/** Probability of freezing precipitation */
		public String popz;

		/** Probability of snow */
		public String pops;

		/** Precipitation type */
		public String type;

		/** Snowfall accumulation */
		public String snow;

		/** Visibility */
		public String visibility;

		/** possible reason for obscurity (fog, smoke, etc) */
		public String obscurity;

		/** Ceiling altitude */
		public String ceiling;

		/**
		 * Constructor for empty Period data
		 */
		public Period()
		{
			super();
			wind = new Wind();
		}

		/**
		 * Constructor to create Period from JSONObject.
		 * 
		 * @param object
		 *            JSON object from which to create Period object.
		 * 
		 * @throws JSONException
		 *             when JSON data is invalid.
		 */
		public Period(JSONObject object) throws JSONException
		{
			date = object.getString("date");
			hour = object.getString("hour");
			temp = object.getString("temp");
			dewpoint = object.getString("dewpoint");
			cover = object.getString("cover");
			wind = new Wind(object.getJSONObject("wind"));
			pop6 = object.getString("pop6");
			pop12 = object.getString("pop12");
			qpf6 = object.getString("qpf6");
			qpf12 = object.getString("qpf12");
			thund6 = object.getString("thund6");
			thund12 = object.getString("thund12");
			popz = object.getString("popz");
			pops = object.getString("pops");
			type = object.getString("type");
			snow = object.getString("snow");
			visibility = object.getString("visibility");
			obscurity = object.getString("obscurity");
			ceiling = object.getString("ceiling");
		}

		/**
		 * Serialize a Period object into JSON
		 * 
		 * @return JSONObject containing all the stuff.
		 * 
		 * @throws JSONException
		 *             When JSON barfs on bad data.
		 */
		public Object toJSONObject() throws JSONException
		{
			JSONObject object = new JSONObject();

			object.put("date", date);
			object.put("hour", hour);
			object.put("temp", temp);
			object.put("dewpoint", dewpoint);
			object.put("cover", cover);
			if (wind != null)
			{
				object.put("wind", wind.toJSONObject());
			}
			object.put("pop6", pop6);
			object.put("pop12", pop12);
			object.put("qpf6", qpf6);
			object.put("qpf12", qpf12);
			object.put("thund6", thund6);
			object.put("thund12", thund12);
			object.put("popz", popz);
			object.put("pops", pops);
			object.put("type", type);
			object.put("snow", snow);
			object.put("visibility", visibility);
			object.put("obscurity", obscurity);
			object.put("ceiling", ceiling);

			return object;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "Period [date=" + date + ", hour=" + hour + ", temp=" + temp
					+ ", dewpoint=" + dewpoint + ", cover=" + cover + ", wind="
					+ wind + ", pop6=" + pop6 + ", pop12=" + pop12 + ", qpf6="
					+ qpf6 + ", qpf12=" + qpf12 + ", thund6=" + thund6
					+ ", thund12=" + thund12 + ", popz=" + popz + ", pops="
					+ pops + ", type=" + type + ", snow=" + snow
					+ ", visibility=" + visibility + ", obscurity=" + obscurity
					+ ", ceiling=" + ceiling + "]";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((ceiling == null) ? 0 : ceiling.hashCode());
			result = prime * result + ((cover == null) ? 0 : cover.hashCode());
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result
					+ ((dewpoint == null) ? 0 : dewpoint.hashCode());
			result = prime * result + ((hour == null) ? 0 : hour.hashCode());
			result = prime * result
					+ ((obscurity == null) ? 0 : obscurity.hashCode());
			result = prime * result + ((pop12 == null) ? 0 : pop12.hashCode());
			result = prime * result + ((pop6 == null) ? 0 : pop6.hashCode());
			result = prime * result + ((pops == null) ? 0 : pops.hashCode());
			result = prime * result + ((popz == null) ? 0 : popz.hashCode());
			result = prime * result + ((qpf12 == null) ? 0 : qpf12.hashCode());
			result = prime * result + ((qpf6 == null) ? 0 : qpf6.hashCode());
			result = prime * result + ((snow == null) ? 0 : snow.hashCode());
			result = prime * result + ((temp == null) ? 0 : temp.hashCode());
			result = prime * result
					+ ((thund12 == null) ? 0 : thund12.hashCode());
			result = prime * result
					+ ((thund6 == null) ? 0 : thund6.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result
					+ ((visibility == null) ? 0 : visibility.hashCode());
			result = prime * result + ((wind == null) ? 0 : wind.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
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
			Period other = (Period) obj;
			if (ceiling == null)
			{
				if (other.ceiling != null)
				{
					return false;
				}
			} else if (!ceiling.equals(other.ceiling))
			{
				return false;
			}
			if (cover == null)
			{
				if (other.cover != null)
				{
					return false;
				}
			} else if (!cover.equals(other.cover))
			{
				return false;
			}
			if (date == null)
			{
				if (other.date != null)
				{
					return false;
				}
			} else if (!date.equals(other.date))
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
			if (hour == null)
			{
				if (other.hour != null)
				{
					return false;
				}
			} else if (!hour.equals(other.hour))
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
			if (pop12 == null)
			{
				if (other.pop12 != null)
				{
					return false;
				}
			} else if (!pop12.equals(other.pop12))
			{
				return false;
			}
			if (pop6 == null)
			{
				if (other.pop6 != null)
				{
					return false;
				}
			} else if (!pop6.equals(other.pop6))
			{
				return false;
			}
			if (pops == null)
			{
				if (other.pops != null)
				{
					return false;
				}
			} else if (!pops.equals(other.pops))
			{
				return false;
			}
			if (popz == null)
			{
				if (other.popz != null)
				{
					return false;
				}
			} else if (!popz.equals(other.popz))
			{
				return false;
			}
			if (qpf12 == null)
			{
				if (other.qpf12 != null)
				{
					return false;
				}
			} else if (!qpf12.equals(other.qpf12))
			{
				return false;
			}
			if (qpf6 == null)
			{
				if (other.qpf6 != null)
				{
					return false;
				}
			} else if (!qpf6.equals(other.qpf6))
			{
				return false;
			}
			if (snow == null)
			{
				if (other.snow != null)
				{
					return false;
				}
			} else if (!snow.equals(other.snow))
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
			if (thund12 == null)
			{
				if (other.thund12 != null)
				{
					return false;
				}
			} else if (!thund12.equals(other.thund12))
			{
				return false;
			}
			if (thund6 == null)
			{
				if (other.thund6 != null)
				{
					return false;
				}
			} else if (!thund6.equals(other.thund6))
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
			return true;
		}
	}

	/**
	 * Create Period array from JSON
	 * 
	 * @param jsonArray
	 *            JSONArray containing period data
	 * @return Period array.
	 * @throws JSONException
	 *             when JSON data is invalid.
	 */
	private Period[] getPeriodsArray(JSONArray jsonArray) throws JSONException
	{
		int length = jsonArray.length();
		Period periods[] = new Period[length];

		for (int i = 0; i < jsonArray.length(); i++)
		{
			periods[i] = new GFSMOSMAVData.Period(
					(JSONObject) (jsonArray.get(i)));
		}
		return periods;
	}

	/**
	 * Serialize a Period array into JSON
	 * 
	 * @param periods
	 *            period array
	 * @return JSONObject containing all the stuff.
	 * @throws JSONException
	 *             When JSON barfs on bad data.
	 */
	private Object toJSONArray(Period[] periods) throws JSONException
	{
		JSONArray jsonArray = new JSONArray();

		for (Period period : periods)
		{
			jsonArray.put(period.toJSONObject());
		}
		return jsonArray;
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
			super();
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
		public Wind(JSONObject object) throws JSONException
		{
			direction = object.getString("direction");
			speed = object.getString("speed");
			gust = object.optString("gust", null);
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

		/*
		 * (non-Javadoc)
		 * 
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

		/*
		 * (non-Javadoc)
		 * 
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
}
