package com.eastrivervillage.nasaepic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener, TestFragmen.OnFragmentInteractionListener{

    public static final String TAG = "MainActivity";

    public static final int ALLOW_INTERNET_DIALOG_CODE = 1;
    public static final int NO_INTERNET_EXIT_CODE = 2;
    public static final int ALLOW_INTERNET_STATUS_DIALOG_CODE = 3;
    public static final int NO_INTERNET_STATUS_EXIT_CODE = 4;
    public static final int ALLOW_INTERNET_REQUEST_CODE = 1270;
    public static final int ALLOW_NETWORK_STATE_REQUEST_CODE = 1271;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForInternetPermission();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void checkForInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            /* Need to provide an explanation */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                showDialog("Permission", "Internet permission is required to access images from NASA",
                        android.R.drawable.ic_dialog_info, "Allow", "Deny", ALLOW_INTERNET_DIALOG_CODE);
            } else {
                requestInternetPermission();
            }
        } else {
            checkNetworkStatePermission();
        }
    }

    public void checkNetworkStatePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                showDialog("Permission", "Access to Network Status watch is required to monitor the Internet status",
                        android.R.drawable.ic_dialog_info, "Allow", "Deny", ALLOW_INTERNET_STATUS_DIALOG_CODE);
            } else {
                requestNetworkStatusPermission();
            }
        } else {
            loadNasaData();
        }
    }

    public void showDialog(String title, String msg, int icon, String positiveStr, String negativeStr,
                           final int callbackCode) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(icon)
                .setPositiveButton(positiveStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCallback(callbackCode, true);
                    }
                })
                .setNegativeButton(negativeStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCallback(callbackCode, false);
                    }
                })
                .show();
    }

    public void dialogCallback(int code, boolean success) {
        switch (code) {
            case ALLOW_INTERNET_DIALOG_CODE:
                if (success) {
                    requestInternetPermission();
                } else {
                    showNoInternetExitDialog();
                }
                break;

            case ALLOW_INTERNET_STATUS_DIALOG_CODE:
                if (success) {
                    requestNetworkStatusPermission();
                } else {
                    showNoNetworkStatusExitDialog();
                }
                break;

            case NO_INTERNET_EXIT_CODE:
                if (success) {
                    checkForInternetPermission();
                } else {
                    System.exit(0);
                }
                break;

            case NO_INTERNET_STATUS_EXIT_CODE:
                if (success) {
                    checkForInternetPermission();
                } else {
                    System.exit(0);
                }
        }
    }

    public void showNoInternetExitDialog() {
        showDialog("Permission", "App cannot proceed without Internet permission",
                android.R.drawable.ic_dialog_alert, "Allow", "Exit", NO_INTERNET_EXIT_CODE);
    }

    public void showNoNetworkStatusExitDialog() {
        showDialog("Permission", "App cannot proceed without access to Network Status permission",
                android.R.drawable.ic_dialog_alert, "Allow", "Exit", NO_INTERNET_STATUS_EXIT_CODE);
    }

    public void requestInternetPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                ALLOW_INTERNET_REQUEST_CODE);
    }

    public void requestNetworkStatusPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                ALLOW_NETWORK_STATE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALLOW_INTERNET_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Permission granted for Internet access */
                    checkNetworkStatePermission();
                } else {
                    showNoInternetExitDialog();
                }
                break;

            case ALLOW_NETWORK_STATE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Permission Granted for Network state */
                    loadNasaData();
                } else {
                    showNoNetworkStatusExitDialog();
                }
                break;
        }
    }

    public void loadNasaData() {
        Log.e(TAG, "loadNasaData");
    }
}
