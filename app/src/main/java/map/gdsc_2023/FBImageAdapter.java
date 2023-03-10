package map.gdsc_2023;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.Timestamp;
import java.util.Objects;


public class FBImageAdapter extends AppCompatActivity {
    static final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    public static final int CAMERA_REQUEST_CODE = 42; // ??? What are you
    private Uri fileProvider;

    private static String photoUrl = "";



    public FBImageAdapter() {}

    public static void uploadImageWithUriDownloadUrl(Uri uri, OnSuccessListener<String> successListener){
        Log.i("FBI", "send the uri:" + uri);
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String ts =  Long.toString(tsTemp.getTime());
        StorageReference uploadRef = storageRef.child(ts + ".jpg");
        UploadTask uploadTask = uploadRef.putFile(uri);
        photoUrl = ts + ".jpg";
        Log.i("FilenameURL", photoUrl);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL for the file
            storageRef.child(photoUrl).getDownloadUrl().addOnSuccessListener(downloadUri -> {
                // Handle successful download URL retrieval
                String downloadUrl = downloadUri.toString();
                successListener.onSuccess(downloadUrl);
            }).addOnFailureListener(e -> {
                // Handle unsuccessful download URL retrieval
                Log.e("DownloadURL", "Error getting download URL: " + e.getMessage());
                successListener.onSuccess(null);
            });
        }).addOnFailureListener(e -> {
            // Handle unsuccessful upload
            Log.e("UploadImage", "Error uploading image: " + e.getMessage());
            successListener.onSuccess(null);
        });
    }

//    public static void imageUpload(Uri uri){
//        Log.i("DownloadURL", "Entering");
//        uploadImageWithUriDownloadUrl(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // Get the download URL for the file
//                storageRef.child(photoUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // Handle successful download URL retrieval
//                        String downloadUrl = uri.toString();
//                        Log.i("DownloadURL", downloadUrl);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful download URL retrieval
//                        Log.e("DownloadURL", "Error getting download URL: " + exception.getMessage());
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Handle unsuccessful upload
//                Log.e("UploadImage", "Error uploading image: " + e.getMessage());
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fileProvider = data.getData();
                Log.i("FBImageAdapter", "Uri: " + fileProvider);

            } else { // Result was a failure
                Log.i("FBImageAdapter", "death");
            }
        }
    }




}
