package com.hfut.zhaojiabao.myrecord.network;

import android.util.Log;

import com.google.gson.Gson;
import com.hfut.zhaojiabao.myrecord.BuildConfig;
import com.hfut.zhaojiabao.myrecord.network.weather_entities.ForecastWeatherEntity;
import com.hfut.zhaojiabao.myrecord.network.weather_entities.RealTimeWeatherEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class WeatherApi {
    private static final String TAG = "WeatherApi";
    /**
     * 预报天气URL
     */
    private static final String FORECAST_WEATHER_URL
            = "https://free-api.heweather.com/s6/weather/forecast?parameters&location={cityName}&key=";

    /**
     * 实况天气URL
     */
    private static final String REAL_TIME_WEATHER_URL
            = "https://free-api.heweather.com/s6/weather/now?parameters&location={cityName}&key=";

    private static OkHttpClient sClient;

    private static class Holder {
        private static final WeatherApi sInstance = new WeatherApi();
    }

    private WeatherApi() {
        sClient = new OkHttpClient();
    }

    public static WeatherApi getInstance() {
        return Holder.sInstance;
    }

    private static String realTimeWeatherUrl(String cityName) {
        return REAL_TIME_WEATHER_URL.replace("{cityName}", cityName) + BuildConfig.weatherKey;
    }

    private static String forecastWeatherUrl(String cityName) {
        return FORECAST_WEATHER_URL.replace("{cityName}", cityName) + BuildConfig.weatherKey;
    }

    /**
     * get forecast weather.
     */
    @SuppressWarnings("unused")
    public Observable<ForecastWeatherEntity> getForecastWeather(String cityName) {
        return Observable
                .create(e -> {
                    Request request = new Request.Builder()
                            .url(forecastWeatherUrl(cityName))
                            .build();

                    Response response = sClient.newCall(request).execute();
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        e.onError(null);
                        return;
                    }
                    if (responseBody != null) {
                        String item = responseBody.string();
                        Log.i(TAG, "forecast weather: " + item);
                        Gson gson = new Gson();
                        JSONArray jsonArray = new JSONObject(item).getJSONArray("HeWeather6");
                        ForecastWeatherEntity forecastWeatherEntity = gson.fromJson(jsonArray.getJSONObject(0).toString(), ForecastWeatherEntity.class);
                        e.onNext(forecastWeatherEntity);
                        e.onComplete();
                    } else {
                        e.onError(null);
                    }
                });
    }

    /**
     * get realTime weather.
     */
    public Observable<RealTimeWeatherEntity> getRealTimeWeather(String cityName) {
        return Observable
                .create(e -> {
                    Request request = new Request.Builder()
                            .url(realTimeWeatherUrl(cityName))
                            .build();

                    Response response = sClient.newCall(request).execute();
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        e.onError(null);
                        return;
                    }
                    if (responseBody != null) {
                        String item = responseBody.string();
                        Log.i(TAG, "realTime weather: " + item);
                        Gson gson = new Gson();
                        JSONArray jsonArray = new JSONObject(item).getJSONArray("HeWeather6");
                        RealTimeWeatherEntity forecastWeatherEntity
                                = gson.fromJson(jsonArray.getJSONObject(0).toString(), RealTimeWeatherEntity.class);
                        e.onNext(forecastWeatherEntity);
                        e.onComplete();
                    } else {
                        e.onError(null);
                    }
                });
    }
}
