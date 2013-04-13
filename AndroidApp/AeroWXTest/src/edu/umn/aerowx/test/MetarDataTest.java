package edu.umn.aerowx.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.ActivityInstrumentationTestCase2;
import edu.umn.aerowx.MetarData;
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
	 * @throws JSONException
	 *             on error
	 */
	public void testMETARData1() throws JSONException
	{
		MetarData metarData = new MetarData();

		// first the required fields
		metarData.wxid = "kros";
		metarData.time = "19:00";
		metarData.temp = "27";
		metarData.dewpoint = "25";
		metarData.pressure = "27.5";
		metarData.obsType = "auto";

		metarData.wind = "ESE at 6 knots";

		metarData.visibility = "10 miles";
		metarData.remarks = "";

		System.out.println("original MetarData: " + metarData);

		// Convert to JSON
		JSONObject jsonMetarData = metarData.toJSONObject();
		System.out.println("Metar JSON: " + jsonMetarData);

		// And now convert back
		MetarData newMetarData = new MetarData(jsonMetarData);
		System.out.println("reconstituted MetarData: " + newMetarData);

		// check that we got back what we put in
		boolean equals = metarData.equals(newMetarData);
		assertTrue("METAR data doesn't match\n" + metarData + "\n"
				+ newMetarData, equals);

		// And one more time to JSON
		JSONObject newJsonMetarData = metarData.toJSONObject();
		System.out.println("reconstituted Metar JSON: " + newJsonMetarData);

		// check that we got back what we put in
		boolean equals2 = jsonMetarData.toString().equals(
				newJsonMetarData.toString());
		assertTrue("JSON data doesn't match\n" + jsonMetarData + "\n"
				+ newJsonMetarData, equals2);
	}

}