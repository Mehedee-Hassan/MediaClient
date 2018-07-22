package automation.test.testapp2.background;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.util.List;

import automation.test.testapp2.util.ApiKey;


public abstract class GetPlaylistAsyncTask extends AsyncTask<String, Void, Pair<String, List<Video>>> {
    private static final String TAG = "GetPlaylistAsyncTask";
    private static final Long YOUTUBE_PLAYLIST_MAX_RESULTS = 10L;

    //see: https://developers.google.com/youtube/v3/docs/playlistItems/list
    private static final String YOUTUBE_PLAYLIST_PART = "snippet";
    private static final String YOUTUBE_PLAYLIST_FIELDS = "pageInfo,nextPageToken,items(id,snippet(resourceId/videoId))";
    //see: https://developers.google.com/youtube/v3/docs/videos/list
    private static final String YOUTUBE_VIDEOS_PART = "snippet,contentDetails,statistics"; // video resource properties that the response will include.
    private static final String YOUTUBE_VIDEOS_FIELDS = "items(id,snippet(title,description,thumbnails/high),contentDetails/duration,statistics)"; // selector specifying which fields to include in a partial response.

    private YouTube mYouTubeDataApi;
    List<Video> searchResultList = null;

    public GetPlaylistAsyncTask(YouTube api) {
        mYouTubeDataApi = api;
    }

    @Override
    protected Pair<String, List<Video>> doInBackground(String... params) {


        final String nextPageToken;
        boolean mostPopular = false;
        String setCategory = null;

        if (params.length == 1) {
            nextPageToken = params[0];
        } else {
            nextPageToken = null;
        }
        if(params.length == 3){
            mostPopular = params[1] == null ? true:false;
            setCategory = params[2];
        }

        Log.d(TAG, "doInBackground: "+"**1");
        if(params.length == 1)
        Log.d(TAG, "doInBackground: no token"+params[0]);
        if(params.length > 1)
        Log.d(TAG, "doInBackground: 1 > "+params[1]);

        VideoListResponse videoListResponse= null;

        try {
            YouTube.Videos.List videosList = mYouTubeDataApi.videos().list("snippet, statistics, contentDetails");

//            if(mostPopular)
                videosList.setChart("mostPopular");
            if(setCategory != null)
                videosList.setVideoCategoryId(setCategory);

            Log.d(TAG, "doInBackground: cat: "+setCategory);

//            videosList.setRegionCode(Constant.YT.VIDEO.REGION_CODE.INDIA);

            videosList.setMaxResults(15L);
            videosList.setFields("items(id, snippet/defaultAudioLanguage, snippet/defaultLanguage, snippet/publishedAt, snippet/title, snippet/channelId, snippet/channelTitle," +
                    "snippet/thumbnails, contentDetails/duration, statistics)," +
                    "nextPageToken");

            videosList.setKey(ApiKey.YOUTUBE_API_KEY);
            videosList.setPageToken(nextPageToken);
            videoListResponse= videosList.execute();

            if(videoListResponse == null){
                return null;
            }

            searchResultList = videoListResponse.getItems();

//            for(Video test : searchResultList) {
//                Log.d(TAG, "doInBackground: " + test.getId().toString());
//            }






        }catch (Exception ex){

        }

        if(videoListResponse != null)
        return new Pair(videoListResponse.getNextPageToken(), searchResultList);
        else if(searchResultList != null && searchResultList.size() > 1)

            return new Pair(null, searchResultList);
        else
            return  new Pair<>(null, null);
    }
}
