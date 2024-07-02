package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.os.Parcel;
import android.os.Parcelable;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

public class WeatherCondition {
    private static final boolean DBG = false;
    private static final boolean NEED_PROXY = false;
    private static final String TAG = "AccuWeatherCondition";
    private static final boolean XML_DBG = false;
    private static String cityDataSvr = "http://motor.accu-weather.com/widget/motor/city-find.asp";
    private static String weatherDataSvr = "http://motor.accu-weather.com/widget/motor/weather-data.asp";

    private class AccuweatherCityHandler extends DefaultHandler {
        private ArrayList<CityInfo> mCityList;

        private AccuweatherCityHandler() {
            this.mCityList = null;
        }

        public ArrayList<CityInfo> getCityList() {
            return this.mCityList;
        }

        public void startDocument() throws SAXException {
            this.mCityList = new ArrayList();
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (localName.equals("location")) {
                CityInfo city = new CityInfo();
                city.mCity = atts.getValue("city");
                city.mState = atts.getValue("state");
                city.mcityCode = atts.getValue("location");
                this.mCityList.add(city);
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        }

        public void characters(char[] ch, int start, int length) {
        }
    }

    private class AccuweatherWeatherHandler extends DefaultHandler {
        private CityInfo mCityInfo;
        private CurrentCondition mCurrentCondition;
        private Integer mDayNumber;
        private ArrayList<ForcastCondition> mForcastConditions;
        private GeoInfo mGeoInfo;
        private boolean mInCity;
        private boolean mInCurCond;
        private boolean mInDate;
        private boolean mInDayTime;
        private boolean mInForcast;
        private boolean mInHighTemp;
        private boolean mInLat;
        private boolean mInLocal;
        private boolean mInLon;
        private boolean mInLowTemp;
        private boolean mInState;
        private boolean mInSunrise;
        private boolean mInSunset;
        private boolean mInTemp;
        private boolean mInTmZone;
        private boolean mInTxtShort;
        private boolean mInURL;
        private boolean mInWeaIcon;
        private boolean mInWeaText;
        private boolean mInWeek;

        private AccuweatherWeatherHandler() {
            this.mCurrentCondition = null;
            this.mForcastConditions = null;
            this.mGeoInfo = null;
            this.mCityInfo = null;
            this.mInLocal = false;
            this.mInCity = false;
            this.mInState = false;
            this.mInLat = false;
            this.mInLon = false;
            this.mInTmZone = false;
            this.mInTemp = false;
            this.mInLowTemp = false;
            this.mInHighTemp = false;
            this.mInWeaText = false;
            this.mInWeaIcon = false;
            this.mInURL = false;
            this.mInCurCond = false;
            this.mInForcast = false;
            this.mInDayTime = false;
            this.mInDate = false;
            this.mInWeek = false;
            this.mInSunrise = false;
            this.mInSunset = false;
            this.mInTxtShort = false;
            this.mDayNumber = Integer.valueOf(0);
        }

        public CityInfo getCityInfo() {
            return this.mCityInfo;
        }

        public GeoInfo getGeoInfo() {
            return this.mGeoInfo;
        }

        public CurrentCondition getCurrentCondition() {
            return this.mCurrentCondition;
        }

        public ArrayList<ForcastCondition> getForcastConditions() {
            return this.mForcastConditions;
        }

        public void startDocument() throws SAXException {
            this.mCityInfo = new CityInfo();
            this.mGeoInfo = new GeoInfo();
            this.mCurrentCondition = new CurrentCondition();
            this.mForcastConditions = new ArrayList(4);
            for (int i = 0; i < 4; i++) {
                this.mForcastConditions.add(new ForcastCondition());
            }
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (localName.equals("local")) {
                this.mInLocal = true;
            } else if (localName.equals("lat")) {
                this.mInLat = true;
            } else if (localName.equals("lon")) {
                this.mInLon = true;
            } else if (localName.equals("city")) {
                this.mInCity = true;
            } else if (localName.equals("state")) {
                this.mInState = true;
            } else if (localName.equals("timeZone")) {
                this.mInTmZone = true;
            } else if (localName.equals("currentconditions")) {
                this.mInCurCond = true;
            } else if (localName.equals("forecast")) {
                this.mInForcast = true;
            } else if (localName.equals("url")) {
                this.mInURL = true;
            } else if (localName.equals("temperature")) {
                this.mInTemp = true;
            } else if (localName.equals("hightemperature")) {
                this.mInHighTemp = true;
            } else if (localName.equals("lowtemperature")) {
                this.mInLowTemp = true;
            } else if (localName.equals("weathertext")) {
                this.mInWeaText = true;
            } else if (localName.equals("weathericon")) {
                this.mInWeaIcon = true;
            } else if (localName.equals("day")) {
                this.mDayNumber = Integer.valueOf(Integer.parseInt(atts.getValue("number")));
            } else if (localName.equals("obsdate")) {
                this.mInDate = true;
            } else if (localName.equals("daycode")) {
                this.mInWeek = true;
            } else if (localName.equals("sunrise")) {
                this.mInSunrise = true;
            } else if (localName.equals("sunset")) {
                this.mInSunset = true;
            } else if (localName.equals("daytime")) {
                this.mInDayTime = true;
            } else if (localName.equals("txtshort")) {
                this.mInTxtShort = true;
            } else if (!localName.equals("txtlong")) {
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("local")) {
                this.mInLocal = false;
            } else if (localName.equals("lat")) {
                this.mInLat = false;
            } else if (localName.equals("lon")) {
                this.mInLon = false;
            } else if (localName.equals("city")) {
                this.mInCity = false;
            } else if (localName.equals("state")) {
                this.mInState = false;
            } else if (localName.equals("timeZone")) {
                this.mInTmZone = false;
            } else if (localName.equals("currentconditions")) {
                this.mInCurCond = false;
            } else if (localName.equals("forecast")) {
                this.mInForcast = false;
            } else if (localName.equals("url")) {
                this.mInURL = false;
            } else if (localName.equals("temperature")) {
                this.mInTemp = false;
            } else if (localName.equals("hightemperature")) {
                this.mInHighTemp = false;
            } else if (localName.equals("lowtemperature")) {
                this.mInLowTemp = false;
            } else if (localName.equals("weathertext")) {
                this.mInWeaText = false;
            } else if (localName.equals("weathericon")) {
                this.mInWeaIcon = false;
            } else if (localName.equals("obsdate")) {
                this.mInDate = false;
            } else if (localName.equals("daycode")) {
                this.mInWeek = false;
            } else if (localName.equals("sunrise")) {
                this.mInSunrise = false;
            } else if (localName.equals("sunset")) {
                this.mInSunset = false;
            } else if (localName.equals("daytime")) {
                this.mInDayTime = false;
            } else if (localName.equals("txtshort")) {
                this.mInTxtShort = false;
            } else if (!localName.equals("txtlong")) {
            }
        }

        public void characters(char[] ch, int start, int length) {
            if (this.mInLocal) {
                if (this.mInCity) {
                    this.mCityInfo.mCity = new String(ch).substring(start, start + length);
                } else if (this.mInState) {
                    this.mCityInfo.mState = new String(ch).substring(start, start + length);
                } else if (this.mInLat) {
                    this.mGeoInfo.mLatitude = new String(ch).substring(start, start + length);
                } else if (this.mInLon) {
                    this.mGeoInfo.mLongitude = new String(ch).substring(start, start + length);
                } else if (this.mInTmZone) {
                    this.mGeoInfo.mTimeZone = new String(ch).substring(start, start + length);
                }
            }
            String tmpStr;
            if (this.mInCurCond) {
                if (this.mInTemp) {
                    this.mCurrentCondition.temp = Integer.valueOf(Integer.parseInt(new String(ch).substring(start, start + length)));
                } else if (this.mInWeaText) {
                    this.mCurrentCondition.conditionTxt = new String(ch).substring(start, start + length);
                } else if (this.mInURL) {
                    tmpStr = new String(ch).substring(start, start + length);
                    if (this.mCurrentCondition.url != null) {
                        this.mCurrentCondition.url += tmpStr;
                        return;
                    }
                    this.mCurrentCondition.url = tmpStr;
                } else if (this.mInWeaIcon) {
                    this.mCurrentCondition.type = Integer.valueOf(Integer.parseInt(new String(ch).substring(start, start + length)));
                }
            } else if (!this.mInForcast) {
            } else {
                if (this.mInDate) {
                    ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).date = new String(ch).substring(start, start + length);
                    ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).dayNumber = this.mDayNumber;
                } else if (this.mInWeek) {
                    ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).week = new String(ch).substring(start, start + length);
                } else if (this.mInSunrise) {
                    ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).sunrise = new String(ch).substring(start, start + length);
                } else if (this.mInSunset) {
                    ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).sunset = new String(ch).substring(start, start + length);
                } else if (this.mInURL) {
                    tmpStr = new String(ch).substring(start, start + length);
                    if (this.mDayNumber.intValue() == 0) {
                        if (this.mForcastConditions.get(0) != null) {
                            ((ForcastCondition) this.mForcastConditions.get(0)).url_general = tmpStr;
                        }
                        if (this.mForcastConditions.get(1) != null) {
                            ((ForcastCondition) this.mForcastConditions.get(1)).url_general = tmpStr;
                        }
                        if (this.mForcastConditions.get(2) != null) {
                            ((ForcastCondition) this.mForcastConditions.get(2)).url_general = tmpStr;
                        }
                        if (this.mForcastConditions.get(3) != null) {
                            ((ForcastCondition) this.mForcastConditions.get(3)).url_general = tmpStr;
                        }
                    } else if (this.mForcastConditions.get(this.mDayNumber.intValue() - 1) == null) {
                    } else {
                        if (((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).url != null) {
                            ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).url += tmpStr;
                            return;
                        }
                        ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).url = tmpStr;
                    }
                } else if (!this.mInDayTime) {
                } else {
                    if (this.mInHighTemp) {
                        ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).tempHigh = Integer.valueOf(Integer.parseInt(new String(ch).substring(start, start + length)));
                    } else if (this.mInLowTemp) {
                        ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).tempLow = Integer.valueOf(Integer.parseInt(new String(ch).substring(start, start + length)));
                    } else if (this.mInTxtShort) {
                        ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).conditionTxt = new String(ch).substring(start, start + length);
                    } else if (this.mInWeaIcon) {
                        ((ForcastCondition) this.mForcastConditions.get(this.mDayNumber.intValue() - 1)).type = Integer.valueOf(Integer.parseInt(new String(ch).substring(start, start + length)));
                    }
                }
            }
        }
    }

    public static class CityInfo implements Parcelable {
        public static final Creator<CityInfo> CREATOR = new Creator<CityInfo>() {
            public CityInfo createFromParcel(Parcel in) {
                return new CityInfo(in);
            }

            public CityInfo[] newArray(int size) {
                return new CityInfo[size];
            }
        };
        public String mCity;
        public double mLatitude;
        public double mLongitude;
        public String mState;
        public String mcityCode;

        public CityInfo() {
        }

        private CityInfo(Parcel in) {
            this.mCity = in.readString();
            this.mState = in.readString();
            this.mcityCode = in.readString();
            this.mLatitude = in.readDouble();
            this.mLongitude = in.readDouble();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(this.mCity);
            if (!(this.mState == null || this.mState.isEmpty())) {
                sb.append(",");
                sb.append(this.mState);
            }
            return sb.toString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mCity);
            dest.writeString(this.mState);
            dest.writeString(this.mcityCode);
            dest.writeDouble(this.mLatitude);
            dest.writeDouble(this.mLongitude);
        }
    }

    private static class CurrentCondition {
        public String conditionTxt;
        public Integer temp;
        public Integer type;
        public String url;

        private CurrentCondition() {
            this.temp = null;
            this.conditionTxt = null;
            this.url = null;
            this.type = null;
        }
    }

    public static class ForcastCondition {
        public String conditionTxt = null;
        public String date = null;
        public Integer dayNumber = Integer.valueOf(0);
        public String sunrise = null;
        public String sunset = null;
        public Integer tempHigh = null;
        public Integer tempLow = null;
        public Integer type = null;
        public String url = null;
        public String url_general = null;
        public String week = null;
    }

    public static class GeoInfo {
        public String mLatitude;
        public String mLongitude;
        public String mTimeZone;
    }

    public static class WeatherResult {
        public String city = null;
        public String conditionTxt = null;
        public ArrayList<ForcastCondition> forcastList = null;
        public String state = null;
        public Integer tempCurrent = null;
        public Integer tempHigh = null;
        public Integer tempLow = null;
        public String timeZone = null;
        public Integer type = null;
        public String url = null;
    }

    public Integer getWeather(String cityCode, Integer unit, WeatherResult weather) {
        String queryString = weatherDataSvr + "?location=" + cityCode.replace(" ", "%20") + "&metric=" + unit;
        try {
            setProxy();
            URL url = new URL(queryString);
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            AccuweatherWeatherHandler weatherHandler = new AccuweatherWeatherHandler();
            xr.setContentHandler(weatherHandler);
            xr.parse(new InputSource(url.openStream()));
            CurrentCondition cCondition = weatherHandler.getCurrentCondition();
            ArrayList<ForcastCondition> fConditions = null;
            if (cCondition != null) {
                weather.conditionTxt = cCondition.conditionTxt;
                weather.tempCurrent = cCondition.temp;
                weather.url = cCondition.url;
                weather.type = cCondition.type;
            }
            if (fConditions != null) {
                weather.tempHigh = ((ForcastCondition) fConditions.get(0)).tempHigh;
                weather.tempLow = ((ForcastCondition) fConditions.get(0)).tempLow;
                weather.url = ((ForcastCondition) fConditions.get(0)).url_general;
                weather.forcastList = fConditions;
            }
            CityInfo cityInfo = weatherHandler.getCityInfo();
            if (cityInfo != null) {
                weather.city = cityInfo.mCity;
                weather.state = cityInfo.mState;
            }
            GeoInfo geoInfo = weatherHandler.getGeoInfo();
            if (geoInfo != null) {
                weather.timeZone = geoInfo.mTimeZone;
            }
            return Integer.valueOf(0);
        } catch (Exception e) {
            return Integer.valueOf(-1);
        }
    }

    public Integer getWeather(double latitude, double longitude, Integer unit, WeatherResult weather) {
        String queryString = weatherDataSvr + "?slat=" + latitude + "&slon=" + longitude + "&metric=" + unit;
        try {
            setProxy();
            URL url = new URL(queryString);
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            AccuweatherWeatherHandler weatherHandler = new AccuweatherWeatherHandler();
            xr.setContentHandler(weatherHandler);
            xr.parse(new InputSource(url.openStream()));
            CurrentCondition cCondition = weatherHandler.getCurrentCondition();
            ArrayList<ForcastCondition> fConditions = null;
            CityInfo cityInfo = weatherHandler.getCityInfo();
            GeoInfo geoInfo = weatherHandler.getGeoInfo();
            if (cCondition != null) {
                weather.conditionTxt = cCondition.conditionTxt;
                weather.tempCurrent = cCondition.temp;
                weather.type = cCondition.type;
            }
            if (fConditions != null) {
                weather.tempHigh = ((ForcastCondition) fConditions.get(0)).tempHigh;
                weather.tempLow = ((ForcastCondition) fConditions.get(0)).tempLow;
                weather.url = ((ForcastCondition) fConditions.get(0)).url_general;
                weather.forcastList = fConditions;
            }
            if (cityInfo != null) {
                weather.city = cityInfo.mCity;
                weather.state = cityInfo.mState;
            }
            if (geoInfo != null) {
                weather.timeZone = geoInfo.mTimeZone;
            }
            return Integer.valueOf(0);
        } catch (Exception e) {
            return Integer.valueOf(-1);
        }
    }

    public ArrayList<CityInfo> SearchCity(String searchString) {
        ArrayList<CityInfo> arrayList = null;
        String queryString = cityDataSvr + "?location=" + searchString.replace(" ", "%20");
        try {
            setProxy();
            URL url = new URL(queryString);
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            AccuweatherCityHandler cityHandler = new AccuweatherCityHandler();
            xr.setContentHandler(cityHandler);
            xr.parse(new InputSource(url.openStream()));
            arrayList = cityHandler.getCityList();
        } catch (Exception e) {
        }
        return arrayList;
    }

    private void setProxy() {
    }
}
