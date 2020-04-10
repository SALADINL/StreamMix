package com.intel.libgdxmisslecommand.streammix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Shodream";

        private Context context = this;
        private ImageButton btnReco;
        private ConstraintLayout mainLayout;
        private VideoView videoView;
        private ImageView info;
        private Recorder recorder = new Recorder(this, getDirectory());
        private RequestServeur requestServeur;
        private HashMap<String,String> param = new HashMap<>();

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        info = findViewById(R.id.info);
        videoView = findViewById(R.id.video);
        btnReco = findViewById(R.id.btn_reco);
        mainLayout = findViewById(R.id.mainLayout);
        final TransitionDrawable transition = (TransitionDrawable) mainLayout.getBackground();

        requestServeur = new RequestServeur(this,RequestServeur.PORT_RECO);

        btnReco.setOnTouchListener(new View.OnTouchListener() {
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
                        requestServeur.sendHttpsRequest(param);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView textView = new TextView(context);
                textView.append("- Thunderstruck : ACDC \n" +
                                "- Bohemian Rhapsody : Queen");
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
}
