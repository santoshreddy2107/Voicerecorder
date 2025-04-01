package com.harman.voiceprojest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath = null;
    private Button recordButton, stopButton, replayButton;
    private TextView audioFilePathText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        replayButton = findViewById(R.id.replayButton);
        audioFilePathText = findViewById(R.id.audioFilePathText);

        stopButton.setEnabled(false);
        replayButton.setEnabled(false);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayRecording();
            }
        });
    }


    private void startRecording() {
        try {
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            } else {
                mediaRecorder.reset();
            }

            // i am using Environment.getExternalStorageDirectory() for SD card path
            String audioFileName = "/voice_clip.mp4";


            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                audioFilePath = Environment.getExternalStorageDirectory() + "/VoiceProjest" + audioFileName;


                File directory = new File(Environment.getExternalStorageDirectory() + "/VoiceProjest");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            } else {
                Toast.makeText(this, "External Storage not available", Toast.LENGTH_SHORT).show();
                return;
            }


            if (audioFilePath == null || audioFilePath.isEmpty()) {
                Toast.makeText(this, "Error: Invalid file path", Toast.LENGTH_SHORT).show();
                return;
            }

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFilePath);

            mediaRecorder.prepare();
            mediaRecorder.start();

            recordButton.setEnabled(false);
            stopButton.setEnabled(true);
            replayButton.setEnabled(false);
            audioFilePathText.setText("Audio File Path: " + audioFilePath);
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting recording", Toast.LENGTH_SHORT).show();
        }
    }


    private void stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
                replayButton.setEnabled(true);
                Toast.makeText(this, "Recording saved at: " + audioFilePath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No recording in progress", Toast.LENGTH_SHORT).show();
            }
        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }
    }


    private void replayRecording() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset(); // Reset if already initialized
            }

            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Playing the recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing recording", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions required to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
