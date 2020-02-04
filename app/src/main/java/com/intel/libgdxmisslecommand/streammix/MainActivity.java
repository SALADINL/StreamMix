package com.intel.libgdxmisslecommand.streammix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Shodream";

    private ImageButton btnReco;
    private Recorder recorder = new Recorder();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        mkdirTest();

        btnReco = findViewById(R.id.btn_reco);

        btnReco.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "down");
                        recorder.startRecording();
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "up");
                        recorder.stopRecording();
                        return true; // if you want to handle the touch event
                }
                return false;
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

    private static File getAbsoluteDir(Context ctx, String optionalPath) {
        String rootPath;
        if (optionalPath != null && !optionalPath.equals("")) {
            rootPath = ctx.getExternalFilesDir(optionalPath).getAbsolutePath();
        } else {
            rootPath = ctx.getExternalFilesDir(null).getAbsolutePath();
        }
        // extraPortion is extra part of file path
        String extraPortion = "Android/data/" + BuildConfig.APPLICATION_ID
                + File.separator + "files" + File.separator;
        // Remove extraPortion
        rootPath = rootPath.replace(extraPortion, "");
        return new File(rootPath);
    }

    private void mkdirTest()
    {
        File rep = new File(Environment.getExternalStorageDirectory().getPath(), "AudioRecorder");



        if (!rep.exists()) {
            boolean success = rep.mkdirs();
            Log.d(TAG,rep.getPath());
        }


    }
}
