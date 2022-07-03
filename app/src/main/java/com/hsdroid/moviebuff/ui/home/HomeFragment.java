package com.hsdroid.moviebuff.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hsdroid.moviebuff.Adapter.MovieAdapter;
import com.hsdroid.moviebuff.Model.MovieResponse;
import com.hsdroid.moviebuff.R;
import com.hsdroid.moviebuff.Util.Constants;
import com.hsdroid.moviebuff.Util.SharedPreferenceUtil;
import com.hsdroid.moviebuff.databinding.FragmentHomeBinding;
import com.hsdroid.moviebuff.viewModel.MoviesViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MovieAdapter adapter;
    private List<MovieResponse> feedItemList, movieResponsesList;
    private MovieResponse item;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        View root = binding.getRoot();
        init(root);
        feedItemList.clear();
        fetchData();
    }

    private void init(View root) {
        shimmerFrameLayout = root.findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmerAnimation();

        recyclerView = root.findViewById(R.id.recycler_movielist);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        feedItemList = new ArrayList<MovieResponse>();
        movieResponsesList = new ArrayList<>();

        adapter = new MovieAdapter(feedItemList, getActivity());
        recyclerView.setAdapter(adapter);

        searchBar = root.findViewById(R.id.search);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void fetchData() {
        MoviesViewModel moviesViewModel = new ViewModelProvider(getActivity()).get(MoviesViewModel.class);
        moviesViewModel.getMoviesListService().observe(getActivity(), response -> {
            if (response != null) {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                parseJsonFeed(response);
            } else {
                Toast.makeText(getActivity(), "Something went wrong, pls check your Internet!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void parseJsonFeed(JSONObject response) {

        boolean adult;
        String backdrop_path;
        int[] genres;
        int id;
        String original_language;
        String original_title;
        String overview;
        float popularity;
        String poster_path;
        String release_date;
        String title;
        String video;
        float vote_average;
        long vote_count;

        try {
            JSONArray jsonArray = response.optJSONArray("results");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    item = new MovieResponse();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    adult = Boolean.parseBoolean(jsonObject.get("adult").toString());
                    backdrop_path = jsonObject.get("backdrop_path").toString();
                    String ids = jsonObject.get("genre_ids").toString();
                    String sub_string = ids.substring(1, ids.length() - 1);
                    String[] split = sub_string.split(",");
                    genres = new int[split.length];
                    for (int j = 0; j < split.length; j++) {
                        genres[j] = Integer.parseInt(split[j]);
                    }
                    id = Integer.parseInt(jsonObject.get("id").toString());
                    Log.d("IDS:::::::::::::::::", String.valueOf(id));
                    original_language = jsonObject.get("original_language").toString();
                    original_title = jsonObject.get("original_title").toString();
                    overview = jsonObject.get("overview").toString();
                    popularity = Float.parseFloat(jsonObject.get("popularity").toString());
                    poster_path = jsonObject.get("poster_path").toString();
                    release_date = jsonObject.get("release_date").toString();
                    title = jsonObject.get("title").toString();
                    video = jsonObject.get("video").toString();
                    vote_average = Float.parseFloat(jsonObject.get("vote_average").toString() + "f");
                    vote_count = Long.parseLong(jsonObject.get("vote_count").toString());

                    item.setAdult(adult);
                    item.setBackdrop_path(backdrop_path);
                    item.setGenres(genres);
                    item.setTitle(title);
                    item.setId(String.valueOf(id));
                    item.setOriginal_language(original_language);
                    item.setOriginal_title(original_title);
                    item.setOverview(overview);
                    item.setPopularity(popularity);
                    item.setPoster_path(poster_path);
                    item.setRelease_date(release_date);
                    item.setVote_average(vote_average);
                    item.setVote_count(vote_count);
                    item.setVideo(video);

                    feedItemList.add(item);
                    movieResponsesList.add(item);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    searchBar.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.getCause();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void filterList(String text) {
        ArrayList<MovieResponse> movieResponses = new ArrayList<>();

        for (MovieResponse s : movieResponsesList) {
            if (s.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    s.getTitle().toLowerCase().contains(text.toLowerCase())) {
                movieResponses.add(s);
            }
        }

        adapter.filterList(movieResponses);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}