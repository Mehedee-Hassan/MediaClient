package automation.test.testapp2.view.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import automation.test.testapp2.R;
import automation.test.testapp2.model.SectionDataModel;
import automation.test.testapp2.yt.MainActivity;
import automation.test.testapp2.yt.util.Constant;
import automation.test.testapp2.yt.ytube.GetPlaylistAsyncTask;
import automation.test.testapp2.yt.ytube.ListCardAdapter;
import automation.test.testapp2.yt.ytube.YouTubeActivity;
import automation.test.testapp2.yt.ytube.model.ListVideos;

/**
 * Created by DELL on 7/18/2018.
 */

public class YoutubeFragment extends Fragment {
    private YouTube mYoutubeDataApi = null;
    private ArrayList<SectionDataModel> allSampleData;
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();

    int color;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListVideos mListVideos;
    private ListCardAdapter mListCardAdapter;
    private static final String TAG = "YoutubeFragment";
    private ProgressBar progrssbar;


    public YoutubeFragment() {
        mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();
    }

    public static YoutubeFragment newInstance(String name, YouTube mYoutubeDataApi) {


        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setYoutubeDataApi(mYoutubeDataApi);

        return fragment;
    }



    @SuppressLint("ValidFragment")
    public YoutubeFragment(int color) {

        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youtube_tab_fragment, container, false);

        allSampleData = new ArrayList<>();

        initRecycler(view);

        return view;
    }


    private void initRecycler(View view) {

        if(!isConnected()){
            Toast.makeText(this.getContext(),"No Internet Connection Detected", Toast.LENGTH_LONG).show();
        }

        // TODO: 7/22/2018 set data api from activity
        // TODO: 7/22/2018 use single tone  TRY TO CREATE DESIGN
        mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null).setApplicationName(getResources().getString(R.string.app_name)).build();


        progrssbar = view.findViewById(R.id.yt_fra_rv_loading_pb);
        progrssbar.setVisibility(View.VISIBLE);

//      youtube list
        mRecyclerView = (RecyclerView) view.findViewById(R.id.yt_tab_list_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        reloadUiCalling(false,null);

    }
    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void reloadUiCalling(boolean loadNewListView,String videoCategory) {

            mListVideos = new ListVideos();
            reloadUi(mListVideos, true, Constant.FLAGS.MOST_POPULAR, videoCategory);
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
    public void setYoutubeDataApi(YouTube youtubeDataApi) {
        this.mYoutubeDataApi = youtubeDataApi;
    }


    private void initCardAdapter(final ListVideos listVideos) {

        boolean fromMainActivity  = true;

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

                // TODO: 7/22/2018  pass to activity youtubeActivity
//                player.cueVideo(id); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
//                player.play();
//                TextView bottomYTtitle = (TextView) findViewById(R.id.yt_video_title_bottom);
//                TextView bottomYTdes = (TextView) findViewById(R.id.yt_video_description_bottom);
//
//                bottomYTdes.setText(description);
//                bottomYTtitle.setText(title);
            }

            @Override
            public void startActivityAndPlay(String youtubeId, String title, String details) {

                Intent ytActivityIntent = new Intent(getActivity(),MainActivity.class);
                ytActivityIntent.putExtra(Constant.START_ACTIVITY.YTA.KEYS.DETAILS,details);
                ytActivityIntent.putExtra(Constant.START_ACTIVITY.YTA.KEYS.YT_VIDEO_ID,youtubeId);
                ytActivityIntent.putExtra(Constant.START_ACTIVITY.YTA.KEYS.YT_VIDEO_TITLE,title);

//                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out);

                getActivity().startActivity(ytActivityIntent);

            }


        },fromMainActivity);


            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
    //                int topPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
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
            progrssbar.setVisibility(View.GONE);

        }
    }


}
