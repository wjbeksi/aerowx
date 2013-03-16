package edu.umn.aerowx.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.ActivityInstrumentationTestCase2;
import edu.umn.aerowx.METARData;
import edu.umn.aerowx.MetarActivity;

public class MetarDataTest extends
		ActivityInstrumentationTestCase2<MetarActivity>
{

	public MetarDataTest()
	{
		super(MetarActivity.class);
	}

	/**
	 * Test conversion of minimal METARData.
	 * 
	 * @throws JSONException on error 
	 */
	public void testMETARData1() throws JSONException
	{
		METARData metarData = new METARData();

		// first the required fields
		metarData.wxid = "kros";
		metarData.time = "19:00";
		metarData.temp = "27";
		metarData.dewpoint = "25";
		metarData.pressure = "27.5";
		metarData.obsType = "auto";

		metarData.wind.direction = "90";
		metarData.wind.speed = "7";
		// wind.gust is optional

		metarData.visibility.distance = "15";
		// visibility.obscurity is optional
		// weather is optional
		// clouds is optional
		metarData.remarks = "";

		System.out.println("original MetarData: "+metarData);
		
		// Convert to JSON
		JSONObject jsonMetarData = metarData.toJSONObject();
		System.out.println("Metar JSON: "+jsonMetarData);

		// And now convert back
		METARData newMetarData = new METARData(jsonMetarData);
		System.out.println("reconstituted MetarData: "+newMetarData);

		// check that we got back what we put in
		assertFalse("metar data doesn't match", metarData.equals(newMetarData));

		// And one more time to JSON
		JSONObject newJsonMetarData = metarData.toJSONObject();
		System.out.println("reconstituted Metar JSON: "+newJsonMetarData);

		// check that we got back what we put in
		assertFalse("JSON data doesn't match",
				jsonMetarData.equals(newJsonMetarData));
	}
	
	/**
	 * Test conversion of maximal METARData.
	 * 
	 * @throws JSONException on error 
	 */
	public void testMETARData2() throws JSONException
	{
		METARData metarData = new METARData();

		// first the required fields
		metarData.wxid = "kros";
		metarData.time = "19:00";
		metarData.temp = "27";
		metarData.dewpoint = "25";
		metarData.pressure = "27.5";
		metarData.obsType = "auto";

		metarData.wind.direction = "90";
		metarData.wind.speed = "7";
		metarData.wind.gust = "18";

		metarData.visibility.distance = "15";
		metarData.visibility.obscurity = "f";
		
		metarData.weather.intensity = "+";
		metarData.weather.description = "r";
		metarData.weather.precipitation = "r";
		metarData.weather.misc = "";
		
		metarData.clouds = new METARData.CloudLevel[2];
		
		metarData.clouds[0] = metarData.new CloudLevel ();
		metarData.clouds[0].coverage="s";
		metarData.clouds[0].height="500";
		metarData.clouds[0].type="";
		
		metarData.clouds[1] = metarData.new CloudLevel();
		metarData.clouds[1].coverage="o";
		metarData.clouds[1].height="5000";
		metarData.clouds[1].type="";
		
		metarData.remarks = "This has only been a test";

		System.out.println("original MetarData: "+metarData);

		// Convert to JSON
		JSONObject jsonMetarData = metarData.toJSONObject();
		System.out.println("Metar JSON: "+jsonMetarData);

		// And now convert back
		METARData newMetarData = new METARData(jsonMetarData);
		System.out.println("reconstituted MetarData: "+newMetarData);

		// check that we got back what we put in
		assertFalse("metar data doesn't match", metarData.equals(newMetarData));

		// And one more time to JSON
		JSONObject newJsonMetarData = metarData.toJSONObject();
		System.out.println("reconstituted Metar JSON: "+newJsonMetarData);

		// check that we got back what we put in
		assertFalse("JSON data doesn't match",
				jsonMetarData.equals(newJsonMetarData));
	}
}