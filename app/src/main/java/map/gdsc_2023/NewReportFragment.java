package map.gdsc_2023;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class NewReportFragment extends Fragment {
    final String TAG = "ADDREPORT";
    //private required variables
    private Button btnBack;
    private FragmentReceiver receiver;
    private Buttons mapButtons;
    private static final int pic_id = 123;
    private Uri photoUri;
    @Override
    public void onAttach(Context context){
        super.onAttach(getContext());
        receiver = (FragmentReceiver) context;
    }
    private ImageView ivuploadImage;
    private String description;
    private Double longitude;
    private Double latitude;


    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mapButtons = receiver.getButtons();
        Bundle args = getArguments();
        longitude = args.getDouble("longitude");
        latitude = args.getDouble("latitude");
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

        Button newMinorButton = view.findViewById(R.id.newMinorButton);
        Button newModerateButton = view.findViewById(R.id.newModerateButton);
        Button newSeriousButton = view.findViewById(R.id.newSeriousButton);
        ivuploadImage = view.findViewById(R.id.ivuploadImage);

        TextView submitReportButton = view.findViewById(R.id.submitReportButton);

        // getting the variables
//        Location location = this.getArguments().getParcelable("lastLocation");


        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
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
        severity.put("minor", false);
        severity.put("moderate", false);
        severity.put("serious", false);

        newMinorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                severity.put("minor", true);
                severity.put("moderate", false);
                severity.put("serious", false);
            }
        });

        newModerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                severity.put("minor", false);
                severity.put("moderate", true);
                severity.put("serious", false);
            }
        });

        newSeriousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                severity.put("minor", false);
                severity.put("moderate", false);
                severity.put("serious", true);
            }
        });

        // TODO: get the image
        ivuploadImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("VisibleForTests")
            @Override
            public void onClick(View view) {
                Log.i("FBImage", "at least you're not fucked up");
                // Create the camera_intent ACTION_IMAGE_CAPTURE it will open the camera for capture the image
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Start the activity with camera_intent, and request pic id
                startActivityForResult(camera_intent, pic_id);
            }
        });

        // link the private variables to the elements in the xml files

        btnBack = view.findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Log.i(TAG, "onClick back button");
                goMapsActivity();
            }
        });

        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // upload image to firebase storage
                FBImageAdapter.uploadImageWithUriDownloadUrl(photoUri, new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String downloadUrl) {
                        if (downloadUrl != null) {
                            // upload image to firestore
                            Log.i("NewReportDownloadURL", downloadUrl);
                            description = etNewReport.getText().toString();
                            FSHazard newReport = new FSHazard(description, downloadUrl, geoPoint, tags, severity);
                            Log.i("NEW", "description: " + description + "\n downloadurl: " + downloadUrl + "\n geoPoint: " + geoPoint + "\n tags: " + tags + "\n severity: " + severity);
                            db.collection("Hazards")
                                    .add(newReport)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        } else {
                            // Handle the case where there was an error
                            Log.e("DownloadURL", "Error getting download URL");
                        }
//                        MapMarker marker = new MapMarker();
//                        marker.loadHazard(receiver.getMap(), receiver.getFM(),getContext());
                    }
                });

                // upload image to firestore


                // exit out
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
        mapButtons.showLocationSelectionFrame();

        transaction.remove(this);
        transaction.commit();

        MapMarker marker = new MapMarker();
        marker.loadHazard(receiver.getMap(), receiver.getFM(),getContext());
    }
    // This method will help to retrieve the image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            Glide.with(this.getContext())
                    .asBitmap()
                    .load(photo)
                    .fitCenter()
                    .into(ivuploadImage);
            photoUri = getImageUri(getContext(), photo);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
