package com.example.fixit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ConnectionThread extends BroadcastReceiver {
    ConnectivityManager manager;
    private boolean isConnected = false;


    public void checkConnectivity(Context pContext) {

        manager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        System.out.println("ConnectionThread Thread is running");
        if (mobileNetInfo.getState() == NetworkInfo.State.CONNECTED
                || wifiNetInfo.getState() == NetworkInfo.State.CONNECTED) {
            if (!isConnected) {
                isConnected = true;
            }

        } else {
            isConnected = false;
        }


    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkConnectivity(context);
        if (!isConnected) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layoutDialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null);
            builder.setView(layoutDialog);

            Button btnRetry = (Button) layoutDialog.findViewById(R.id.btnRetry);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });

        }
    }
}
