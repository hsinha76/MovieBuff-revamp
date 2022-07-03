package com.hsdroid.moviebuff.viewModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.Map;

public class MoviesViewModel extends ViewModel {

    private MutableLiveData<JSONObject> mutableLiveData = null;

    public MutableLiveData<JSONObject> getMoviesListService() {
        mutableLiveData = null;
        if (mutableLiveData == null)
            return mutableLiveData = MoviesRepository.getInstance().requestForMoviesListService();
        else
            return null;
    }
}