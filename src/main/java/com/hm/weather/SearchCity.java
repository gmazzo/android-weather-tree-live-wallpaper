package com.hm.weather;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hm.weather.sky_manager.GeoInfoManager;
import com.hm.weather.sky_manager.WeatherCondition;
import com.hm.weather.sky_manager.WeatherCondition.CityInfo;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchCity extends ListActivity implements OnCancelListener {
    private static final int ACT_CANCEL = 1;
    public static final String CITY_CODE = "cityCode";
    public static final String CITY_INFO = "cityInfo";
    public static final String CITY_NAME = "cityName";
    private static final boolean DEBUG = false;
    public static final String DIS_NAME = "disName";
    public static final String INVALID_CODE = "InvalidCode";
    private static final int SEARCH_RESPONSE = 0;
    public static final String STATE_NAME = "stateName";
    private static final String TAG = "SearchCity";
    private boolean actCanceled = DEBUG;
    ArrayList<CityInfo> cityList = null;
    private final Handler mCityHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchCity.SEARCH_RESPONSE /*0*/:
                    if (!SearchCity.this.actCanceled) {
                        if (SearchCity.this.progressDialog != null && SearchCity.this.progressDialog.isShowing()) {
                            SearchCity.this.progressDialog.dismiss();
                        }
                        SearchCity.this.initCities(SearchCity.this.cityList);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private String mSearchCity = null;
    private ProgressDialog progressDialog = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchcity);
        setDefaultKeyMode(2);
        Button searchBtn = (Button) findViewById(R.id.search_city_now);
        if (searchBtn != null) {
            searchBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SearchCity.this.setListAdapter(null);
                    EditText editText = (EditText) SearchCity.this.findViewById(R.id.loc_edit);
                    if (editText != null) {
                        SearchCity.this.searchCity(editText.getText().toString());
                    }
                }
            });
        }
    }

    public void searchCity(String searchString) {
        this.actCanceled = DEBUG;
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle(getText(R.string.search_location_title));
        this.progressDialog.setMessage(getText(R.string.search_location_msg));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(DEBUG);
        this.progressDialog.setOnCancelListener(this);
        this.progressDialog.setButton(-2, getText(17039360), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SearchCity.this.actCanceled = true;
                Log.v(SearchCity.TAG, "send msg ACT_CANCEL to handler");
                Message.obtain(SearchCity.this.mCityHandler, 1).sendToTarget();
            }
        });
        this.mSearchCity = searchString;
        this.progressDialog.show();
        new Thread() {
            public void run() {
                WeatherCondition weather_condition = new WeatherCondition();
                SearchCity.this.cityList = weather_condition.SearchCity(SearchCity.this.mSearchCity);
                if (!(SearchCity.this.cityList == null || SearchCity.this.cityList.isEmpty())) {
                    Iterator i$ = SearchCity.this.cityList.iterator();
                    while (i$.hasNext()) {
                        CityInfo city = (CityInfo) i$.next();
                        if (!(city.mcityCode == null || city.mcityCode.isEmpty())) {
                            double[] coords = GeoInfoManager.getCoordFromZip(SearchCity.this, city.mcityCode);
                            city.mLatitude = coords[SearchCity.SEARCH_RESPONSE];
                            city.mLongitude = coords[1];
                        }
                    }
                }
                Message.obtain(SearchCity.this.mCityHandler, SearchCity.SEARCH_RESPONSE).sendToTarget();
            }
        }.start();
    }

    public void initCities(ArrayList<CityInfo> cityList) {
        if (cityList == null) {
            cityList = new ArrayList();
        }
        if (cityList.isEmpty()) {
            CityInfo fake_ci = new CityInfo();
            fake_ci.mCity = getString(R.string.no_result_found);
            fake_ci.mcityCode = INVALID_CODE;
            cityList.add(fake_ci);
        } else {
            Iterator i$ = cityList.iterator();
            while (i$.hasNext()) {
                CityInfo cityInfo = (CityInfo) i$.next();
            }
        }
        setListAdapter(new ArrayAdapter(this, R.layout.searchcity_item, cityList));
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        CityInfo city = (CityInfo) l.getItemAtPosition(position);
        if (city.mcityCode != null && !city.mcityCode.isEmpty()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(CITY_INFO, city);
            setResult(-1, resultIntent);
            finish();
        }
    }

    public void onCancel(DialogInterface dialog) {
        this.actCanceled = true;
        Message.obtain(this.mCityHandler, 1).sendToTarget();
    }
}
