package ru.mirea.pavlovve.mireaproject;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class MicrophoneFragment extends Fragment {

    private TextView permissionHint;
    private TextView statusText;
    private ProgressBar levelBar;
    private Button recordButton;
    private Button playButton;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File audioFile;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRecording;

    private final ActivityResultLauncher<String> audioPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    permissionHint.setText(R.string.microphone_permission_granted);
                } else {
                    permissionHint.setText(R.string.microphone_permission_denied);
                    if (PermissionHelper.shouldShowRationale(this, Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(requireContext(), R.string.microphone_permission_rationale, Toast.LENGTH_LONG).show();
                    }
                }
            });

    private final Runnable levelUpdater = new Runnable() {
        @Override
        public void run() {
            if (!isRecording || mediaRecorder == null) {
                return;
            }
            try {
                int amplitude = mediaRecorder.getMaxAmplitude();
                int level = Math.min(100, amplitude / 1000);
                levelBar.setProgress(level);
                statusText.setText(getString(R.string.microphone_recording_level, level));
            } catch (IllegalStateException ignored) {
            }
            handler.postDelayed(this, 100);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_microphone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionHint = view.findViewById(R.id.textMicrophonePermission);
        statusText = view.findViewById(R.id.textMicrophoneStatus);
        levelBar = view.findViewById(R.id.progressAudioLevel);
        recordButton = view.findViewById(R.id.buttonRecord);
        playButton = view.findViewById(R.id.buttonPlay);

        recordButton.setOnClickListener(v -> toggleRecording());
        playButton.setOnClickListener(v -> playRecording());

        updatePermissionHint();
    }

    private void toggleRecording() {
        if (!ensureAudioPermission()) {
            return;
        }
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private boolean ensureAudioPermission() {
        if (PermissionHelper.isGranted(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            return true;
        }
        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        return false;
    }

    private void startRecording() {
        releasePlayer();
        try {
            audioFile = File.createTempFile("voice_note_", ".m4a", requireContext().getCacheDir());
            mediaRecorder = createMediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            recordButton.setText(R.string.microphone_stop);
            playButton.setEnabled(false);
            statusText.setText(R.string.microphone_recording);
            handler.post(levelUpdater);
        } catch (IOException | IllegalStateException e) {
            Toast.makeText(requireContext(), R.string.microphone_record_error, Toast.LENGTH_SHORT).show();
            releaseRecorder();
        }
    }

    private void stopRecording() {
        handler.removeCallbacks(levelUpdater);
        isRecording = false;
        recordButton.setText(R.string.microphone_record);
        levelBar.setProgress(0);

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException ignored) {
            }
        }
        releaseRecorder();

        statusText.setText(R.string.microphone_recorded);
        playButton.setEnabled(audioFile != null && audioFile.exists());
    }

    private void playRecording() {
        if (audioFile == null || !audioFile.exists()) {
            return;
        }
        releasePlayer();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> statusText.setText(R.string.microphone_playback_done));
            mediaPlayer.start();
            statusText.setText(R.string.microphone_playing);
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.microphone_play_error, Toast.LENGTH_SHORT).show();
        }
    }

    private MediaRecorder createMediaRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new MediaRecorder(requireContext());
        }
        return new MediaRecorder();
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updatePermissionHint() {
        if (PermissionHelper.isGranted(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            permissionHint.setText(R.string.microphone_permission_granted);
        } else {
            permissionHint.setText(R.string.microphone_permission_required);
        }
    }

    @Override
    public void onStop() {
        if (isRecording) {
            stopRecording();
        }
        releasePlayer();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(levelUpdater);
        releaseRecorder();
        releasePlayer();
        super.onDestroyView();
    }
}
