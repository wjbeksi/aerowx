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
	 * Test method for {@link edu.umn.aerowx.METARData#METARData()}.
	 * @throws JSONException 
	 */
	public void testMETARData() throws JSONException
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

		// Convert to JSON
		JSONObject jsonMetarData = metarData.toJSONObject();

		// And now convert back
		METARData newMetarData = new METARData(jsonMetarData);

		// And one more time to JSON
		JSONObject newJsonMetarData = metarData.toJSONObject();

		assertFalse("metar data doesn't match", metarData.equals(newMetarData));
		assertFalse("JSON data doesn't match",
				jsonMetarData.equals(newJsonMetarData));
	}


}
