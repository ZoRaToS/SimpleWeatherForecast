package com.example.blackclover.simpleweatherforecast;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences pref;

    public CityPreference(Activity activity) {
        pref = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity() {
        return pref.getString("city", "KYIV, UA");
    }

    void setCity(String city){
        pref.edit().putString("city", city).commit();
    }
}

