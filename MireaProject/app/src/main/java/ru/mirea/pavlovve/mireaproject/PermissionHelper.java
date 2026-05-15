package ru.mirea.pavlovve.mireaproject;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public final class PermissionHelper {

    private PermissionHelper() {
    }

    public static boolean isGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean shouldShowRationale(Fragment fragment, String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }
}
