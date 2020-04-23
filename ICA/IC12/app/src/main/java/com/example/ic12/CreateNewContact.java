package com.example.ic12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class CreateNewContact extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "IC12-CNC";

    FirebaseAuth auth;

    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageRef;
    private boolean isTakenPhoto = false;

    Bitmap imageBitmap;
    ImageView imageView;

    EditText emailET;
    EditText phoneET;
    EditText nameET;


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "onStart: currentUser: " + currentUser.getEmail());
        } else {
            Log.d(TAG, "onStart: currentUser is null");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_contact);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Button button = findViewById(R.id.createContactSubmit);

        Log.d(TAG, "onCreate: Creating listener");

        imageView = findViewById(R.id.createContactImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        emailET = findViewById(R.id.createContactEmail);
        nameET = findViewById(R.id.creatContactName);
        phoneET = findViewById(R.id.createContactPhone);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTakenPhoto){

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();


                    Log.d(TAG, "Image Hash: " + Integer.toHexString(Arrays.hashCode(data)));
                    final StorageReference imageStorageRef = storageRef.child(Integer.toHexString(Arrays.hashCode(data)));

                    UploadTask uploadTask = imageStorageRef.putBytes(data);

                    Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            // Continue with the task to get the download URL
                            return imageStorageRef.getDownloadUrl();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(isDataValid()) {
                                Contact contact = new Contact(
                                        Objects.requireNonNull(auth.getCurrentUser()),
                                        getName(),
                                        getEmail(),
                                        getPhone(),
                                        uri
                                );
                                Log.d(TAG, "onSuccess: " + contact);
                                sendContactToFirebase(contact);
                            }
                        }
                    });

                } else {
                    if(isDataValid()) {
                        Contact contact = new Contact(
                                Objects.requireNonNull(auth.getCurrentUser()),
                                getName(),
                                getEmail(),
                                getPhone()
                        );
                        Log.d(TAG, "onSuccess: " + contact);
                        sendContactToFirebase(contact);
                    }
                }
            }
        });
    }

    private boolean isDataValid() {
        String email = getEmail();
        String name = getName();
        String phone = getPhone();

        if(email.length() == 0){
            emailET.setError("Please enter an email");
            return false;
        }

        if(name.length() == 0){
            nameET.setError("Please enter a name");
            return false;
        }

        if(phone.length() == 0){
            phoneET.setError("Please enter a phone number");
            return false;
        }

        String emailRegex = "^(.+)@(.+)\\.(.+)$"; //REGEX: XXX@YYY.ZZZ
        Pattern emailPattern = Pattern.compile(emailRegex);
        if(!emailPattern.matcher(email).matches()){
            emailET.setError("Enter a valid email address.");
            return false;
        }

        return true;
    }

    private String getPhone() {
        return phoneET.getText().toString().trim();
    }

    private String getName() {
        return nameET.getText().toString().trim();
    }

    private String getEmail() {
        return emailET.getText().toString().trim();
    }

    private void sendContactToFirebase(Contact contact) {
        final String ownerEmail = Objects.requireNonNull(contact.getOwner());

        db.collection("users")
                .document(ownerEmail)
                .collection("contacts")
                .add(contact)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Contact in Firestore under: " + ownerEmail);

                            //if successful, send user back to contacts list
                            Intent toContactsList = new Intent(CreateNewContact.this, ContactsList.class);
                            startActivity(toContactsList);
                            finish();

                        } else {
                            Log.d(TAG, "onComplete: Contact upload to Firestore failed");
                        }
                    }
                });
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            assert extras != null;
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            isTakenPhoto = true;
        }
    }

}
