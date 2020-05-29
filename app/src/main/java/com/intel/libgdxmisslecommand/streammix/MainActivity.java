package com.intel.libgdxmisslecommand.streammix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements CallbackServer {

    private static final String TAG = "Shodream";

        private Context context = this;
        private ImageButton btnReco;
        private TextView title_info;
        private ConstraintLayout mainLayout;
        private VideoView videoView;
        private ImageView info;
        private Recorder recorder = new Recorder(this, getDirectory());
        private RequestServeur requestServeur;
        private HashMap<String,String> param = new HashMap<>();
        private CircleLineVisualizer circleLineVisualizer;

        private SimpleExoPlayer simpleExoPlayer;

        private IceApp _app;


        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        _app = (IceApp) getApplication();

        _app.setHost("192.168.43.194");
        _app.setDeliveryMode(DeliveryMode.TWOWAY);

        //initStream();

        checkPermissions();

        info = findViewById(R.id.info);
        title_info = findViewById(R.id.title);
        videoView = null;
        btnReco = findViewById(R.id.btn_reco);
        mainLayout = findViewById(R.id.mainLayout);
        circleLineVisualizer = findViewById(R.id.blast);

        final TransitionDrawable transition = (TransitionDrawable) mainLayout.getBackground();

        requestServeur = new RequestServeur(this,this);

        btnReco.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recorder.startRecording();
                        btnReco.setSelected(true);
                        transition.startTransition(500);
                        Log.d(TAG, "down");

                        return true; // if you want to handle the touch event²
                    case MotionEvent.ACTION_UP:
                        btnReco.setSelected(false);
                        transition.reverseTransition(1000);
                        Log.d(TAG, "up");
                        recorder.stopRecording("tmp.wav");
                        param.put("wav",Encode.getEncode(getDirectory()+"/tmp.wav"));
                        requestServeur.sendHttpsRequest(param,RequestServeur.PORT_RECO);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView textView = new TextView(context);
                textView.append("- Waka-Waka \n" +
                                "- Babygirl \n" +
                                "- FeelGood \n\n" +
                                " Auteur : \n" +
                                "Léo Moracchioli");
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                new cn.pedant.SweetAlert.SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Liste des musiques disponibles :")
                        .setCustomView(textView)
                        .show();
            }
        });
    }


    private void checkPermissions()
    {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
    private String getDirectory()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/StreamMix");

        if(!file.exists()) {
            file.mkdir();
        }

        return file.getAbsolutePath();
    }
    @Override
    public void executeAfterResponseServer(final String response, final int idServer)
    {
        switch (idServer) {
            case 0 : {
                param.clear();
                param.put("input", response);
                requestServeur.sendHttpsRequest(param,RequestServeur.PORT_CMD);
            }
            case 1 : {
                if(_app != null) executeIce(response);
            }
        }
    }

    @Override
    public void exercuceAfterErrorServer(String error)
    {

        SweetAlertDialog sDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        sDialog.setTitleText("Oups ...");
        sDialog.setContentText(error);
        sDialog.setConfirmText("Ok");
        sDialog.setCancelable(false);
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        sDialog.show();
    }

    private void initStream(String music) {
        String myUri = "http://192.168.43.194:3200/"+music;
            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);

                simpleExoPlayer.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if(playbackState == SimpleExoPlayer.STATE_READY) {
                            System.out.println("audioSession => "+simpleExoPlayer.getAudioSessionId());
                            circleLineVisualizer.setAudioSessionId(simpleExoPlayer.getAudioSessionId());
                            circleLineVisualizer.setDrawLine(true);
                        }
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }
                });

                Uri uri = Uri.parse(myUri);
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_audio");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(uri,dataSourceFactory,extractorsFactory,null,null);

                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.setPlayWhenReady(true);

            } catch (Exception e) {}





    }
    private void executeIce(String response) {

        int cmd = -1;
        String music = "";

        try {
            JSONObject json = new JSONObject(response);
            cmd = (int) json.get("cmd");
            music = (String) json.get("music");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (cmd) {
                case 0 : {
                    _app.addMusic(music);
                    _app.playMusic();
                    title_info.setText(music);
                    initStream(music);
                }break;
                case 1 : {
                    _app.removeMusic();
                    title_info.setText("Aucune musique en cours");
                }break;
                case 2 : {
                    _app.pauseMusic();

                    String s = title_info.getText().toString();

                    if(title_info.getText().toString().contains("(Pause)")) {
                        s = s.substring(0, s.indexOf("("));
                        title_info.setText(s);
                    }else {
                        title_info.setText(title_info.getText().toString().concat("(Pause)"));
                    }
                }break;
            }
    }
}
