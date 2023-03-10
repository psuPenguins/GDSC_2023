package map.gdsc_2023;


import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapMarker{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HashMap<Integer,FSHazard> hazards = new HashMap<>();



    public MapMarker(){}


    private void loadBitmap (Context context, FragmentManager fragmentManager, GoogleMap map, FSHazard hazard) {
        Glide.with(context)
                .asBitmap()
                .load(hazard.image)
                .fitCenter()
                .into(new CustomTarget<Bitmap>(200,200){
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                .position(new LatLng(hazard.location.getLatitude(), hazard.location.getLongitude())));
                        marker.setTag(hazards.size());
                        hazards.put(hazards.size(), hazard);
                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                FSHazard clickedHazard = hazards.get(marker.getTag());
                                Log.i("Clicked ping on map.", "The point is: " + clickedHazard.location);
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.map, new ViewReportFragment());
                                fragmentTransaction.commit();
                                // TODO: add the report where point is (for VED) :)
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    private void hazardLoadOnSucess (GoogleMap mMap, FragmentManager fragmentManager, Context context, @NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            // success stuff
            for (QueryDocumentSnapshot document : task.getResult()) {
                FSHazard hazard = new FSHazard((String)document.get("description"), (String)document.get("image"), (GeoPoint)document.get("location"), (Map<String, Object>)document.get("tags"), (Map<String, Object>)document.get("severity"));
                loadBitmap(context, fragmentManager, mMap, hazard);
            }
        } else {
            // death
            Log.i("loadHazard", "Done fucked up");
        }
    }

    public void loadHazard (GoogleMap mMap, FragmentManager fragmentManager, Context context) {
        db.collection("Hazards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hazardLoadOnSucess(mMap, fragmentManager, context, task);
                    }
                });
    }

    public void addHazard() {} // when we report a new hazard
    public void removeHazard() {} // when we remove a hazard

}
