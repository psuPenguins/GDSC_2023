package map.gdsc_2023;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;


public class Buttons{
    private final LinearLayout addReportBtn, locationReportLayout;
    private final Button useCurLocBtn, useSelectLocBtn, cancelReportBtn;
    private final CoordinatorLayout persistentBottomSheet;
    private final SearchView searchView;

    public Buttons(LinearLayout addReportBtn,
                   LinearLayout locationReportLayout,
                   Button useCurLocBtn,
                   Button useSelectLocBtn,
                   Button cancelReportBtn,
                   CoordinatorLayout persistentBottomSheet,
                   SearchView searchView) {
        this.addReportBtn = addReportBtn;
        this.locationReportLayout = locationReportLayout;
        this.useCurLocBtn = useCurLocBtn;
        this.useSelectLocBtn = useSelectLocBtn;
        this.cancelReportBtn = cancelReportBtn;
        this.persistentBottomSheet = persistentBottomSheet;
        this.searchView = searchView;
    }


    public void addReport () {
        //choose to add report
        addReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make other layout visible, first one gone
                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);

            }
        });
    }

    public void useCurrentLocation (FragmentManager fragmentManager, Location mLastLocation) {
        //choose to use current location
        useCurLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make other layout visible, first one gone

                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);

                // Bundle up a point
                Bundle args = new Bundle();
                args.putDouble("longitude", mLastLocation.getLongitude());
                args.putDouble("latitude", mLastLocation.getLatitude());

                NewReportFragment newReportFragment = new NewReportFragment();
                newReportFragment.setArguments(args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map, newReportFragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void useSelectedLocation (FragmentManager fragmentManager, GoogleMap mMap) {
        //choose to select a location
        useSelectLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentBottomSheet.setVisibility(View.GONE);
                locationReportLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);

                mMap.setOnMapClickListener(new OnMapClickListener(){

                    public void onMapClick(@NonNull LatLng point){
                        Log.i("Select Location LatLong", "The point is: " + point);

                        // Bundle up a point
                        Bundle args = new Bundle();
                        args.putDouble("longitude", point.longitude);
                        args.putDouble("latitude", point.latitude);

                        NewReportFragment newReportFragment = new NewReportFragment();
                        newReportFragment.setArguments(args);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.map, newReportFragment);
                        fragmentTransaction.commit();
                        // TODO: add the report where point is (for VED) :)
                    }
                });
            }
        });
    }

    public void cancelReport () {
        //cancel adding a report
        cancelReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make other layout visible, first one gone
                locationReportLayout.setVisibility(View.GONE);
                persistentBottomSheet.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
            }
        });
    }


    public void showMainFrames() {
        /** Shows all elements in map activity*/
        locationReportLayout.setVisibility(View.GONE);
        persistentBottomSheet.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
    }
    public void showLocationSelectionFrame() {
        /** Shows all elements in map activity*/
        locationReportLayout.setVisibility(View.VISIBLE);
        persistentBottomSheet.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    public void hideAll() {
        /** Hide all elements in map activity*/
        locationReportLayout.setVisibility(View.GONE);
        persistentBottomSheet.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
    }

}
