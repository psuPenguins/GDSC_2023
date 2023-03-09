package map.gdsc_2023;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class NewReportFragment extends Fragment {
    final String TAG = "ADDREPORT";
    //private required variables
    private Button btnBack;
    private Receiver receiver;
    private Buttons mapButtons;

    @Override
    public void onAttach(Context context){
        super.onAttach(getContext());
        receiver = (Receiver) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mapButtons = receiver.getResult();
        return inflater.inflate(R.layout.activity_new_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG,"I'm in AddReportFragment");

        // link the private variables to the elements in the xml files
        btnBack = view.findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Log.i(TAG, "onClick back button");
                goMapsActivity();
            }
        });
    }

    private void goMapsActivity(){
        Log.i(TAG, "Going into MapsFragment");
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction().setReorderingAllowed(true);
        Log.i(TAG, "Going into MapsFragment");

        // Show all of the views on maps
        mapButtons.showAll();

        transaction.remove(this);
        transaction.commit();
    }

}
