package map.gdsc_2023;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.gdsc_2023.databinding.ActivityMapsBinding;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // for search view.
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // marking map

        // adding on query listener for our search view.
        // initializing our search view.
        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);


        LinearLayout addReportBtn;
        Button useCurLocBtn, useSelectLocBtn, cancelReportBtn;
        addReportBtn = findViewById(R.id.btnAddReport);
        useCurLocBtn = findViewById(R.id.currentLocationButton);
        useSelectLocBtn = findViewById(R.id.selectLocationButton);
        cancelReportBtn = findViewById(R.id.cancelButton);
        CoordinatorLayout persistentBottomSheet = findViewById(R.id.persistentBottomSheet);
        LinearLayout locationReportLayout = findViewById(R.id.locationReportLayout);

        //choose to add report
        addReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make other layout visible, first one gone
                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.VISIBLE);
//                useCurLocBtn = findViewById(R.id.currentLocationButton);
//                useSelectLocBtn = findViewById(R.id.selectLocationButton);
//                cancelReportBtn = findViewbyId(R.id.cancelButton);

            }
        });
        //choose to use current location
        useCurLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make other layout visible, first one gone
                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.GONE);
//               selectedLocation=??;
               replaceFragment(new NewReportFragment());

            }
        });
        //choose to select a location
        useSelectLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.GONE);
//               selectedLocation=??;
                replaceFragment(new NewReportFragment());


            }
        });

        //cancel adding a report
        cancelReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make other layout visible, first one gone
                locationReportLayout.setVisibility(View.GONE);
                persistentBottomSheet.setVisibility(View.VISIBLE);

            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        LatLng pennState = new LatLng(40.7965, -77.8628);
        mMap.addMarker(new MarkerOptions().position(pennState).title("Marker in Penn State"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pennState,17));
        */

        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .setMinUpdateDistanceMeters(1)
                .build();
        // permission check thing
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
        mMap.setMyLocationEnabled(true);

        // marking map
        MapMarker marker = new MapMarker();
        marker.loadHazard(mMap, this);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     * Not used for now.
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }
        // 2. Otherwise, request location permissions from the user.
        // PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    /**
     * location callback thing
     *
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //move map camera
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    };

//Will need to import those classes
//Do all activity screens need to be fragments?


    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map,fragment);
        fragmentTransaction.commit();

    }

}