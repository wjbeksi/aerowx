#!/usr/bin/env python
#
# A Python package for interpreting GFS MOS MAV weather reports.
# 
"""
This module defines the Mav class.  A Mav object represents the weather report
encoded by a single GFS MOS MAV message.
"""
import re
import json
import string
import pprint

MAX_COLS = 22  # Number of columns in a MAV message

# Exceptions

class ParserError(Exception):
    """Exception raised when an unparseable group is found in body of the 
       message."""
    pass

# Regular expressions to decode various groups of the MAV message 

STATION_RE = re.compile(r"([A-Z][A-Z0-9]{3})\s+")

TIMESTAMP_RE = re.compile(r"\s+GFS\sMOS\sGUIDANCE\s+(\d+/\d+/\d+\s+\d+\s+UTC)")

DT_RE = re.compile(r"DT\s+(.+)")

HR_RE = re.compile(r"""HR\s+(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)""",
                            re.VERBOSE)

NX_RE = re.compile(r"""[N|X]/[X|N]\s(\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)
                                    (\d\d\d|[\s|-]\d\d|\s[\s|-]\d|\s\s\s)""",
                                    re.VERBOSE)

TMP_RE = re.compile(r"""TMP\s+(\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)""",
                              re.VERBOSE)

DPT_RE = re.compile(r"""DPT\s+(\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)(\d\d\d|\s*-?\d\d|\s*-?\d)
                              (\d\d\d|\s*-?\d\d|\s*-?\d)""",
                              re.VERBOSE)

CLD_RE = re.compile(r"""CLD\s+(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s
                              (\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s
                              (\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)\s(\w+)""",
                              re.VERBOSE)

WDR_RE = re.compile(r"""WDR\s+(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)""",
                              re.VERBOSE)

WSP_RE = re.compile(r"""WSP\s+(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                              (\d+)\s(\d+)\s(\d+)""",
                              re.VERBOSE)

P06_RE = re.compile(r"""P06\s\s(\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)""",
                               re.VERBOSE)

P12_RE = re.compile(r"""P12\s\s(\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)\s(\s\s|\s\d|\d\d)\s
                               (\s\s|\s\d|\d\d)""",
                               re.VERBOSE)

Q06_RE = re.compile(r"""Q06\s\s(\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)""",
                               re.VERBOSE)

Q12_RE = re.compile(r"""Q12\s\s(\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)""",
                               re.VERBOSE)

T06_RE = re.compile(r"T06\s\s(.+)")

T12_RE = re.compile(r"T12\s\s(.+)")

POZ_RE = re.compile(r"""POZ\s+(\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)""",
                              re.VERBOSE)

POS_RE = re.compile(r"""POS\s+(\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)(\d\d\d|\s\d\d|\s\s\d)
                              (\d\d\d|\s\d\d|\s\s\d)""",
                              re.VERBOSE)

TYP_RE = re.compile(r"""TYP\s+(\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s
                              (\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s
                              (\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s
                              (\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s(\s*\w)\s
                              (\s*\w)""",
                              re.VERBOSE)

SNW_RE = re.compile(r"""SNW\s\s(\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)\s
                               (\s\s|\s\d)\s(\s\s|\s\d)\s(\s\s|\s\d)""",
                               re.VERBOSE)

CIG_RE = re.compile(r"""CIG\s+(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)""",
                              re.VERBOSE)

VIS_RE = re.compile(r"""VIS\s+(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s(\s*\d+)\s
                              (\s*\d+)""",
                              re.VERBOSE)

OBV_RE = re.compile(r"""OBV\s+(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s
                              (\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s
                              (\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s
                              (\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s(\s*\w+)\s
                              (\s*\w+)""",
                              re.VERBOSE)

# Translation of the category codes

CEILING_HEIGHT = {"1" : "< 200 feet", 
                  "2" : "200 - 400 feet",
                  "3" : "500 - 900 feet",
                  "4" : "1,000 - 1,900 feet",
                  "5" : "2,000 - 3,000 feet",
                  "6" : "3,100 - 6,500 feet",
                  "7" : "6,600 - 12,000 feet",
                  "8" : "> 12,000 feet or unlimited ceiling"}

VISIBILITY = {"1" : "< 1/2 miles", 
              "2" : "1/2 - < 1 miles",
              "3" : "1 - < 2 miles",
              "4" : "2 - < 3 miles",
              "5" : "3 - 5 miles",
              "6" : "6 miles",
              "7" : "> 6 miles"}

QPF = {"0" : "no precipitation", 
       "1" : "0.01 to 0.09 inches",
       "2" : "0.10 to 0.24 inches",
       "3" : "0.25 to 0.49 inches",
       "4" : "0.50 to 0.99 inches",
       "5" : "1.00 to 1.99 inches",
       "6" : "2.00 inches or greater"}
 
SKY_COVER = {"CL" : "clear", 
             "FW" : "few > 0 to 2 octas",
             "SC" : "scattered > 2 to 4 octas",
             "BK" : "broken > 4 to < 8 octas",
             "OV" : "overcast"}

OBSTRUCTION_TO_VISION = {"N"  : "none", 
                         "HZ" : "haze, smoke, dust",
                         "BR" : "mist (fog with visibility >= 5/8 mile)",
                         "FG" : "fog or ground fog (visibility < 5/8 mile)",
                         "BL" : "blowing dust, sand, snow"}

SNOWFALL_AMOUNT = {"0" : "no snow or a trace expected", 
                   "1" : "> a trace to < 2 inches",
                   "2" : "2 to < 4 inches",
                   "4" : "4 to 6 inches",
                   "6" : "6 to < 8 inches",
                   "8" : ">= 8 inches"}

PRECIPITATION_TYPE = {"S" : "pure snow or snow grains", 
                      "Z" : "freezing rain/drizzle, ice pellets, or anything mixed with freezing precip",
                      "R" : "pure rain/drizzle or rain mixed with snow"}

POSITION_TO_HOUR = {1 : 0, 4 : 1, 7 : 2, 10 : 3, 13 : 4, 16 : 5, 19 : 6, 22 : 7, 
                    25 : 8, 28 : 9, 31 : 10, 34 : 11, 37 : 12, 40 : 13, 43 : 14,
                    46 : 15, 49 : 16, 52 : 17, 55 : 18, 58 : 19, 61 : 20}

# MAV report objects

debug = False

class Mav(object):
    """MAV (short range weather report)"""
  
    def __init__(self, message):
        """Parse the raw MAV message"""
        self.station_id = None  # 4-character ICAO station code
        self.time = None        # Timestamp of report
        self.high = None        # High temperature
        self.low = None         # Low temperature
        self.dt = []            # Day of the month, denoted by the standard three or four letter abbreviation
        self.hr = []            # Hour of the day in UTC 
        self.nx = []            # Nighttime minimum/daytime maximum surface temperatures 
        self.tmp = []           # Surface temperature valid at that hour 
        self.dpt = []           # Surface dew point valid at that hour 
        self.cld = []           # Forecast categories of total sky cover valid at that hour 
        self.wdr = []           # Forecasts of the 10-meter wind direction at the hour, given in tens of degrees 
        self.wsp = []           # Forecasts of the 10-meter wind speed at the hour, given in knots 
        self.p06 = []           # Probability of precipitation (PoP) during a 6-h period ending at that time 
        self.p12 = []           # PoP during a 12-h period ending at that time 
        self.q06 = []           # Quantitative precipitation forecast (QPF) during a 6-h period ending at that time 
        self.q12 = []           # QPF during a 12-h period ending at that time 
        self.t06 = []           # Probability of thunderstorms/conditional probability of severe thunderstorms for the 6-hr 
        self.t12 = []           # Probability of thunderstorms/conditional probability of severe thunderstorms for the 12-hr period 
        self.poz = []           # Conditional probability of freezing pcp occurring at the hour 
        self.pos = []           # Conditional probability of snow occurring at the hour 
        self.typ = []           # Conditional precipitation type at the hour 
        self.snw = []           # Snowfall categorical forecasts during a 24-h period ending at the indicated time 
        self.cig = []           # Ceiling height categorical forecasts at the hour 
        self.vis = []           # Visibility categorical forecasts at the hour 
        self.obv = []           # Obstruction to vision categorical forecasts at the hour 

        try:
            m = STATION_RE.search(message)
            if m:
                self._handle_station(m)
            m = TIMESTAMP_RE.search(message)
            if m:
                self._handle_timestamp(m)
            m = DT_RE.search(message)
            if m:
                self._handle_dt(m)
            m = HR_RE.search(message)
            if m:
                self._handle_hr(m)
            m = NX_RE.search(message)
            if m:
                self._handle_nx(m)
            m = TMP_RE.search(message)
            if m:
                self._handle_tmp(m)
            m = DPT_RE.search(message)
            if m:
                self._handle_dpt(m)
            m = CLD_RE.search(message)
            if m:
                self._handle_cld(m)
            m = WDR_RE.search(message)
            if m:
                self._handle_wdr(m)
            m = WSP_RE.search(message)
            if m:
                self._handle_wsp(m)
            m = P06_RE.search(message)
            if m:
                self._handle_p06(m)
            m = P12_RE.search(message)
            if m:
                self._handle_p12(m)
            m = Q06_RE.search(message)
            if m:
                self._handle_q06(m)
            m = Q12_RE.search(message)
            if m:
                self._handle_q12(m)
            m = T06_RE.search(message)
            if m:
                self._handle_t06(m)
            m = T12_RE.search(message)
            if m:
                self._handle_t12(m)
            m = POZ_RE.search(message)
            if m:
                self._handle_poz(m)
            m = POS_RE.search(message)
            if m:
                self._handle_pos(m)
            m = TYP_RE.search(message)
            if m:
                self._handle_typ(m)
            m = SNW_RE.search(message)
            if m:
                self._handle_snw(m)
            m = CIG_RE.search(message)
            if m:
                self._handle_cig(m)
            m = VIS_RE.search(message)
            if m:
                self._handle_vis(m)
            m = OBV_RE.search(message)
            if m:
                self._handle_obv(m)

        except Exception, err:
            raise err

    def __str__(self):
        return self.string()

    def _handle_station(self, m):
        """
        Extract the station ID.
        """
        self.station_id = m.group(1)

    def _handle_timestamp(self, m):
        """
        Extract the message timestamp.
        """
        self.time = m.group(1)
    
    def _handle_dt(self, m):
        """
        Extract the date fields.
        """
        self.dt = re.findall('\w+\s+\d+', m.group(1))

    def _handle_hr(self, m):
        """
        Extract the hour of the day fields.
        """
        for i in range(1,MAX_COLS):
            self.hr.append(m.group(i))

    def _handle_nx(self, m):
        """
        Extract the minimum/maximum surface temperature fields.
        """
        for i in range(1,MAX_COLS):
            self.nx.append(m.group(i).strip())

    def _handle_tmp(self, m):
        """
        Extract the surface temperature fields.
        """
        for i in range(1,MAX_COLS):
            self.tmp.append(m.group(i).strip())

    def _handle_dpt(self, m):
        """
        Extract the surface dew point fields.
        """
        for i in range(1,MAX_COLS):
            self.dpt.append(m.group(i))

    def _handle_cld(self, m):
        """
        Extract the sky cover fields.
        """
        for i in range(1,MAX_COLS):
            self.cld.append(m.group(i))

    def _handle_wdr(self, m):
        """
        Extract the wind direction fields.
        """
        for i in range(1,MAX_COLS):
            self.wdr.append(m.group(i))

    def _handle_wsp(self, m):
        """
        Extract the wind speed fields.
        """
        for i in range(1,MAX_COLS):
            self.wsp.append(m.group(i))

    def _handle_p06(self, m):
        """
        Extract the 6-h PoP fields.
        """
        for i in range(1,MAX_COLS):
            self.p06.append(m.group(i).strip())
    
    def _handle_p12(self, m):
        """
        Extract the 12-h PoP fields.
        """
        for i in range(1,MAX_COLS):
            self.p12.append(m.group(i).strip())

    def _handle_q06(self, m):
        """
        Extract the 6-h QPF fields.
        """
        for i in range(1,MAX_COLS):
            self.q06.append(m.group(i).strip())
    
    def _handle_q12(self, m):
        """
        Extract the 12-h QPF fields.
        """
        for i in range(1,MAX_COLS):
            self.q12.append(m.group(i).strip())

    def _handle_t06(self, m):
        """
        Extract the 6-h thunderstorm probability fields.
        """
        for i in range(1,MAX_COLS):
            self.t06.append('')
        for m in re.finditer(r"(\d+/)(\s\d|\d\d)", m.group(1)):
            self.t06[POSITION_TO_HOUR[m.end()-1]] = m.group(1) + m.group(2).strip()

    def _handle_t12(self, m):
        """
        Extract the 12-h thunderstorm probability fields.
        """
        for i in range(1,MAX_COLS):
            self.t12.append('')
        for m in re.finditer(r"(\d+/)(\s\d|\d\d)", m.group(1)):
            self.t12[POSITION_TO_HOUR[m.end()-1]] = m.group(1) + m.group(2).strip()
    
    def _handle_poz(self, m):
        """
        Extract the conditional probability of freezing pcp fields.
        """
        for i in range(1,MAX_COLS):
            self.poz.append(m.group(i).strip())

    def _handle_pos(self, m):
        """
        Extract the conditional probability of snow fields.
        """
        for i in range(1,MAX_COLS):
            self.pos.append(m.group(i).strip())

    def _handle_typ(self, m):
        """
        Extract the precipitation type fields.
        """
        for i in range(1,MAX_COLS):
            self.typ.append(m.group(i).strip())

    def _handle_snw(self, m):
        """
        Extract the snowfall fields.
        """
        for i in range(1,MAX_COLS):
            self.snw.append(m.group(i).strip())

    def _handle_cig(self, m):
        """
        Extract the ceiling height fields.
        """
        for i in range(1,MAX_COLS):
            self.cig.append(m.group(i).strip())

    def _handle_vis(self, m):
        """
        Extract the visibility fields.
        """
        for i in range(1,MAX_COLS):
            self.vis.append(m.group(i).strip())

    def _handle_obv(self, m):
        """
        Extract the obstruction to view fields.
        """
        for i in range(1,MAX_COLS):
            self.obv.append(m.group(i).strip())

    def string(self):
        """
        Return a human readable version of the decoded message.
        """
        lines = []
        if self.station_id:
            lines.append("station: %s" % self.station_id)
        if self.time:
            lines.append("timestamp: %s" % self.time)
        ndays = len(self.dt)
        nhours = len(self.hr)
        h = 0
        for d in range(ndays):
            lines.append("date: %s" % self.dt[d])
            while 1:
                lines.append("\thour: %s" % self.hr[h])
                self.attach_string_fields(lines, h)
                h += 1
                if h == nhours or self.hr[h] == '00':
                    break

        return string.join(lines,"\n")
    
    def attach_string_fields(self, lines, h):
        if len(self.nx) and self.nx[h] != '':
            lines.append("\t\tnighttime minimum/daytime maximum surface temperature: %s F" % self.nx[h])
        lines.append("\t\tsurface temperature: %s F" % self.tmp[h])
        lines.append("\t\tsurface dew point: %s F" % self.dpt[h])
        lines.append("\t\tsky cover: %s" % SKY_COVER[self.cld[h]])               
        lines.append("\t\twind direction: %s degrees" % self.wdr[h])
        lines.append("\t\twind speed: %s knots" % self.wsp[h])
        if len(self.p06) and self.p06[h] != '':
            lines.append("\t\t6 hour PoP: %s%%" % self.p06[h])
        if len(self.p12) and self.p12[h] != '':
            lines.append("\t\t12 hour PoP: %s%%" % self.p12[h])
        if len(self.q06) and self.q06[h] != '':
            lines.append("\t\t6 hour QPF: %s" % QPF[self.q06[h]])
        if len(self.q12) and self.q12[h] != '':
            lines.append("\t\t12 hour QPF: %s" % QPF[self.q12[h]])
        if len(self.t06) and self.t06[h] != '':
            p = self.t06[h].split('/')
            lines.append("\t\t6 hour probability of thunderstorms: %s%%" % p[0])
            lines.append("\t\t6 hour probability of severe thunderstorms: %s%%" % p[1])
        if len(self.t12) and self.t12[h] != '':
            p = self.t12[h].split('/')
            lines.append("\t\t12 hour probability of thunderstorms: %s%%" % p[0])
            lines.append("\t\t12 hour probability of severe thunderstorms: %s%%" % p[1])
        if len(self.poz) and self.poz[h] != '':
            lines.append("\t\tconditional probability of freezing pcp: %s%%" % self.poz[h])
        if len(self.pos) and self.pos[h] != '':
            lines.append("\t\tconditional probability of snow: %s%%" % self.pos[h])
        lines.append("\t\tprecipitation type: %s" % PRECIPITATION_TYPE[self.typ[h]])
        if len(self.snw) and self.snw[h] != '':
            lines.append("\t\tsnowfall: %s" % SNOWFALL_AMOUNT[self.snw[h]])
        lines.append("\t\tceiling height: %s" % CEILING_HEIGHT[self.cig[h]])
        lines.append("\t\tvisibility: %s" % VISIBILITY[self.vis[h]])
        lines.append("\t\tobstruction to vision: %s" % OBSTRUCTION_TO_VISION[self.obv[h]])

    @staticmethod
    def createEmptyPeriodArray():
        periods = [None] * (MAX_COLS - 1)
        for i in range(MAX_COLS - 1):
            periods[i] = {'date' : 'None',
                          'hour' : 'None',
                          'temp' : 'None',
                          'dewpoint' : 'None',
                          'cover' : 'None',
                          'wind' : {'direction' : 'None',
                                     'speed' : 'None',
                                     'gust' : 'None'},
                          'pop6' : 'None',
                          'pop12' : 'None',
                          'qpf12' : 'None',
                          'thund6' : 'None',
                          'thund12' : 'None',
                          'popz' : 'None',
                          'pops' : 'None',
                          'type' : 'None',
                          'snow' : 'None',
                          'visibility' : 'None',
                          'obscurity' : 'None',
                          'ceiling' : 'None'}
        return periods

    def json(self):
        """
        Return a JSON version of the decoded message.
        """
        periodArray = Mav.createEmptyPeriodArray()
        report = {'wxid' : self.station_id,
                  'time' : 'None',
                  'high' : 'None',
                  'low' : 'None',
                  'periods' : periodArray}

        # populate report with weather data

        # time
        if self.time:
            report['time'] = self.time
        # high
        if self.high:
            report['high'] = self.high
        # low
        if self.low:
            report['low'] = self.low
        # loop over periods
        date_index = 0
        prev_hr = 0

        for i in range(MAX_COLS - 1):
            # hour
            if len(self.hr) > i and self.hr[i] != '':
                report['periods'][i]['hour'] = self.hr[i]
                # check if we need to increment the date index
                if i > 0 and int(prev_hr) > int(self.hr[i]): 
                    date_index += 1 # inc the date index
                # save prev_hr for next comparison
                prev_hr = self.hr[i]

            # date
            if len(self.dt) > date_index:
                report['periods'][i]['date'] = self.dt[date_index]
            # temp
            if len(self.tmp) > i and self.tmp[i] != '':
                report['periods'][i]['temp'] = self.tmp[i]
            # dewpoint
            if len(self.dpt) > i and self.dpt[i] != '':
                report['periods'][i]['dewpoint'] = self.dpt[i]
            # cover
            if len(self.cld) > i and self.cld[i] != '':
                report['periods'][i]['cover'] = SKY_COVER[self.cld[i]]
            # wind direction 
            if len(self.wdr) > i and self.wdr[i] != '':
                report['periods'][i]['wind']['direction'] = self.wdr[i]
            # wind speed
            if len(self.wsp) > i and self.wsp[i] != '':
                report['periods'][i]['wind']['speed'] = self.wsp[i]
            # pop6
            if len(self.p06) > i and self.p06[i] != '':
                report['periods'][i]['pop6'] = self.p06[i]            
            # pop12 
            if len(self.p12) > i and self.p12[i] != '':
                report['periods'][i]['pop12'] = self.p12[i]   
            # qpf12
            if len(self.q12) > i and self.q12[i] != '':
                report['periods'][i]['qpf12'] = QPF[self.q12[i]]
            # thund6
            if len(self.t06) > i and self.t06[i] != '':
                report['periods'][i]['thund6'] = self.t06[i]            
            # thund12
            if len(self.t12) > i and self.t12[i] != '':
                report['periods'][i]['thund12'] = self.t12[i]            
            # popz
            if len(self.poz) > i and self.poz[i] != '':
                report['periods'][i]['popz'] = self.poz[i]   
            # pops
            if len(self.pos) > i and self.pos[i] != '':
                report['periods'][i]['pops'] = self.pos[i]   
            # type
            if len(self.typ) > i and self.typ[i] != '':
                report['periods'][i]['type'] = PRECIPITATION_TYPE[self.typ[i]]
            # snow
            if len(self.snw) > i and self.snw[i] != '':
                report['periods'][i]['snow'] = SNOWFALL_AMOUNT[self.snw[i]]
            # visibility
            if len(self.vis) > i and self.vis[i] != '':
                report['periods'][i]['visibility'] = VISIBILITY[self.vis[i]] 
            # obscurity
            if len(self.obv) > i and self.obv[i] != '':
                report['periods'][i]['obscurity'] = OBSTRUCTION_TO_VISION[self.obv[i]]   
            # ceiling
            if len(self.cig) > i and self.cig[i] != '':
                report['periods'][i]['ceiling'] = CEILING_HEIGHT[self.cig[i]]
            
        #pprint.pprint(report)
        return json.dumps({'mav': report}) 
