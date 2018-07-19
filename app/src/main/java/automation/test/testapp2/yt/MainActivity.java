package automation.test.testapp2.yt;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;

import java.util.List;

import automation.test.testapp2.R;
import automation.test.testapp2.yt.util.Constant;
import automation.test.testapp2.yt.ytube.ApiKey;
import automation.test.testapp2.yt.ytube.GetPlaylistAsyncTask;
import automation.test.testapp2.yt.ytube.ListCardAdapter;
import automation.test.testapp2.yt.ytube.YouTubeActivity;
import automation.test.testapp2.yt.ytube.model.ListVideos;


public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private static final String TAG = "MainActivity";

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;

    private TextView mTextMessage;
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    reloadUiCalling(true, Constant.YT.VIDEO.CATEGORY_ID.COMEDY);

                    return true;
                case R.id.navigation_sports:
                    reloadUiCalling(true, Constant.YT.VIDEO.CATEGORY_ID.Sports);

                    return true;
//                case R.id.navigation_entertainment:
//                    reloadUiCalling(true,Constant.YT.VIDEO.CATEGORY_ID.Entertainment);
//                    return true;
                case R.id.navigation_films:
                    reloadUiCalling(true, Constant.YT.VIDEO.CATEGORY_ID.Films);
                    return true;
//                case R.id.navigation_animation:
//                    reloadUiCalling(true,Constant.YT.VIDEO.CATEGORY_ID.Animations);
//                    return true;
                case R.id.navigation_funny:
                    reloadUiCalling(true, Constant.YT.VIDEO.CATEGORY_ID.COMEDY);
                    return true;
//                case R.id.navigation_action:
//                    reloadUiCalling(true,Constant.YT.VIDEO.CATEGORY_ID.SiFi);
//                    return true;
                case R.id.navigation_music:
                    reloadUiCalling(true, Constant.YT.VIDEO.CATEGORY_ID.Music);
                    return true;
            }
            return false;
        }
    };
//    private RecyclerView recyclerView;
//    private MoviesAdapter mAdapter;
//    private List<Movie> movieList = new ArrayList<>();
    private YouTube mYoutubeDataApi;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListVideos mListVideos;
    private ListCardAdapter mListCardAdapter;
    private View videoDescriptionBlock;
    private int topPositionForV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);



//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();


        changeBottomNavBar();
        initRecycler();


    }

    private void changeBottomNavBar() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);

            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();



            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }

    private void initRecycler() {

        if(!isConnected()){
            Toast.makeText(MainActivity.this,"No Internet Connection Detected", Toast.LENGTH_LONG).show();
        }



        mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();




        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        Resources resources = getResources();
        if (false) {
        } else {
            // use a linear layout on phone devices
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
        }


        mRecyclerView.setLayoutManager(mLayoutManager);
        reloadUiCalling(false,null);


    }

    private void reloadUiCalling(boolean loadNewListView,String videoCategory) {

        if (loadNewListView){
            mListVideos = new ListVideos();
            reloadUi(mListVideos, true, Constant.FLAGS.NO_MOST_POPULAR, videoCategory);
        }else
        if (mListVideos != null ) {
            reloadUi(mListVideos, false, Constant.FLAGS.MOST_POPULAR, videoCategory);
        } else {
            mListVideos = new ListVideos();
            reloadUi(mListVideos, true, Constant.FLAGS.MOST_POPULAR, videoCategory);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.player = youTubePlayer;
//        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);

        if (!b) {
            player.cueVideo("hk3bXMbFkew"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }


    private void reloadUi(final ListVideos listVideos, boolean fetchPlaylist, String fetchMostPopular , String categoryId) {
        // initialize the cards adapter
        initCardAdapter(listVideos);

        if (fetchPlaylist) {
            new GetPlaylistAsyncTask(mYoutubeDataApi) {
                @Override
                public void onPostExecute(Pair<String, List<Video>> result) {
                    handleGetPlaylistResult(listVideos, result);
                }
            }.execute(listVideos.getNextPageToken(),fetchMostPopular,categoryId);
        }
    }

    private void initCardAdapter(final ListVideos listVideos) {

        mListCardAdapter = new ListCardAdapter(listVideos, new YouTubeActivity.LastItemReachedListener() {
            @Override
            public void onLastItem(int position, String nextPageToken) {
                new GetPlaylistAsyncTask(mYoutubeDataApi) {
                    @Override
                    public void onPostExecute(Pair<String, List<Video>> result) {
                        handleGetPlaylistResult(listVideos, result);
                    }
                }.execute(listVideos.getNextPageToken());
            }

            @Override
            public void onMovedFromFirst() {

            }

            @Override
            public void onFirsItemReached() {


            }

            @Override
            public void playVideoOnclick(String id, String title, String description) {
                player.cueVideo(id); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
                player.play();
                TextView bottomYTtitle = (TextView) findViewById(R.id.yt_video_title_bottom);
                TextView bottomYTdes = (TextView) findViewById(R.id.yt_video_description_bottom);

                bottomYTdes.setText(description);
                bottomYTtitle.setText(title);
            }
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){

                int topPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
                videoDescriptionBlock = findViewById(R.id.video_description_view);

                    if (topPosition <= 0) {

                        if(videoDescriptionBlock.getVisibility() == View.GONE) {
                            videoDescriptionBlock.setVisibility(View.VISIBLE);
                        }
                    } else if(topPosition >1){
                        if(videoDescriptionBlock.getVisibility() == View.VISIBLE) {
                            videoDescriptionBlock.setVisibility(View.GONE);
                         }

                    }


            }

        });

        mRecyclerView.setAdapter(mListCardAdapter);
    }

    private void handleGetPlaylistResult(ListVideos listVideos, Pair<String, List<Video>> result) {
        if (result == null) {
            Log.d(TAG, "handleGetPlaylistResult: result null");
            return;}
        int listSize = 0;
        if(result.second != null) {
            Log.d(TAG, "handleGetPlaylistResult: result " + result.second.size());
            listSize = result.second.size();


            final int positionStart = listVideos.size();
            listVideos.setNextPageToken(result.first);
            listVideos.addAll(result.second);

            mListCardAdapter.notifyItemRangeInserted(positionStart, listSize);
        }
    }


    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }

    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }
}
