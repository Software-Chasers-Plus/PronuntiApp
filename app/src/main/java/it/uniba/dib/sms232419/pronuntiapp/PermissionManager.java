package it.uniba.dib.sms232419.pronuntiapp;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final int PERMISSION_REQUEST_CODE = 100;

    public interface PermissionListener {
        void onPermissionsGranted();

        void onPermissionsDenied();
    }

    public static void requestPermissions(Fragment fragment, String[] permissions, PermissionListener listener) {
        List<String> pendingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (fragment.requireContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                pendingPermissions.add(permission);
            }
        }
        if (!pendingPermissions.isEmpty()) {
            fragment.requestPermissions(pendingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            listener.onPermissionsGranted();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionListener listener) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                listener.onPermissionsGranted();
            } else {
                listener.onPermissionsDenied();
            }
        }
    }
}
