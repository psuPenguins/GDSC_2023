package map.gdsc_2023;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;


public class NewReportFragment extends Fragment {
    final String TAG = "ADDREPORT";
    //private required variables
    private Button btnBack;
    private ButtonsReceiver receiver;
    private Buttons mapButtons;
    private static final int pic_id = 123;
    private Uri photoUri;
    @Override
    public void onAttach(Context context){
        super.onAttach(getContext());
        receiver = (ButtonsReceiver) context;
    }
    private ImageView ivuploadImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mapButtons = receiver.getResult();
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

        String description =etNewReport.getText().toString();
        GeoPoint geoPoint = new GeoPoint(MapsActivity.mLastLocation.getLatitude(), MapsActivity.mLastLocation.getLatitude());
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

        newModerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.put("minor", false);
                tags.put("moderate", true);
                tags.put("serious", false);
            }
        });

        newSeriousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.put("minor", false);
                tags.put("moderate", false);
                tags.put("serious", true);
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
                File photoFile = null;
                try {
                    // Create a temporary file to store the image
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Handle the exception
                    ex.printStackTrace();
                }

                if (photoFile != null) {
                    // Get the content URI using the FileProvider
                    Uri photoURI = FileProvider.getUriForFile(view.getContext(), "com.example.myapp.fileprovider", photoFile);

                    // Add the content URI to the intent
                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    // Start the camera activity
                    startActivityForResult(camera_intent, pic_id);
                }
                //startActivityForResult(camera_intent, pic_id);
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
                // upload to firebase storage
                String url = FBImageAdapter.uploadImageWithUriDownloadUrl(photoUri);

                // upload image to firestore
                FSHazard newReport = new FSHazard(description, url, geoPoint, tags, severity);

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
    }
    // This method will help to retrieve the image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Uri photoUri = data.getData();

            // Use the content resolver to open an input stream for the photo
            InputStream inputStream = null;
            try {
                inputStream = getContext().getContentResolver().openInputStream(photoUri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Use BitmapFactory to decode the input stream into a Bitmap
            Bitmap photo = BitmapFactory.decodeStream(inputStream);

            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            Glide.with(getContext())
                    .asBitmap()
                    .load(photo)
                    .fitCenter()
                    .into(ivuploadImage);

            if(data.getData()==null){

            }else{
                photoUri = data.getData();
            }

            Log.i("Debugging", "URI: " + photoUri);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name with the current time in milliseconds
        int time = (int) (System.currentTimeMillis());
        String imageFileName = "IMG_" + time + "_";

        // Get the directory for the app's private pictures directory
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create a new temporary file with the image file name and ".jpg" extension

        // Return the temporary file
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /*
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    */

}
