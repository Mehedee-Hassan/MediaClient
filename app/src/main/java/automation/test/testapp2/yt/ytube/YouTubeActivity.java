package automation.test.testapp2.yt.ytube;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;

import java.util.List;

import automation.test.testapp2.R;
import automation.test.testapp2.yt.ytube.model.ListVideos;


public class YouTubeActivity extends AppCompatActivity {

    private static final String TAG = "YouTubeActivity";
    private YouTube mYoutubeDataApi;
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListVideos mListVideos;
    private YouTube mYouTubeDataApi;
    private ListCardAdapter mListCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_activity);

        
        if(!isConnected()){
            Toast.makeText(YouTubeActivity.this,"No Internet Connection Detected", Toast.LENGTH_LONG).show();
        }





        if (ApiKey.YOUTUBE_API_KEY.startsWith(ApiKey.YOUTUBE_API_KEY)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Edit ApiKey.java and replace \"YOUR_API_KEY\" with your Applications Browser API Key")
                        .setTitle("Missing API Key")
                        .setNeutralButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (savedInstanceState == null) {
            mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                    .setApplicationName(getResources().getString(R.string.app_name))
                    .build();
        }



        mRecyclerView = (RecyclerView) findViewById(R.id.youtube_recycler_view);
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

        // if we have a playlist in our retained fragment, use it to populate the UI
        if (mListVideos != null) {
            // reload the UI with the existing playlist.  No need to fetch it again
            reloadUi(mListVideos, false);
        } else {
            // otherwise create an empty playlist using the first item in the playlist id's array
            mListVideos = new ListVideos();

            // and reload the UI with the selected playlist and kick off fetching the playlist content
            reloadUi(mListVideos, true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.you_tube, menu);
        return true;
    }
    
    public boolean isConnected() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_recyclerview) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadUi(final ListVideos listVideos, boolean fetchPlaylist) {
        // initialize the cards adapter
        initCardAdapter(listVideos);

        if (fetchPlaylist) {
            // start fetching the selected playlistVideos contents
            new GetPlaylistAsyncTask(mYoutubeDataApi) {
                @Override
                public void onPostExecute(Pair<String, List<Video>> result) {
                    handleGetPlaylistResult(listVideos, result);

                }
            }.execute( listVideos.getNextPageToken());
        }
    }

    private void initCardAdapter(final ListVideos listVideos) {
        // create the adapter with our playlistVideos and a callback to handle when we reached the last item
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

            }

        });


        mRecyclerView.setAdapter(mListCardAdapter);
    }

    private void handleGetPlaylistResult(ListVideos listVideos, Pair<String, List<Video>> result) {
        if (result == null) {
            Log.d(TAG, "handleGetPlaylistResult: result null");
            return;}

        Log.d(TAG, "handleGetPlaylistResult: result "+result.second.size());
        final int positionStart = listVideos.size();
        listVideos.setNextPageToken(result.first);
        listVideos.addAll(result.second);
        mListCardAdapter.notifyItemRangeInserted(positionStart, result.second.size());
    }


    /**
     * Interface used by the {@link ListCardAdapter} to inform us that we reached the last item in the list.
     */
    public interface LastItemReachedListener {
        void onLastItem(int position, String nextPageToken);
        void onMovedFromFirst();
        void onFirsItemReached();
        void playVideoOnclick(String id, String title, String description);
    }

}
