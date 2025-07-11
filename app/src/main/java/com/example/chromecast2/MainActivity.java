package com.example.chromecast2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView =  findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        String account = getString(R.string.sdk_demo_account);
        MediaRouteButton mMediaRouteButton =  findViewById(R.id.media_route_button);

        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);

        CastContext mCastContext = CastContext.getSharedInstance(this, ContextCompat.getMainExecutor(this)).getResult();
        SessionManager mSessionManager = mCastContext.getSessionManager();
        mSessionManager.addSessionManagerListener(new SessionManagerListener<>() {
            @Override
            public void onSessionEnded(@NonNull Session session, int i) {
                Log.d("CastSession", "onSessionEnded: " + session.getSessionId() + ", reason: " + i);
            }

            @Override
            public void onSessionEnding(@NonNull Session session) {
                Log.d("CastSession", "onSessionEnding: " + session.getSessionId());
            }

            @Override
            public void onSessionResumeFailed(@NonNull Session session, int i) {
                Log.d("CastSession", "onSessionResumeFailed: " + session.getSessionId() + ", errorCode: " + i);
            }

            @Override
            public void onSessionResumed(@NonNull Session session, boolean b) {
                Log.d("CastSession", "onSessionResumed: " + session.getSessionId() + ", wasSuspended: " + b);
            }

            @Override
            public void onSessionResuming(@NonNull Session session, @NonNull String s) {
                Log.d("CastSession", "onSessionResuming: sessionId=" + session.getSessionId() + ", routeId=" + s);
            }

            @Override
            public void onSessionStartFailed(@NonNull Session session, int i) {
                Log.d("CastSession", "onSessionStartFailed: " + session.getSessionId() + ", errorCode: " + i);
            }

            @Override
            public void onSessionStarted(@NonNull Session session, @NonNull String s) {
                Log.d("CastSession", "onSessionStarted: sessionId=" + session.getSessionId() + ", routeId=" + s);
            }

            @Override
            public void onSessionStarting(@NonNull Session session) {
                Log.d("CastSession", "onSessionStarting: " + session.getSessionId());
            }

            @Override
            public void onSessionSuspended(@NonNull Session session, int i) {
                Log.d("CastSession", "onSessionSuspended: " + session.getSessionId() + ", reason: " + i);
            }
        });

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(
                getApplicationContext(),
                menu,
                R.id.media_route_menu_item
        );
        return true;
    }
}
