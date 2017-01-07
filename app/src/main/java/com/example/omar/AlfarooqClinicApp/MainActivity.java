package com.example.omar.AlfarooqClinicApp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.appcompat.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


public class MainActivity extends AppCompatActivity {

    private static final String DOCTOR_AVAILABLE = "doctor_available";
    private static final String OPEN_THIS_WEEK = "open_this_week";

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private TextView doctorTextView;
    private TextView clinicOpenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doctorTextView = (TextView) findViewById(R.id.doctorTextView);
        clinicOpenTextView = (TextView) findViewById(R.id.clinicOpen);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //TODO: May want to enable debug mode to test multiple config values
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        //Set the default values
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchClinicData();
    }

    private void fetchClinicData() {
        int cacheExpiration = 3600; // 1 hour in seconds
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        boolean isDeveloperModeEnabled = firebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled();
        if (isDeveloperModeEnabled) {
            cacheExpiration = 0;
        }
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Data Successfully Fetched",
                                    Toast.LENGTH_SHORT).show();
                            // Not too sure what this does
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Data Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayData();
                    }
                });
    }

    private void displayData() {
        String doctorAvailable = firebaseRemoteConfig.getString(DOCTOR_AVAILABLE);
        String clinicOpen = firebaseRemoteConfig.getString(OPEN_THIS_WEEK);
        Log.d("mytag", doctorAvailable);
        Log.d("mytag", clinicOpen);
        doctorTextView.setText(doctorAvailable);
        clinicOpenTextView.setText(clinicOpen);
    }

    //TODO: Update phone number
    public void callNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234567890"));
        Button callButton = (Button) findViewById(R.id.callButton);
        startActivity(intent);
    }

    //TODO: Update email address
    public void sendEmail(View view) {
        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:example@gmail.com"));
        startActivity(sendEmailIntent);
    }
}
