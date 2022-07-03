package com.hsdroid.moviebuff.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;


public class MoviesRepository {
    private static MoviesRepository instance = null;

    public static MoviesRepository getInstance() {
        if (instance == null)
            instance = new MoviesRepository();
        return instance;
    }

    public MutableLiveData<JSONObject> requestForMoviesListService() {
        MutableLiveData<JSONObject> dataResponse = new MutableLiveData<>();
        AndroidNetworking.get("https://api.themoviedb.org/3/movie/popular?api_key=YOUR_API_KEY_HERE")
                .setPriority(Priority.LOW)
                .doNotCacheResponse()
                .getResponseOnlyFromNetwork()
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataResponse.postValue(response);
                        Log.d("SUCCESS::::::::", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        dataResponse.postValue(null);
                        Log.d("FAILED::::::::", error.getErrorDetail());
                    }
                });
        return dataResponse;
    }
}
