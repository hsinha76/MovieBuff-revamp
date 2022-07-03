package com.hsdroid.moviebuff.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hsdroid.moviebuff.ui.MovieDetails;
import com.hsdroid.moviebuff.R;
import com.hsdroid.moviebuff.Model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    List<MovieResponse> ar_movie;
    FragmentActivity mainActivity;

    public MovieAdapter(List<MovieResponse> ar_movie, FragmentActivity mainActivity) {
        this.ar_movie = ar_movie;
        this.mainActivity = mainActivity;
    }

    public void filterList(ArrayList<MovieResponse> movieTitles) {
        this.ar_movie = movieTitles;
        notifyDataSetChanged();
    }

    public void filterFavouritesList(ArrayList<MovieResponse> movieTitles) {
        this.ar_movie = movieTitles;
        notifyDataSetChanged();
    }

    public void filterWatchList(ArrayList<MovieResponse> movieTitles) {
        this.ar_movie = movieTitles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity);
        View view = layoutInflater.inflate(R.layout.item_movie, parent, false);
        MovieHolder holder = new MovieHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        MovieResponse response = ar_movie.get(position);
        Log.d("backdrop", response.getBackdrop_path());
        Glide.with(mainActivity)
                .load("https://image.tmdb.org/t/p/w500/" + response.getPoster_path())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(holder.movie_image);

        holder.movie_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, MovieDetails.class);
                intent.putExtra("movie", response);
                Pair<View, String> p1 = Pair.create((View) holder.movie_image, "profile");
                Pair<View, String> p2 = Pair.create(holder.movie_title, "palette");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mainActivity, p1, p2);
                mainActivity.startActivity(intent, options.toBundle());
            }
        });

        holder.movie_title.setText(response.getTitle());
        holder.movie_vote.setText(String.valueOf(response.getVote_average()));

    }

    @Override
    public int getItemCount() {
        return ar_movie.size();
    }

    static class MovieHolder extends RecyclerView.ViewHolder {

        ImageView movie_image;
        TextView movie_title, movie_vote, movie_language;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            movie_image = itemView.findViewById(R.id.movie_image);
            movie_title = itemView.findViewById(R.id.movie_title);
            movie_vote = itemView.findViewById(R.id.tx_movie_vote_3);
        }
    }


}
