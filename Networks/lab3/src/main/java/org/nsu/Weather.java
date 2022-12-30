package org.nsu;

import org.json.JSONArray;
import org.json.JSONObject;

public class Weather {
    private static String findInArray(JSONArray arr, String what) {
        String res = null;
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject inWeather = arr.getJSONObject(i);
            if (inWeather.has(what)) {
                res = inWeather.getString(what);
                break;
            }
        }
        return res;
    }

    private static Weather getWeatherFrom(String response) {
        if (response == null) {
            return null;
        }
        Weather weather = new Weather();
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.has("weather")) {
            JSONArray weatherArr = jsonResponse.getJSONArray("weather");
            weather.description = findInArray(weatherArr, "description");
        }
        if (jsonResponse.has("main")) {
            JSONObject main = jsonResponse.getJSONObject("main");
            weather.temp = (main.has("temp")) ? main.getDouble("temp") : 0.0;
            weather.feelsLike = (main.has("feels_like")) ? main.getDouble("feels_like") : 0.0;
            weather.pressure = (main.has("pressure")) ? main.getDouble("pressure") : 0.0;
            weather.humidity = (main.has("humidity")) ? main.getDouble("humidity") : 0.0;
        }
        if (jsonResponse.has("wind")) {
            JSONObject wind = jsonResponse.getJSONObject("main");
            weather.windSpeed = (wind.has("speed")) ? wind.getDouble("speed") : 0.0;
        }
        if (jsonResponse.has("visibility")) {
            weather.visibility = jsonResponse.getDouble("visibility");
        }
        return weather;
    }

    public static Weather findWeather(LocationCoordinates coordinates) {
        String url = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat=" + coordinates.latitude() +
                "&lon=" + coordinates.longitude() +
                "&appid=" + apiKey +
                "&lang=en&units=metric";
        String response = ApiCaller.getResponseAsJSON(url);
        return getWeatherFrom(response);
    }

    public String getDescription() {
        return description;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getVisibility() {
        return visibility;
    }

    private String description = null;
    private double temp;
    private double feelsLike;
    private double pressure;
    private double humidity;
    private double windSpeed;
    private double visibility;

    private static String apiKey = "31e818e0682ca6efac18ce313700e7d9";
}
