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

    public static String uploadImageWithUriDownloadUrl(Uri uri){
        Log.i("FBI", "send the uri:" + uri);
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String ts =  tsTemp.toString();
        StorageReference uploadRef = storageRef.child(ts + ".jpg");
        UploadTask uploadTask = uploadRef.putFile(uri);
        photoUrl = ts + ".jpg";

        StorageReference downloadRef = storageRef.child(photoUrl);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return uploadRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {

                }
            }
        });
        return "";
    }

    public String getImageUrl(){
        return "";
    }

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
