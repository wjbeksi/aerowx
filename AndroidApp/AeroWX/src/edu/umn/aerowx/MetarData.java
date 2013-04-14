package edu.umn.aerowx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

/**
 * This class manages data coming from the METAR weather server.
 * 
 * @author Wayne Johnson
 * 
 */
public class MetarData
{
	/* METAR Data */
	/** Weather Station ID (4 letters) */
	public String wxid;

	/** Time/Date of observation (in GMT) */
	public Date time;
 
	/** Temperature */
	public String temp;

	/** Dewpoint */
	public String dewpoint;

	/** Air pressure/altimeter setting */
	public String pressure;

	/** Type of observation (i.e. automatic) */
	public String obsType;

	/** Surface wind */
	public String wind;

	/** Visibility */
	public String visibility;

	/** Weather */
	public String weather;

	/** */
	public String sky;

	/** various optional remarks */
	public String remarks;

	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat sdfMETAR = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy zzz");

	/**
	 * Constructor for empty METARData
	 */
	public MetarData()
	{
		super();
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
	public MetarData(JSONObject object) throws JSONException
	{
		super();

		// METAR data is encased in a metar JSON object
		JSONObject metarObject = object.getJSONObject("metar");

		// All these are required in a METAR message
		wxid = metarObject.optString("station", null);
		time = convertTime(metarObject.optString("time", null));
		temp = metarObject.optString("temperature", null);
		dewpoint = metarObject.optString("dew point", null);
		pressure = metarObject.optString("pressure", null);
		obsType = metarObject.optString("type", null);
		wind = metarObject.optString("wind", null);
		visibility = metarObject.optString("visibility", null);
		weather = metarObject.optString("weather", null);
		sky = metarObject.optString("sky", null);
		remarks = metarObject.optString("remarks", null);
	}

	private Date convertTime(String timeString)
	{
		Date date = null;
		try
		{
			date=sdfMETAR.parse(timeString+" GMT");
		} catch (ParseException e)
		{
			return null;
		}
		
		return date;
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
		object.put("station", wxid);
		object.put("time", time);
		object.put("temperature", temp);
		object.put("dew point", dewpoint);
		object.put("pressure", pressure);
		object.put("type", obsType);
		object.put("wind", wind);
		object.put("visibility", visibility);
		object.put("weather", weather);
		object.put("sky", sky);
		object.put("remarks", remarks);

		JSONObject metar=new JSONObject();
		metar.put("metar", object);
		return metar;
	}

	/** Quintessential toString method */
	@Override
	public String toString()
	{
		return "METARData [" + (wxid != null ? "wxid=" + wxid + ", " : "")
				+ (time != null ? "time=" + time + ", " : "")
				+ (temp != null ? "temp=" + temp + ", " : "")
				+ (dewpoint != null ? "dewpoint=" + dewpoint + ", " : "")
				+ (pressure != null ? "pressure=" + pressure + ", " : "")
				+ (obsType != null ? "obsType=" + obsType + ", " : "")
				+ (wind != null ? "wind=" + wind + ", " : "")
				+ (visibility != null ? "visibility=" + visibility + ", " : "")
				+ (weather != null ? "weather=" + weather + ", " : "")
				+ (sky != null ? "sky=" + sky + ", " : "")
				+ (remarks != null ? "remarks=" + remarks : "") + "]";
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
				+ ((dewpoint == null) ? 0 : dewpoint.hashCode());
		result = prime * result + ((obsType == null) ? 0 : obsType.hashCode());
		result = prime * result
				+ ((pressure == null) ? 0 : pressure.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((sky == null) ? 0 : sky.hashCode());
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
		MetarData other = (MetarData) obj;
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
		if (sky == null)
		{
			if (other.sky != null)
			{
				return false;
			}
		} else if (!sky.equals(other.sky))
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
}
