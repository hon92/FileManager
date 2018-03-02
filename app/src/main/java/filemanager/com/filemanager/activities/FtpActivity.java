package filemanager.com.filemanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.services.FtpService;

/**
 * Created by Honza on 08.12.2017.
 */

public class FtpActivity extends AppCompatActivity {
    private Button startServerButton;
    private boolean ftpServiceRunning = false;
    private Intent ftpServerIntent;
    private int ftpPort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftp_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        startServerButton = findViewById(R.id.startServerButton);
        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ftpServiceRunning) {
                    startFtpService();
                } else {
                    stopFtpService();
                }
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ftpPort = Integer.parseInt(sharedPreferences.getString("ftp_port", "2000"));

        final TextView addressTextView = findViewById(R.id.addressTextView);
        WifiManager wifiManager = getSystemService(WifiManager.class);
        if (wifiManager.isWifiEnabled() && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            String addressText = String.format("ftp://%s:%d", getIpAddress(ipAddress), ftpPort);
            addressTextView.setText(addressText);
        } else {
            addressTextView.setText("You must be connected to internet via WIFI");
            startServerButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ftpServiceRunning) {
            stopFtpService();
        }
    }

    private void startFtpService() {
        ftpServiceRunning = true;
        startServerButton.setText("Stop");
        ftpServerIntent = new Intent(getBaseContext(), FtpService.class);
        ftpServerIntent.putExtra("ftpPort", ftpPort);
        startService(ftpServerIntent);
        Toast.makeText(getBaseContext(), "Ftp server started", Toast.LENGTH_LONG).show();
    }

    private void stopFtpService() {
        ftpServiceRunning = false;
        startServerButton.setText("Start");
        stopService(ftpServerIntent);
        ftpServerIntent = null;
        Toast.makeText(getBaseContext(), "Ftp server was stopped", Toast.LENGTH_LONG).show();
    }

    private String getIpAddress(int ipAddress) {
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    }
}
