package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.SAXParserFactory;

public class GeoInfoManager {
    private static final String TAG = "GeoInfoManager";
    private static final boolean XML_DBG = true;
    private static String cityLocSvr = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=true&address=";
    Context mContext;
    Geocoder mGeocoder = new Geocoder(this.mContext, Locale.getDefault());

    public static class CityAsyncTask extends AsyncTask<String, String, String> {
        Context mContext;

        public CityAsyncTask(Context ctx) {
            this.mContext = ctx;
        }

        protected String doInBackground(String... params) {
            String name = null;
            double[] cords = GeoInfoManager.getLastGPS(this.mContext);
            try {
                List<Address> addresses = new Geocoder(this.mContext, Locale.getDefault()).getFromLocation(cords[0], cords[1], 1);
                Log.i("Addresses", "-->" + addresses);
                name = ((Address) addresses.get(0)).toString();
            } catch (IOException ex) {
                Log.e(GeoInfoManager.TAG, "retrive city resId failed: " + ex);
            }
            return name;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private static class GoogleCityInfoHandler extends DefaultHandler {
        private boolean isLat = false;
        private boolean isLng = false;
        private boolean isLocation = false;
        private double[] mResult = null;

        public GoogleCityInfoHandler(double[] result) {
            this.mResult = result;
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (localName.equals("location")) {
                this.isLocation = GeoInfoManager.XML_DBG;
            } else if (localName.equals("lat")) {
                this.isLat = GeoInfoManager.XML_DBG;
            } else if (localName.equals("lng")) {
                this.isLng = GeoInfoManager.XML_DBG;
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("location")) {
                this.isLocation = false;
            } else if (localName.equals("lat")) {
                this.isLat = false;
            } else if (localName.equals("lng")) {
                this.isLng = false;
            }
        }

        public void characters(char[] ch, int start, int length) {
            if (this.isLocation) {
                String res = new String(ch, start, length);
                if (this.isLat) {
                    this.mResult[0] = Double.valueOf(res).doubleValue();
                    Log.i(GeoInfoManager.TAG, "lat = " + res);
                } else if (this.isLng) {
                    this.mResult[1] = Double.valueOf(res).doubleValue();
                    Log.i(GeoInfoManager.TAG, "lng = " + res);
                }
            }
        }
    }

    public GeoInfoManager(Context ctx) {
        this.mContext = ctx;
    }

    public static double[] getLastGPS(Context context) {
        double[] axis = new double[]{0.0d, 0.0d};
        LocationManager localLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List locationProviders = localLocationManager.getProviders(XML_DBG);
        Log.w(TAG, "locationProviders size=" + locationProviders.size());
        for (int i = locationProviders.size() - 1; i >= 0; i--) {
            @SuppressLint("MissingPermission") Location location = localLocationManager.getLastKnownLocation((String) locationProviders.get(i));
            if (location != null) {
                axis[0] = location.getLatitude();
                axis[1] = location.getLongitude();
                Log.i(TAG, "Latitude:" + axis[0] + " Longitude" + axis[1]);
                break;
            }
        }
        return axis;
    }

    public static double[] getCoordFromZip(Context ctx, String zip) {
        double[] lat_lon = new double[]{360.0d, 360.0d};
        String queryString = cityLocSvr + zip.replace(" ", "+");
        Log.d(TAG, "queryString = " + queryString);
        try {
            URL url = new URL(queryString);
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xr.setContentHandler(new GoogleCityInfoHandler(lat_lon));
            xr.parse(new InputSource(url.openStream()));
        } catch (Exception e) {
            Log.e(TAG, "Error Happen when getCoordFromZip", e);
        }
        return lat_lon;
    }

    public static String syncGetCityName(Context ctx) {
        String name = null;
        double[] cords = getLastGPS(ctx);
        try {
            List<Address> addresses = new Geocoder(ctx, Locale.getDefault()).getFromLocation(cords[0], cords[1], 1);
            if (addresses != null && addresses.size() > 0) {
                name = ((Address) addresses.get(0)).getLocality();
            }
        } catch (IOException ex) {
            Log.e(TAG, "retrive city resId failed: " + ex);
        }
        return name;
    }

    public static void asyncGetCityName(Context ctx) {
        new CityAsyncTask(ctx).execute(new String[0]);
    }
}
