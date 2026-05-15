package ru.mirea.pavlovve.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class BackgroundWorkFragment extends Fragment {

    private static final String WORK_TAG = "background_upload";

    private TextView statusText;
    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background_work, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusText = view.findViewById(R.id.textWorkStatus);
        startButton = view.findViewById(R.id.buttonStartWork);

        startButton.setOnClickListener(v -> enqueueBackgroundWork());

        WorkManager.getInstance(requireContext())
                .getWorkInfosByTagLiveData(WORK_TAG)
                .observe(getViewLifecycleOwner(), workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) {
                        statusText.setText(R.string.work_status_idle);
                        startButton.setEnabled(true);
                        return;
                    }
                    WorkInfo workInfo = workInfos.get(0);
                    statusText.setText(workStatusMessage(workInfo.getState()));
                    startButton.setEnabled(
                            workInfo.getState() != WorkInfo.State.RUNNING
                                    && workInfo.getState() != WorkInfo.State.ENQUEUED);
                });
    }

    private void enqueueBackgroundWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .addTag(WORK_TAG)
                        .build();

        WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest);
        statusText.setText(R.string.work_status_enqueued);
        startButton.setEnabled(false);
    }

    private String workStatusMessage(WorkInfo.State state) {
        if (state == WorkInfo.State.ENQUEUED) {
            return getString(R.string.work_status_enqueued);
        }
        if (state == WorkInfo.State.RUNNING) {
            return getString(R.string.work_status_running);
        }
        if (state == WorkInfo.State.SUCCEEDED) {
            return getString(R.string.work_status_finished);
        }
        if (state == WorkInfo.State.FAILED) {
            return getString(R.string.work_status_failed);
        }
        if (state == WorkInfo.State.CANCELLED) {
            return getString(R.string.work_status_cancelled);
        }
        return getString(R.string.work_status_idle);
    }
}
