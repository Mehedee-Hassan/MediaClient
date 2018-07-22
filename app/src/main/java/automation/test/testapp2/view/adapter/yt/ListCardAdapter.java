package automation.test.testapp2.view.adapter.yt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import automation.test.testapp2.R;
import automation.test.testapp2.util.PrettyTimeEx;
import automation.test.testapp2.yt.ytube.YouTubeActivity;
import automation.test.testapp2.model.yt.ListVideos;

import static android.graphics.Color.WHITE;


public class ListCardAdapter extends RecyclerView.Adapter<ListCardAdapter.ViewHolder> {
    private static final DecimalFormat sFormatter = new DecimalFormat("#,###,###");
    private final ListVideos mPlaylistVideos;
    private final YouTubeActivity.LastItemReachedListener mListener;

    private static final String TAG = "ListCardAdapter";
    private final boolean fromMainActivity;
    private Intent mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final Context mContext;
        public final TextView mTitleText;
        public final TextView mDescriptionText;
        public final ImageView mThumbnailImage;
        public final ImageView mShareIcon;
        public final TextView mShareText;
        public final TextView mDurationText;
        public final TextView mViewCountText;
        public final TextView mLikeCountText;
        public final TextView mDislikeCountText;
        public final TextView mPublishedDate;

        public ViewHolder(View v) {
            super(v);
            mContext = v.getContext();
            mTitleText = (TextView) v.findViewById(R.id.video_title);
            mDescriptionText = (TextView) v.findViewById(R.id.video_description);
            mThumbnailImage = (ImageView) v.findViewById(R.id.video_thumbnail);
            mShareIcon = (ImageView) v.findViewById(R.id.video_share);
            mShareText = (TextView) v.findViewById(R.id.video_share_text);
            mDurationText = (TextView) v.findViewById(R.id.video_dutation_text);
            mViewCountText= (TextView) v.findViewById(R.id.video_view_count);
            mLikeCountText = (TextView) v.findViewById(R.id.video_like_count);
            mDislikeCountText = (TextView) v.findViewById(R.id.video_dislike_count);
            mPublishedDate = (TextView) v.findViewById(R.id.video_published_date);
        }
    }



    public ListCardAdapter(ListVideos playlistVideos, YouTubeActivity.LastItemReachedListener lastItemReachedListener ,boolean fromMainActivity) {
        mPlaylistVideos = playlistVideos;
        mListener = lastItemReachedListener;
        this.fromMainActivity = fromMainActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a card layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_video_card, parent, false);
        // populate the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mPlaylistVideos.size() == 0) {
            return;
        }

        final Video video = mPlaylistVideos.get(position);
        final VideoSnippet videoSnippet = video.getSnippet();
        final VideoContentDetails videoContentDetails = video.getContentDetails();
        final VideoStatistics videoStatistics = video.getStatistics();





        holder.mTitleText.setText(videoSnippet.getTitle());
        holder.mDescriptionText.setText(videoSnippet.getDescription());
        holder.itemView.setBackgroundColor(WHITE);

        // load the video thumbnail image
        Picasso.with(holder.mContext)
                .load(videoSnippet.getThumbnails().getHigh().getUrl())
                .placeholder(R.drawable.video_placeholder)
                .into(holder.mThumbnailImage);

        // set the click listener to play the video
//        holder.mThumbnailImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.getId())));
//            }
//        });

        // create and set the click listener for both the share icon and share text
//        View.OnClickListener shareClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Watch \"" + videoSnippet.getTitle() + "\" on YouTube");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + video.getId());
//                sendIntent.setType("text/plain");
//                holder.mContext.startActivity(sendIntent);
//            }
//        };
//        holder.mShareIcon.setOnClickListener(shareClickListener);
//        holder.mShareText.setOnClickListener(shareClickListener);

        // set the video duration text
        holder.mDurationText.setText(parseDuration(videoContentDetails.getDuration()));
        // set the video statistics
        holder.mViewCountText.setText(sFormatter.format(videoStatistics.getViewCount()));
        holder.mLikeCountText.setText( (videoSnippet.getPublishedAt() != null) ? new PrettyTimeEx().format(videoSnippet.getPublishedAt()) : "???" );
        holder.mDislikeCountText.setText(sFormatter.format(videoStatistics.getDislikeCount()));
        holder.mPublishedDate.setText( (videoSnippet.getPublishedAt() != null) ? new PrettyTimeEx().format(videoSnippet.getPublishedAt()) : "???" );





        Log.d(TAG, "onBindViewHolder: "+position+" "+holder.getAdapterPosition()+" "+holder.getLayoutPosition()+" ");



        if (mListener != null) {
             final String nextPageToken = mPlaylistVideos.getNextPageToken();
            if (!isEmpty(nextPageToken) && position == mPlaylistVideos.size() - 1) {
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLastItem(position, nextPageToken);
                    }
                });
            }

            if(position == 0){
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onFirsItemReached();
                    }
                });
            }
            if(position>0){
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onMovedFromFirst();
                    }
                });
            }

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        String details = video.getSnippet().getDescription();
                        if (details == null || details.isEmpty()){
                            details = (video.getSnippet().getPublishedAt() != null) ? new PrettyTimeEx().format(video.getSnippet().getPublishedAt()) : "";
                            details += " | "+video.getStatistics().getViewCount()+" views";
                        }

                        mListener.playVideoOnclick(video.getId(),video.getSnippet().getTitle(),details);
                    }
                });
            }
        });

        if(fromMainActivity == true){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            String details = video.getSnippet().getDescription();
                            if (details == null || details.isEmpty()){
                                details = (video.getSnippet().getPublishedAt() != null) ? new PrettyTimeEx().format(video.getSnippet().getPublishedAt()) : "";
                                details += " | "+video.getStatistics().getViewCount()+" views";
                            }




//                            mListener.playVideoOnclick(video.getId(),video.getSnippet().getTitle(),details);
                            mListener.startActivityAndPlay(video.getId(),video.getSnippet().getTitle(),details);
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mPlaylistVideos.size();
    }

    private boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }

    private String parseDuration(String in) {
        boolean hasSeconds = in.indexOf('S') > 0;
        boolean hasMinutes = in.indexOf('M') > 0;

        String s;
        if (hasSeconds) {
            s = in.substring(2, in.length() - 1);
        } else {
            s = in.substring(2, in.length());
        }

        String minutes = "0";
        String seconds = "00";

        if (hasMinutes && hasSeconds) {
            String[] split = s.split("M");
            minutes = split[0];
            seconds = split[1];
        } else if (hasMinutes) {
            minutes = s.substring(0, s.indexOf('M'));
        } else if (hasSeconds) {
            seconds = s;
        }

        // pad seconds with a 0 if less than 2 digits
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return minutes + ":" + seconds;
    }




    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
