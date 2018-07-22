package automation.test.testapp2.model.yt;

import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;


public class ListVideos extends ArrayList<Video> {
    private String mNextPageToken;

    public ListVideos() {
    }

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }
}
