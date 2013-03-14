package edu.umn.aerowx;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
	String wxid;

	/** Time/Date of observation (in GMT) */
	String time;

	/** Temperature */
	String temp;

	/** Dewpoint */
	String dewpoint;

	/** Air pressure/altimeter setting */
	String pressure;

	/** Type of observation (i.e. automatic) */
	String obsType;

	/** Surface wind */
	Wind wind;

	/** Visibility */
	Visibility visibility;

	/** Weather */
	Weather weather;

	/** */
	Clouds clouds[];

	/** various optional remarks */
	String remarks;

	/**
	 * Constructor for empty METARData
	 */
	public METARData()
	{
		super();
		wind=new Wind();
		visibility=new Visibility();
		weather=new Weather();
		clouds=new Clouds[0];
	}

	/**
	 * Constructor to create METARData from JSONObject.
	 * 
	 * @param object JSON object from which to create METARData object.
	 * 
	 * @throws JSONException when JSON data is invalid.
	 */
	METARData(JSONObject object) throws JSONException
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
			clouds = Clouds.getCloudsArray(object.getJSONArray("clouds"));
			remarks = object.getString("remarks");
		} catch (JSONException e)
		{
			throw e;
		}
	}

	JSONObject toJSONObject() throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("wxid", wxid);
		object.put("time", time);
		object.put("temp", temp);
		object.put("dewpoint", dewpoint);
		object.put("pressure", pressure);
		object.put("type", obsType);
		object.put("wind", wind);
		object.put("visibility", visibility);
		object.put("weather", weather);
		object.put("clouds", clouds);
		object.put("remarks", remarks);

		return object;
	}

	/** 
	 * Simple test to verify we can marshal/demarshal METAR data to/from JSON.
	 * @throws Exception 
	 * 
	 */
	static void testMetarData() throws Exception
	{
		METARData metarData=new METARData();
		
		metarData.wxid="kros";
		metarData.time="19:00";
		metarData.temp="27";
		
		JSONObject jsonMetarData = null;
		try
		{
			jsonMetarData=metarData.toJSONObject();
			Log.i(MetarActivity.class.toString(), jsonMetarData.toString());
		} catch (JSONException e1)
		{
			// this shouldn't happen
			throw e1;
		}

		// This should fail and generate a JSONException indicating "No value for dewpoint".
		try
		{
			METARData newMetarData=new METARData(jsonMetarData);
			Log.i(MetarActivity.class.toString(), newMetarData.toString());
		} catch (JSONException e)
		{
			if (!e.getMessage().contains("dewpoint"))
			{
				throw new Exception("Failed to throw \"No value for dewpoint\"");
			}
		}
		
		metarData.dewpoint="25";
		metarData.pressure="27.5";
		metarData.obsType="auto";
		metarData.wind.direction="90";
		metarData.wind.speed="7";
		
		// This shouldn't fail with JSONException indicating "No value for gust".
		try
		{
			METARData newMetarData=new METARData(jsonMetarData);
			Log.i(MetarActivity.class.toString(), newMetarData.toString());
		} catch (JSONException e)
		{
			if (e.getMessage().contains("gust"))
			{
				throw e;
			}
		}
	}
	
	public String getWxid()
	{
		return wxid;
	}

	public void setWxid(String wxid)
	{
		this.wxid = wxid;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTemp()
	{
		return temp;
	}

	public void setTemp(String temp)
	{
		this.temp = temp;
	}

	public String getDewpoint()
	{
		return dewpoint;
	}

	public void setDewpoint(String dewpoint)
	{
		this.dewpoint = dewpoint;
	}

	public String getPressure()
	{
		return pressure;
	}

	public void setPressure(String pressure)
	{
		this.pressure = pressure;
	}

	public String getObsType()
	{
		return obsType;
	}

	public void setObsType(String obsType)
	{
		this.obsType = obsType;
	}

	public Wind getWind()
	{
		return wind;
	}

	public void setWind(Wind wind)
	{
		this.wind = wind;
	}

	public Visibility getVisibility()
	{
		return visibility;
	}

	public void setVisibility(Visibility visibility)
	{
		this.visibility = visibility;
	}

	public Weather getWeather()
	{
		return weather;
	}

	public void setWeather(Weather weather)
	{
		this.weather = weather;
	}

	public Clouds[] getClouds()
	{
		return clouds;
	}

	public void setClouds(Clouds[] clouds)
	{
		this.clouds = clouds;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
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

class Wind
{
	/** direction */
	String direction;
	/** speed */
	String speed;
	/** optional wind gust speed */
	String gust;

	public Wind(JSONObject jsonObject) throws JSONException
	{
		direction = jsonObject.getString("direction");
		speed = jsonObject.getString("speed");
		gust = jsonObject.getString("gust");
	}

	public Wind()
	{
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	public String getSpeed()
	{
		return speed;
	}

	public void setSpeed(String speed)
	{
		this.speed = speed;
	}

	public String getGust()
	{
		return gust;
	}

	public void setGust(String gust)
	{
		this.gust = gust;
	}

	@Override
	public String toString()
	{
		return "Wind [direction=" + direction + ", speed=" + speed + ", gust="
				+ gust + "]";
	}
}

class Visibility
{
	/** Visibility distance */
	String distance;
	/** reason for obscurity (fog, smoke, etc) */
	String obscurity;

	public Visibility(JSONObject jsonObject) throws JSONException
	{
		distance = jsonObject.getString("distance");
		obscurity = jsonObject.getString("obscurity");
	}

	public Visibility()
	{
		// TODO Auto-generated constructor stub
	}

	public String getDistance()
	{
		return distance;
	}

	public void setDistance(String distance)
	{
		this.distance = distance;
	}

	public String getObscurity()
	{
		return obscurity;
	}

	public void setObscurity(String obscurity)
	{
		this.obscurity = obscurity;
	}

	@Override
	public String toString()
	{
		return "Visibility [distance=" + distance + ", obscurity=" + obscurity
				+ "]";
	}
}

class Weather
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

	public Weather(JSONObject jsonObject) throws JSONException
	{
		intensity = jsonObject.getString("intensity");
		description = jsonObject.getString("description");
		precipitation = jsonObject.getString("precipitation");
		misc = jsonObject.getString("misc");
	}

	public Weather()
	{
		// TODO Auto-generated constructor stub
	}

	public String getIntensity()
	{
		return intensity;
	}

	public void setIntensity(String intensity)
	{
		this.intensity = intensity;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getPrecipitation()
	{
		return precipitation;
	}

	public void setPrecipitation(String precipitation)
	{
		this.precipitation = precipitation;
	}

	public String getObscuration()
	{
		return obscuration;
	}

	public void setObscuration(String obscuration)
	{
		this.obscuration = obscuration;
	}

	public String getMisc()
	{
		return misc;
	}

	public void setMisc(String misc)
	{
		this.misc = misc;
	}

	@Override
	public String toString()
	{
		return "Weather [intensity=" + intensity + ", description="
				+ description + ", precipitation=" + precipitation
				+ ", obscuration=" + obscuration + ", misc=" + misc + "]";
	}
}

class Clouds
{
	/** */
	String coverage;
	/** */
	String height;
	/** */
	String type;

	public static Clouds[] getCloudsArray(JSONArray jsonArray)
			throws JSONException
	{
		int length = jsonArray.length();
		Clouds clouds[] = new Clouds[length];

		for (int i = 0; i < jsonArray.length(); i++)
		{
			Clouds layer = new Clouds();
			layer.coverage = ((JSONObject) (jsonArray.get(i)))
					.getString("coverage");
			layer.height = ((JSONObject) (jsonArray.get(i)))
					.getString("height");
			layer.type = ((JSONObject) (jsonArray.get(i))).getString("type");
		}
		return clouds;
	}

	public String getCoverage()
	{
		return coverage;
	}

	public void setCoverage(String coverage)
	{
		this.coverage = coverage;
	}

	public String getHeight()
	{
		return height;
	}

	public void setHeight(String height)
	{
		this.height = height;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "Clouds [coverage=" + coverage + ", height=" + height
				+ ", type=" + type + "]";
	}

}
