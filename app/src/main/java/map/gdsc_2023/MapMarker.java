package map.gdsc_2023;


import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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


    private static void loadBitmap (Context context, String imageUrl, GoogleMap map, GeoPoint location) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .fitCenter()
                .into(new CustomTarget<Bitmap>(200,200){
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                .position(new LatLng(location.getLatitude(), location.getLongitude())));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    private void hazardLoadOnSucess (GoogleMap mMap, Context context, @NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            // success stuff
            for (QueryDocumentSnapshot document : task.getResult()) {
                GeoPoint geo = (GeoPoint) document.get("location");
                LatLng pin = new LatLng(geo.getLatitude(), geo.getLongitude());
                String link = (String) document.get("image");
                loadBitmap(context, link, mMap, geo);
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
