package com.heshmat.doctoreta.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RequestPermissions {
    public static void requestMultiplePermissions(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET)
                .withListener(
                        new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    Toast.makeText(getApplicationContext(),
                                            "All permissions are granted by user!",
                                            Toast.LENGTH_SHORT).show();
                                }

//                      check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // show alert dialog navigating to Settings

                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(
                                    List<PermissionRequest> permissions,
                                    PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                  //      Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

}
