/**
 * 
 */
package edu.umn.aerowx;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Wayne Johnson
 *
 */
public class GFSData
{
	//* Weather Station ID (4 letters) */
	String wxid;

	//* Time/Date of forecast (in GMT) */
	String time;

	//* High temperature */
	String high;

	//* low temperature */
	String low;

	//* forecast periods */
	Period periods[] ;

	/**
	 * 
	 */
	public GFSData()
	{
		super();
	}

	/**
	 * @throws JSONException
	 * 
	 */
	public GFSData(JSONObject object) throws JSONException
	{
		super();

		// All these are required in a METAR message
		wxid = object.getString("wxid");
		time = object.getString("time");
		high = object.getString("high");
		low = object.getString("low");

		periods = getPeriodsArray(object.getJSONArray("periods"));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "GFSData [wxid=" + wxid + ", time=" + time + ", high=" + high
				+ ", low=" + low + ", periods=" + Arrays.toString(periods)
				+ "]";
	}

	/* (non-Javadoc)
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
		GFSData other = (GFSData) obj;
		if (high == null)
		{
			if (other.high != null)
			{
				return false;
			}
		} else if (!high.equals(other.high))
		{
			return false;
		}
		if (low == null)
		{
			if (other.low != null)
			{
				return false;
			}
		} else if (!low.equals(other.low))
		{
			return false;
		}
		if (!Arrays.equals(periods, other.periods))
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

	class Period
	{
		/** Date of period */
		String date;
	
		/** Beginning hour of period */
		String hour;
	
		/** Temperature */
		String temp;
	
		/** Dewpoint */
		String dewpoint;
	
		/** Cloud cover */
		String cover;
	
		/** Surface wind */
		Wind wind;
	
		/** Probability of precipitation for previous 6 hours */
		String pop6;
	
		/** Probability of precipitation for previous 12 hours */
		String pop12;
	
		/** Quantitative precipitation forecast (accumulation) for previous 6 hours */
		String qpf6;
	
		/** Quantitative precipitation forecast (accumulation) for previous 12 hours */
		String qpf12;
	
		/** Probability of thunderstorms for previous 6 hours */
		String thund6;
	
		/** Probability of thunderstorms for previous 12 hours */
		String thund12;
	
		/** Probability of freezing precipitation */
		String popz;
	
		/** Probability of snow */
		String pops;
	
		/** Precipitation type */
		String type;
	
		/** Snowfall accumulation */
		String snow;
	
		/** Visibility */
		String visibility;
	
		/** possible reason for obscurity (fog, smoke, etc) */
		String obscurity;
	
		/** Ceiling altitude */
		String ceiling;

		/**
		 * 
		 */
		public Period()
		{
			super();
		}

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

		/* (non-Javadoc)
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

		/* (non-Javadoc)
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

	private Period[] getPeriodsArray(JSONArray jsonArray) throws JSONException
	{
		int length = jsonArray.length();
		Period periods[] = new Period[length];

		for (int i = 0; i < jsonArray.length(); i++)
		{
			periods[i] = new GFSData.Period(
					(JSONObject) (jsonArray.get(i)));
		}
		return periods;
	}

	class Wind
	{
		/** direction */
		String direction;
	
		/** speed */
		String speed;
	
		/** optional wind gust speed */
		String gust;

		/**
		 * 
		 */
		public Wind()
		{
			super();
		}

		public Wind(JSONObject object) throws JSONException
		{
			direction = object.getString("direction");
			speed = object.getString("speed");
			gust = object.getString("gust");
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
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
}
