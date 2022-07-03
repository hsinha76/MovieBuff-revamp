package com.hsdroid.moviebuff.ui;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hsdroid.moviebuff.Model.MovieResponse;
import com.hsdroid.moviebuff.R;
import com.hsdroid.moviebuff.Util.Constants;
import com.hsdroid.moviebuff.Util.SharedPreferenceUtil;

public class MovieDetails extends AppCompatActivity {

    private Button playBtn, watchLaterBtn;
    private MovieResponse response;
    private ImageView movie_backdrop, movie_like;
    private TextView tx_movie_title, tx_movie_adult, tx_movie_release, tx_movie_language, tx_vote_count, tx_movie_vote, tx_movie_overview;
    private View adult_view;
    int like = 0, count = 1;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        response = (MovieResponse) getIntent().getSerializableExtra("movie");
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        userId = sharedPreferenceUtil.getStringPreference(Constants.USER_ID, null);

        String movie_title = response.getTitle();

        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.Toolbar_TitleText);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.Toolbar_Collapsed_TitleText);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView cardView = findViewById(R.id.pinned_details);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolBarLayout.setTitle(movie_title);
                    cardView.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
                    cardView.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        init();
        pinnedDataInit();
    }

    private void pinnedDataInit() {
        ImageView pinned_poster = findViewById(R.id.pinned_poster);
        TextView pinned_title = findViewById(R.id.tx_movie_title_2);
        TextView pinned_adult = findViewById(R.id.tx_movie_adult_2);
        TextView pinned_release = findViewById(R.id.tx_movie_release_2);
        TextView pinned_language = findViewById(R.id.tx_movie_language_2);
        View pinned_adult_view = findViewById(R.id.adult_line_2);
        TextView pinned_vote_count = findViewById(R.id.tx_movie_vote_count_2);
        TextView pinned_vote = findViewById(R.id.tx_movie_vote_2);
        setPinnedData(pinned_poster, pinned_title, pinned_adult, pinned_release, pinned_language, pinned_adult_view, pinned_vote_count, pinned_vote);
    }

    private void setPinnedData(ImageView pinned_poster, TextView pinned_title, TextView pinned_adult, TextView pinned_release, TextView pinned_language, View pinned_adult_view, TextView pinned_vote_count, TextView pinned_vote) {
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/" + response.getPoster_path())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(pinned_poster);

        pinned_title.setText(response.getTitle());
        pinned_language.setText(response.getOriginal_language());
        pinned_release.setText(response.getRelease_date());
        pinned_vote_count.setText("Votes : " + response.getVote_count());
        pinned_vote.setText(String.valueOf(response.getVote_average()));

        if (!response.getAdult()) {
            pinned_adult.setVisibility(View.GONE);
            pinned_adult_view.setVisibility(View.GONE);
        }
    }

    private void init() {
        playBtn = findViewById(R.id.btn_play);
        watchLaterBtn = findViewById(R.id.btn_watch_later);
        movie_like = findViewById(R.id.like);
        movie_backdrop = findViewById(R.id.movie_backdrop);
        tx_movie_adult = findViewById(R.id.tx_movie_adult);
        tx_movie_title = findViewById(R.id.tx_movie_title);
        tx_movie_release = findViewById(R.id.tx_movie_release);
        tx_movie_language = findViewById(R.id.tx_movie_language);
        tx_vote_count = findViewById(R.id.tx_movie_vote_count);
        tx_movie_vote = findViewById(R.id.tx_movie_vote);
        adult_view = findViewById(R.id.adult_line);
        tx_movie_overview = findViewById(R.id.tx_movie_overview);
        setData();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'Users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("Users");

        playBtn.setOnClickListener(a -> {
            showNotification("Movie is Playing", "Enjoy!");
        });

        watchLaterBtn.setOnClickListener(a -> {
            mFirebaseDatabase.child(userId)
                    .child("WatchList")
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(response.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            watchLaterPopup();
                        }
                    });
        });
    }

    private void setData() {
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/" + response.getBackdrop_path())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(movie_backdrop);

        tx_movie_title.setText(response.getTitle());
        tx_movie_language.setText(response.getOriginal_language());
        tx_movie_release.setText(response.getRelease_date());
        tx_vote_count.setText("Votes : " + response.getVote_count());
        tx_movie_vote.setText(String.valueOf(response.getVote_average()));
        tx_movie_overview.setText(response.getOverview());

        if (!response.getAdult()) {
            tx_movie_adult.setVisibility(View.GONE);
            adult_view.setVisibility(View.GONE);
        }
    }

    public void imageLiked(View view) {
        if (like == 0) {
            mFirebaseDatabase.child(userId)
                    .child("Favourites")
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(response.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            movie_like.setImageResource(R.drawable.ic_baseline_favorite_24_selected);
                            like = 1;
                        }
                    });
        } else {
            movie_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            like = 0;
        }
    }

    public void showNotification(String title, String message) {
        count++;
        Intent intent = new Intent(this, HomeActivity.class);
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //  Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.coin);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                //.setSound(soundUri)
                .setContentIntent(pendingIntent);
        builder.setNumber(count);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        } else {
            builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, String.valueOf(System.currentTimeMillis()), NotificationManager.IMPORTANCE_HIGH); // Here I tried put different channel name.
            notificationChannel.setShowBadge(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                notificationChannel.canBubble();
            }
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            notificationChannel.canShowBadge();
            notificationChannel.enableVibration(true);
            //notificationChannel.setSound(soundUri, audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(count, builder.build());
        }
    }

    public void watchLaterPopup() {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetails.this);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_layout, null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button closeBtn = customLayout.findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(a -> {
            dialog.dismiss();
        });
    }
}