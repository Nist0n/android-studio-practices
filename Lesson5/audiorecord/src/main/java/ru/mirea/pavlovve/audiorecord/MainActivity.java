package ru.mirea.pavlovve.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import ru.mirea.pavlovve.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;

    private final String TAG =
            MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private boolean isWork;

    private String recordFilePath = null;

    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    private boolean isStartRecording = true;

    private boolean isStartPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        checkPermissions();

        binding.playButton.setEnabled(false);

        recordFilePath =
                (
                        new File(
                                getExternalFilesDir(
                                        Environment.DIRECTORY_MUSIC
                                ),
                                "audiorecordtest.3gp"
                        )
                ).getAbsolutePath();

        binding.recordButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (isStartRecording) {

                            startRecording();

                            binding.recordButton.setText(
                                    "Stop recording"
                            );

                            binding.playButton.setEnabled(false);

                        } else {

                            stopRecording();

                            binding.recordButton.setText(
                                    "Start recording"
                            );

                            binding.playButton.setEnabled(true);
                        }

                        isStartRecording =
                                !isStartRecording;
                    }
                });

        binding.playButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (isStartPlaying) {

                            startPlaying();

                            binding.playButton.setText(
                                    "Stop playing"
                            );

                            binding.recordButton.setEnabled(false);

                        } else {

                            stopPlaying();

                            binding.playButton.setText(
                                    "Start playing"
                            );

                            binding.recordButton.setEnabled(true);
                        }

                        isStartPlaying =
                                !isStartPlaying;
                    }
                });
    }

    private void checkPermissions() {

        int audioPermissionStatus =
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                );

        if (audioPermissionStatus ==
                PackageManager.PERMISSION_GRANTED) {

            isWork = true;

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    REQUEST_CODE_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_CODE_PERMISSION) {

            isWork =
                    grantResults.length > 0
                            &&
                            grantResults[0]
                                    == PackageManager.PERMISSION_GRANTED;

            if (!isWork) {
                finish();
            }
        }
    }

    private void startRecording() {

        if (!isWork) {
            return;
        }

        recorder = new MediaRecorder();

        recorder.setAudioSource(
                MediaRecorder.AudioSource.MIC
        );

        recorder.setOutputFormat(
                MediaRecorder.OutputFormat.THREE_GPP
        );

        recorder.setOutputFile(recordFilePath);

        recorder.setAudioEncoder(
                MediaRecorder.AudioEncoder.AMR_NB
        );

        try {

            recorder.prepare();

            recorder.start();

        } catch (IOException e) {

            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopRecording() {

        recorder.stop();

        recorder.release();

        recorder = null;
    }

    private void startPlaying() {

        player = new MediaPlayer();

        try {

            player.setDataSource(recordFilePath);

            player.prepare();

            player.start();

        } catch (IOException e) {

            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {

        player.release();

        player = null;
    }
}