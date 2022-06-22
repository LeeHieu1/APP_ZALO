package hcmute.edu.vn.zalo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Test_Acitvity extends AppCompatActivity {

    ImageView btnplay;
    boolean isPlayingRecord = false;
    Chronometer chronometer;
    SeekBar seekBar;
    Handler seekbarHandler;
    Runnable seekbarRunnable;


    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ref = db.child("Test");
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dong_chat_item_left);

        btnplay = findViewById(R.id.circleimg_PageChat_imgOpponent);
        chronometer = findViewById(R.id.chronometer_chat);
        seekBar = findViewById(R.id.seekbar_chat);

//        btnplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPlayingRecord == false) {
//                    btnplay.setImageResource(R.drawable.pause_icon);
//                    playingRecord();
//                    isPlayingRecord = true;
//                } else {
//                    btnplay.setImageResource(R.drawable.play_icon);
//                    isPlayingRecord = false;
//                }
//            }
//        });
    }

    private void playingRecord() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Download/record.mp3";
        String mess = "";
        try {
            byte[] arr = Files.readAllBytes(Paths.get(path));
            mess = Base64.encodeToString(arr, 0);
            byte[] retrive = Base64.decode(mess, 0);
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(retrive);
            fos.close();

            mediaPlayer.reset();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    chronometer.stop();
                }
            });

            seekBar.setMax(mediaPlayer.getDuration());
            seekbarHandler = new Handler();
            seekbarRunnable  = new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    seekbarHandler.postDelayed(this, 500);
                }
            };
            seekbarHandler.postDelayed(seekbarRunnable,0);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//
//
////            HashMap<String, Object> hashMap = new HashMap<>();
////            hashMap.put("test", mess);
////            ref.push().setValue(hashMap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
