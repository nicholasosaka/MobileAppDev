package com.example.ic12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ContactsList extends AppCompatActivity implements ContactsAdapter.InteractWithContact {

    private FirebaseAuth auth;
    FirebaseFirestore db;

    private String TAG = "IC12-CL";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter rv_adapter;
    private RecyclerView.LayoutManager rv_layoutManager;

    private HashMap<String, Contact> db_contacts = new HashMap<>();
    private ArrayList<Contact> rv_contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        setTitle("Contacts List");
        Intent intent = getIntent();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.contactsRecycler);

        rv_layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_layoutManager);
        rv_adapter = new ContactsAdapter(this, rv_contacts);
        recyclerView.setAdapter(rv_adapter);


        findViewById(R.id.contactsLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent toLogin = new Intent(ContactsList.this, Login.class);
                startActivity(toLogin);
                finish();
            }
        });

        findViewById(R.id.contactsCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreateNewContact = new Intent(ContactsList.this, CreateNewContact.class);
                startActivity(toCreateNewContact);
                finish();
            }
        });

        db.collection("users")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New contact: " + dc.getDocument().getData());
                                    db_contacts.put(dc.getDocument().getId(), new Contact(dc.getDocument().getId(), (dc.getDocument().getData())));
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed contact: " + dc.getDocument().getData());
                                    db_contacts.remove(dc.getDocument().getId());
                                    break;
                            }
                        }

                        rv_contacts.clear();

                        rv_contacts.addAll(db_contacts.values());

                        for(Contact c : rv_contacts){
                            Log.d(TAG, "Current Contacts: " + c);
                        }

                        rv_adapter.notifyDataSetChanged();
                    }
                });

    }
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
    public void deleteContact(int position) {
        Log.d(TAG, "deleteContact: " + position + " @ " + rv_contacts.get(position));
        db.collection("users")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("contacts")
                .document(rv_contacts.get(position).getID())
                .delete();
    }
}
