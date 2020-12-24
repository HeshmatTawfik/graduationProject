package com.heshmat.doctoreta.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.heshmat.doctoreta.LoadingDialog;
import com.heshmat.doctoreta.R;
import com.heshmat.doctoreta.models.Doctor;
import com.heshmat.doctoreta.models.User;
import com.heshmat.doctoreta.services.FirebaseFunction;

import java.util.HashMap;
import java.util.Objects;

public class PatientVideoCallActivity extends AppCompatActivity implements View.OnTouchListener {
    private RtcEngine mRtcEngine;
    private static final String LOG_TAG = "VIDEO_CALL";
    private String remoteUserId;
    float dX;
    float dY;
    int lastAction;
    ConstraintLayout constraintLayout;


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set first remote user to the main bg video container
                    setupRemoteVideoStream(uid);
                }
            });
        }

        // remote user has left channel
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        // remote user has toggled their video
        @Override
        public void onUserMuteVideo(final int uid, final boolean toggle) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoToggle(uid, toggle);
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(LOG_TAG, "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.i(LOG_TAG, "error numb: " + (err & 0xFFFFFFFFL));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_video_call);
        Intent intent = getIntent();

        remoteUserId = intent.getStringExtra("ID");
        constraintLayout = findViewById(R.id.patientVideoCallC);
        View view = findViewById(R.id.floating_video_container);
        view.setOnTouchListener(this);
        Log.i(LOG_TAG, "patientVideoCAllChannel : " + remoteUserId + "_" + User.currentLoggedUser.getId());
        initAgoraEngine();
        findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.GONE);
    }

    private void initAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        setupSession();
    }

    private void setupSession() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideoFeed() {

        // setup the container for the local user
        FrameLayout videoContainer = findViewById(R.id.floating_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoSurface.setZOrderMediaOverlay(true);
        videoContainer.addView(videoSurface);
        mRtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void setupRemoteVideoStream(int uid) {
        // setup ui element for the remote stream
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);
        // ignore any new streams that join the session
        if (videoContainer.getChildCount() >= 1) {
            return;
        }

        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoContainer.addView(videoSurface);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, uid));
        mRtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);

    }

    public void onAudioMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.audio_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.audio_toggle_active_btn);
        }

        mRtcEngine.muteLocalAudioStream(btn.isSelected());

    }

    public void onVideoMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.video_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.video_toggle_active_btn);
        }

        mRtcEngine.muteLocalVideoStream(btn.isSelected());

        FrameLayout container = findViewById(R.id.floating_video_container);
        container.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
        SurfaceView videoSurface = (SurfaceView) container.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!btn.isSelected());
        videoSurface.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onjoinChannelClicked(View view) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("clientId", User.currentLoggedUser.getId());
        hashMap.put("doctorId", remoteUserId);
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        Task<HashMap<String, Object>> tokenTask = FirebaseFunction.callFirebaseFunction("createAgoraToken", hashMap);
        tokenTask.addOnSuccessListener(new OnSuccessListener<HashMap<String, Object>>() {
            @Override
            public void onSuccess(HashMap<String, Object> stringObjectHashMap) {
                loadingDialog.dismissDialog();
                if (stringObjectHashMap != null && stringObjectHashMap.containsKey("result") && stringObjectHashMap.get("result").equals("SUCCESSFUL")) {
                    String token = Objects.requireNonNull(stringObjectHashMap.get("token")).toString();
                    if (!token.isEmpty()) {

                        mRtcEngine.joinChannel(token, remoteUserId + "_" + User.currentLoggedUser.getId(), "Extra Optional Data", 0); // if you do not specify the uid, Agora will assign one.
                        Log.i(LOG_TAG, "patient: " + String.format("%s_%s", remoteUserId, User.currentLoggedUser.getId()));

                        setupLocalVideoFeed();
                        findViewById(R.id.joinBtn).setVisibility(View.GONE); // set the join button hidden
                        findViewById(R.id.audioBtn).setVisibility(View.VISIBLE); // set the audio button hidden
                        findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE); // set the leave button hidden
                        findViewById(R.id.videoBtn).setVisibility(View.VISIBLE); // set the video button hidden
                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
                new AlertDialog.Builder(PatientVideoCallActivity.this).setMessage(e.getMessage()).show();
            }
        });


    }

    public void onLeaveChannelClicked(View view) {
        leaveChannel();
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
        findViewById(R.id.joinBtn).setVisibility(View.VISIBLE); // set the join button visible
        findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
        findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
        findViewById(R.id.videoBtn).setVisibility(View.GONE); // set the video button hidden
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    private void removeVideo(int containerID) {
        FrameLayout videoContainer = findViewById(containerID);
        videoContainer.removeAllViews();
    }

    private void onRemoteUserVideoToggle(int uid, boolean toggle) {
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);

        SurfaceView videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setVisibility(toggle ? View.GONE : View.VISIBLE);

        // add an icon to let the other user know remote video has been disabled
        if (toggle) {
            ImageView noCamera = new ImageView(this);
            noCamera.setImageResource(R.drawable.video_disabled);
            videoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) videoContainer.getChildAt(1);
            if (noCamera != null) {
                videoContainer.removeView(noCamera);
            }
        }
    }

    private void onRemoteUserLeft() {
        removeVideo(R.id.bg_video_container);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int width = v.getLayoutParams().width;
        ;
        int height = v.getLayoutParams().height;
        int windowWidth = constraintLayout.getWidth();
        int windowHeight = constraintLayout.getHeight();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;


                return true;

            case MotionEvent.ACTION_MOVE:
                lastAction = MotionEvent.ACTION_MOVE;


                if (width == windowWidth && height == windowHeight-65) {
                } else {
                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();

                    if (event.getRawX() + dX + width > windowWidth) {
                        v.animate()
                                .x(windowWidth - width)
                                .setDuration(0)
                                .start();
                    }
                    if (event.getRawX() + dX < 0) {
                        v.animate()
                                .x(0)
                                .setDuration(0)
                                .start();
                    }
                    if (event.getRawY() + dY + height > windowHeight) {
                        v.animate()
                                .y(windowHeight - height)
                                .setDuration(0)
                                .start();
                    }
                    if (event.getRawY() + dY < 0) {
                        v.animate()
                                .y(0)
                                .setDuration(0)
                                .start();
                    }

                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }
}
