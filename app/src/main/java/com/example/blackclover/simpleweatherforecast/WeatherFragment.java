package com.example.blackclover.simpleweatherforecast;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Typeface.createFromAsset;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updated_field;
    TextView weather_icon;
    TextView current_temperature_field;
    TextView details_field;

    android.os.Handler handler;

    public WeatherFragment() {
        handler = new android.os.Handler();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updated_field = (TextView) rootView.findViewById(R.id.updated_field);
        weather_icon = (TextView) rootView.findViewById(R.id.weather_icon);
        current_temperature_field = (TextView) rootView.findViewById(R.id.current_temperature_field);
        details_field = (TextView) rootView.findViewById(R.id.details_field);

        weather_icon.setTypeface(weatherFont);
        return rootView;
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject json = ResponseFromServer.getJSON(getActivity(),
                        city);
                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        };
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    "," + json.getJSONObject("sys").getString("country"));
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            details_field.setText(details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            current_temperature_field.setText(String.format("%.2f", main.getDouble("temp")) + "°С");

            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String updateOn = dateFormat.format(new Date(json.getLong("dt") * 1000));

            updated_field.setText("Last update: " + updateOn);
            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e("SimpleWeatherForeCast",
                    "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long curruentTime = new Date().getTime();
            if (curruentTime >= sunrise && curruentTime <= sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.weather_cloudy);
                    break;
            }
        }
        weather_icon.setText(icon);
    }
    public void changeCity(String city){
        updateWeatherData(city);
    }
}