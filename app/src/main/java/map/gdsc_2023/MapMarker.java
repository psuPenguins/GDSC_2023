package map.gdsc_2023;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

public class MapMarker {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MapMarker(){}


    private static void getBitmap (Context context, String imageUrl, OnBitmapLoadedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .submit()
                            .get();

                    // Return the Bitmap on the background thread
                    listener.onBitmapLoaded(bitmap);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private interface OnBitmapLoadedListener {
        void onBitmapLoaded(Bitmap bitmap);
    }

    private void hazardLoadOnSucess (GoogleMap mMap, Context context, @NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            // success stuff
            for (QueryDocumentSnapshot document : task.getResult()) {
                GeoPoint geo = (GeoPoint) document.get("location");
                LatLng pin = new LatLng(geo.getLatitude(), geo.getLongitude());
                String link = (String) document.get("image");
                getBitmap(context, link, new OnBitmapLoadedListener() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        mMap.addMarker(new MarkerOptions()
                                .position(pin)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                    }
                });
            }
        } else {
            // death
            Log.i("loadHazard", "Done fucked up");
        }
    }

    public void loadHazard (GoogleMap mMap, Context context) {
        db.collection("Hazards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hazardLoadOnSucess(mMap, context, task);
                    }
                });
    }

    public void addHazard() {} // when we report a new hazard
    public void removeHazard() {} // when we remove a hazard

}
