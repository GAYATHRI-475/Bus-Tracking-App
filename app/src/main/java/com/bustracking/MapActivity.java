package com.bustracking;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private WebView mapWebView;
    private DatabaseReference busRef;
    private String busNumber;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapWebView = findViewById(R.id.mapWebView);
        mapWebView.getSettings().setJavaScriptEnabled(true);
        mapWebView.getSettings().setDomStorageEnabled(true);
        mapWebView.setWebViewClient(new WebViewClient());

        // ✅ Load local map.html
        mapWebView.loadUrl("file:///android_asset/map.html");

        // ✅ Get assigned bus number from StudentActivity
        busNumber = getIntent().getStringExtra("busNumber");
        if (busNumber == null || busNumber.isEmpty()) {
            Toast.makeText(this, "No bus assigned", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Reference in Firebase: BusLocations/busNumber
        busRef = FirebaseDatabase.getInstance().getReference("BusLocations").child(busNumber);

        // ✅ Listen for real-time updates
        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Double lat = snapshot.child("latitude").getValue(Double.class);
                    Double lng = snapshot.child("longitude").getValue(Double.class);

                    if(lat != null && lng != null){
                        // Update marker on map
                        String js = "moveBus(" + lat + "," + lng + ")";
                        mapWebView.evaluateJavascript(js, null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });
    }
}