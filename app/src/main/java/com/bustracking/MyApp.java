package com.bustracking;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {

    public static FirebaseDatabase infoDb;       // Default Info DB
    public static FirebaseDatabase busLocationDb; // BusLocation DB

    @Override
    public void onCreate() {
        super.onCreate();

        // Default Firebase (Info DB)
        infoDb = FirebaseDatabase.getInstance();

        // Second Firebase (BusLocation DB)
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:37711229202:android:95318a9dd39a57e0c1c3cb") // mobilesdk_app_id from BusLocation google-services.json
                .setApiKey("AIzaSyDGGZyUp_lfDQuMLvrkmZF6Mj7z2DEUFlo")                        // api_key from BusLocation google-services.json
                .setDatabaseUrl("https://buslocation-f1fe7-default-rtdb.asia-southeast1.firebasedatabase.app/") // Realtime DB URL
                .build();

        FirebaseApp busApp;
        try {
            busApp = FirebaseApp.initializeApp(this, options, "busApp");
        } catch (IllegalStateException e) {
            busApp = FirebaseApp.getInstance("busApp");
        }

        busLocationDb = FirebaseDatabase.getInstance(busApp);
        busLocationDb.setPersistenceEnabled(true);
    }
}