package map.gdsc_2023;

import androidx.fragment.app.Fragment;

import android.content.Context;
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


public class ViewReportFragment extends Fragment {
    //private required variables
    final String TAG = "VIEWREPORT";
    private ButtonsReceiver receiver;
    private Buttons mapButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mapButtons = receiver.getResult();
        mapButtons.hideAll();
        return inflater.inflate(R.layout.activity_existing_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG,"I'm in ViewReportFragment");

        // link the private variables to the elements in the xml files

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(getContext());
        receiver = (ButtonsReceiver) context;
    }

}

