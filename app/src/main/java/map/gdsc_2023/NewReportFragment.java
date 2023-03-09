package map.gdsc_2023;

import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;


public class NewReportFragment extends Fragment {
    final String TAG = "ADDREPORT";
    //private required variables
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_new_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG,"I'm in AddReportFragment");

        EditText etNewReport = view.findViewById(R.id.etNewReport);

        LinearLayout llicyroad = view.findViewById(R.id.llicyroad);
        LinearLayout llobstacles = view.findViewById(R.id.llobstacles);
        LinearLayout llpuddles = view.findViewById(R.id.llpuddles);
        //TODO: others doesn't work now
        LinearLayout llothers = view.findViewById(R.id.llothers);

        LinearLayout newMinorButton = view.findViewById(R.id.newMinorButton);
        LinearLayout newModerateButton = view.findViewById(R.id.newModerateButton);
        LinearLayout newSeriousButton = view.findViewById(R.id.newSeriousButton);

        // getting the variables
        Location location = this.getArguments().getParcelable("lastLocation");

        String description =etNewReport.getText().toString();
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLatitude());
        Map<String, Object> tags = new HashMap<>();
        tags.put("icy road", false);
        tags.put("obstacles", false);
        tags.put("puddles", false);
        tags.put("uneven road", false);
        tags.put("others", "");

        llicyroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = (boolean) tags.get("icy road");
                tags.put("icy road", !value);
            }
        });

        llobstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = (boolean) tags.get("obstacles");
                tags.put("obstacles", !value);
            }
        });

        llpuddles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = (boolean) tags.get("puddles");
                tags.put("puddles", !value);
            }
        });

        Map<String, Object> severity = new HashMap<>();
        tags.put("minor", false);
        tags.put("moderate", false);
        tags.put("serious", false);

        newMinorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.put("minor", true);
                tags.put("moderate", false);
                tags.put("serious", false);
            }
        });

        newMinorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.put("minor", false);
                tags.put("moderate", true);
                tags.put("serious", false);
            }
        });

        newMinorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.put("minor", false);
                tags.put("moderate", false);
                tags.put("serious", true);
            }
        });

        // TODO: get the image
        // link the private variables to the elements in the xml files
//        FSHazard newReport = new FSHazard(description, image, geoPoint, tags, severity);
//        MapsActivity.global_location = null;

    }

}
