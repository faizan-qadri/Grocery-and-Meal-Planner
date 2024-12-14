package com.android.groceryandmealplanner.Data;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ConnectionUtils {

    // Method to check if the device has an active network connection
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Method to show a connection error dialog with retry option
    public static void showConnectionErrorDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Connection Error")
                .setMessage("Unable to connect to the internet. Please check your connection.")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Retry logic - check network again
                        if (isNetworkAvailable(context)) {
                            // Network is available, proceed with your operation
                            Toast.makeText(context, "Connection successful!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            // If still no connection, show the dialog again
                            showConnectionErrorDialog(context);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the operation or exit
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
