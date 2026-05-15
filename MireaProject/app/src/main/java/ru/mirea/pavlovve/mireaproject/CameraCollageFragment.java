package ru.mirea.pavlovve.mireaproject;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class CameraCollageFragment extends Fragment {

    private static final int SLOT_LEFT = 0;
    private static final int SLOT_RIGHT = 1;

    private ImageView photoLeft;
    private ImageView photoRight;
    private TextView permissionHint;
    private Button captureLeftButton;
    private Button captureRightButton;

    private int activeSlot = SLOT_LEFT;
    private Uri[] photoUris = new Uri[2];

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchCamera();
                } else {
                    permissionHint.setText(R.string.camera_permission_denied);
                    if (PermissionHelper.shouldShowRationale(this, Manifest.permission.CAMERA)) {
                        Toast.makeText(requireContext(), R.string.camera_permission_rationale, Toast.LENGTH_LONG).show();
                    }
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (!success || photoUris[activeSlot] == null) {
                    return;
                }
                ImageView target = activeSlot == SLOT_LEFT ? photoLeft : photoRight;
                target.setImageURI(photoUris[activeSlot]);
                target.setScaleType(ImageView.ScaleType.CENTER_CROP);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_collage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoLeft = view.findViewById(R.id.imageCollageLeft);
        photoRight = view.findViewById(R.id.imageCollageRight);
        permissionHint = view.findViewById(R.id.textCameraPermission);
        captureLeftButton = view.findViewById(R.id.buttonCaptureLeft);
        captureRightButton = view.findViewById(R.id.buttonCaptureRight);

        captureLeftButton.setOnClickListener(v -> requestPhoto(SLOT_LEFT));
        captureRightButton.setOnClickListener(v -> requestPhoto(SLOT_RIGHT));

        updatePermissionHint();
    }

    private void requestPhoto(int slot) {
        activeSlot = slot;
        if (PermissionHelper.isGranted(requireContext(), Manifest.permission.CAMERA)) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = File.createTempFile(
                    "collage_" + activeSlot + "_",
                    ".jpg",
                    requireContext().getCacheDir());
            photoUris[activeSlot] = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoUris[activeSlot]);
            permissionHint.setText(R.string.camera_permission_granted);
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.camera_capture_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePermissionHint() {
        if (PermissionHelper.isGranted(requireContext(), Manifest.permission.CAMERA)) {
            permissionHint.setText(R.string.camera_permission_granted);
        } else {
            permissionHint.setText(R.string.camera_permission_required);
        }
    }
}
