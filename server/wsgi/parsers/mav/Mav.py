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
import pprint

MAX_COLS = 22  # Number of columns in a MAV message

# Exceptions

class ParserError(Exception):
    """Exception raised when an unparseable group is found in body of the 
       message."""
    pass

# Regular expressions to decode various groups of the MAV message 

DT_RE = re.compile(r'DT\s+(.+)')

HR_RE = re.compile(r"""HR\s+(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s(\d+)\s
                            (\d+)\s(\d+)\s(\d+)""",
                            re.VERBOSE)

NX_RE = re.compile(r"""[N|X]/[X|N]\s\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)\s
                                      (\s\s|-?\d*)\s(\s\s|-?\d*)\s(\s\s|-?\d*)""",
                                      re.VERBOSE)

TMP_RE = re.compile(r"""TMP\s+(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)""",
                              re.VERBOSE)

DPT_RE = re.compile(r"""DPT\s+(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s(\s*-?\d+)\s
                              (\s*-?\d+)""",
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




# Translation of the cloud category codes

CLOUD = {"CL" : "clear", 
         "FW" : "few > 0 to 2 octas",
         "SC" : "scattered > 2 to 4 octas",
         "BK" : "broken > 4 to < 8 octas",
         "OV" : "overcast"}
                          
# MAV report objects

debug = False

class Mav(object):
    """MAV (short range weather report)"""
  
    def __init__(self, message):
        """Parse the raw MAV message"""
        self.station_id = None  # 4-character ICAO station code
        self.dt = []            # Day of the month, denoted by the standard three or four letter abreviation
        self.hr = []            # Hour of the day in UTC 
        self.nx = []            # Nighttime minimum/daytime maxium surface temperatures 
        self.tmp = []           # Surface temperature valid at that hour 
        self.dpt = []           # Surface dewpoint valid at that hour 
        self.cld = []           # Forecast cataegories of total sky cover valid at that hour 
        self.wdr = []           # Forecasts of the 10-meter wind direction at the hour, given in tens of degrees 
        self.wsp = []           # Forecasts of the 10-meter wind speed at the hour, given in knots 

        try:
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

           
        except Exception, err:
            #raise ParserError(handler.__name__+" failed while processing '"+code+"'\n"+string.join(err.args))
            raise err

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
                                    'gust' : 'None',
                                    },
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
                          'ceiling' : 'None'
                          }
        return periods

    def json(self):

        periodArray = Mav.createEmptyPeriodArray()
        report = {'wxid' : self.station_id,
                  'time' : 'None',
                  'high' : 'None',
                  'low' : 'None',
                  'periods' : periodArray
                  }

        #pprint.pprint(report)
        return json.dumps({'mav': report}) 



    def _handle_dt(self, m):
        """
        Extract the date fields.
        """
        self.dt = re.findall('\w+\s+\d+', m.group(1))
        print self.dt

    def _handle_hr(self, m):
        """
        Extract the hour of the day fields.
        """
        for i in range(1,MAX_COLS):
            self.hr.append(m.group(i))
        print self.hr

    def _handle_nx(self, m):
        """
        Extract the minimum/maxium surface temperature fields.
        """
        for i in range(1,MAX_COLS):
            self.nx.append(m.group(i))
        print self.nx

    def _handle_tmp(self, m):
        """
        Extract the surface temperature fields.
        """
        for i in range(1,MAX_COLS):
            self.tmp.append(m.group(i))
        print self.tmp

    def _handle_dpt(self, m):
        """
        Extract the surface dewpoint fields.
        """
        for i in range(1,MAX_COLS):
            self.dpt.append(m.group(i))
        print self.dpt

    def _handle_cld(self, m):
        """
        Extract the sky cover fields.
        """
        for i in range(1,MAX_COLS):
            self.cld.append(m.group(i))
        print self.cld

    def _handle_wdr(self, m):
        """
        Extract the wind direction fields.
        """
        for i in range(1,MAX_COLS):
            self.wdr.append(m.group(i))
        print self.wdr

    def _handle_wsp(self, m):
        """
        Extract the wind speed fields.
        """
        for i in range(1,MAX_COLS):
            self.wsp.append(m.group(i))
        print self.wsp
