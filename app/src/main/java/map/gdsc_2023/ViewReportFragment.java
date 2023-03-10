package map.gdsc_2023;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.Map;


public class ViewReportFragment extends Fragment {
    //private required variables
    final String TAG = "VIEWREPORT";
    private ButtonsReceiver receiver;
    private Buttons mapButtons;
    private FSHazard hazard;
    private String description;
    private String image;
    private Map severity;
    private Map tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mapButtons = receiver.getResult();
        mapButtons.hideAll();
        Bundle args = getArguments();
        description = args.getString("description");
        image = args.getString("image");
        Parcelable parcelSeverity = args.getParcelable("severity");
        Parcelable parcelTag = args.getParcelable("tags");
        severity = Parcels.unwrap(parcelSeverity);
        tag = Parcels.unwrap(parcelTag);

        return inflater.inflate(R.layout.activity_existing_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG,"I'm in ViewReportFragment");

        // link the private variables to the elements in the xml files
        TextView tvReportDescription = view.findViewById(R.id.tvReportDescription);
        ImageView ivReportPic = view.findViewById(R.id.ivReportPic);
        Button btnBack = view.findViewById(R.id.backButton);

        tvReportDescription.setText(description);

        //image
        Glide.with(this.getContext())
                .asBitmap()
                .load(image)
                .fitCenter()
                .into(ivReportPic);

        //back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Log.i(TAG, "onClick back button");
                goMapsActivity();
            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(getContext());
        receiver = (ButtonsReceiver) context;
    }

    private void goMapsActivity(){
        Log.i(TAG, "Going into MapsFragment");
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction().setReorderingAllowed(true);
        Log.i(TAG, "Going into MapsFragment");

        mapButtons.showMainFrames();

        transaction.remove(this);
        transaction.commit();
    }

}

