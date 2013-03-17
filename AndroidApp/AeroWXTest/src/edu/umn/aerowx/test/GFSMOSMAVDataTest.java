package edu.umn.aerowx.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.ActivityInstrumentationTestCase2;
import edu.umn.aerowx.GFSMOSMAVData;
import edu.umn.aerowx.MetarActivity;

public class GFSMOSMAVDataTest extends
		ActivityInstrumentationTestCase2<MetarActivity>
{

	public GFSMOSMAVDataTest()
	{
		super(MetarActivity.class);
	}

	/**
	 * Test conversion of minimal GFSMOSMAVData.
	 * 
	 * @throws JSONException on error 
	 */
	public void testGFSMOSMAVData1() throws JSONException
	{
		GFSMOSMAVData gfsMosMavData = new GFSMOSMAVData();

		// first the required fields
		gfsMosMavData.wxid = "kros";
		gfsMosMavData.time = "19:00";
		gfsMosMavData.high = "27";
		gfsMosMavData.low = "25";

		for (int i = 0; i < 4; ++i)
		{
			gfsMosMavData.periods[i]=gfsMosMavData.new Period();
			gfsMosMavData.periods[i].date = "1/1/2013";
			gfsMosMavData.periods[i].hour = "7";
			gfsMosMavData.periods[i].temp = "27";
			gfsMosMavData.periods[i].dewpoint = "27";
			gfsMosMavData.periods[i].cover = "";
			gfsMosMavData.periods[i].wind.direction = "S";
			gfsMosMavData.periods[i].wind.speed = "-20";
			gfsMosMavData.periods[i].pop6 = "";
			gfsMosMavData.periods[i].pop12 = "";
			gfsMosMavData.periods[i].qpf6 = "";
			gfsMosMavData.periods[i].qpf12 = "";
			gfsMosMavData.periods[i].thund6 = "";
			gfsMosMavData.periods[i].thund12 = "";
			gfsMosMavData.periods[i].popz = "";
			gfsMosMavData.periods[i].pops = "";
			gfsMosMavData.periods[i].type = "";
			gfsMosMavData.periods[i].snow = "";
			gfsMosMavData.periods[i].visibility = "";
			gfsMosMavData.periods[i].obscurity = "";
			gfsMosMavData.periods[i].ceiling = "";
		}
		
		System.out.println("original GfsMosMavData: "+gfsMosMavData);
		
		// Convert to JSON
		JSONObject jsonGfsMosMavData = gfsMosMavData.toJSONObject();
		System.out.println("GfsMosMav JSON: "+jsonGfsMosMavData);

		// And now convert back
		GFSMOSMAVData newGfsMosMavData = new GFSMOSMAVData(jsonGfsMosMavData);
		System.out.println("reconstituted GfsMosMavData: "+newGfsMosMavData);

		// check that we got back what we put in
		boolean equals = gfsMosMavData.equals(newGfsMosMavData);
		assertTrue("gfsMosMav data doesn't match", equals);

		// And one more time to JSON
		JSONObject newJsonGfsMosMavData = newGfsMosMavData.toJSONObject();
		System.out.println("reconstituted GfsMosMav JSON: "+newJsonGfsMosMavData);

		// check that we got back what we put in
		boolean equals2 = jsonGfsMosMavData.toString().equals(newJsonGfsMosMavData.toString());
		assertTrue("JSON data doesn't match",
				equals2);
	}
}