package com.example.ic12;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private static final String TAG = "IC-CAdap";

    public static InteractWithContact interactWithContact;

    public interface InteractWithContact{
        void deleteContact(int position);
    }

    ArrayList<Contact> contacts = new ArrayList<>();
    Context ctx;

    public ContactsAdapter(Context ctx, ArrayList<Contact> contacts) {
        this.contacts = contacts;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout rv_layout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_layout, parent, false);
        return new ViewHolder(rv_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        interactWithContact = (InteractWithContact) ctx;

        holder.itemView.setLongClickable(true);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO: implement delete
                ((InteractWithContact) ctx).deleteContact(position);
                return true;
            }
        });

        final Contact contact = contacts.get(position);

        holder.email.setText(contact.getEmail());
        holder.phone.setText(contact.getPhone());
        holder.name.setText(contact.getName());
//      Picasso.get().load(contact.getImageURL()).into(holder.image);

        final ImageView target = holder.image;

        if(!contact.isDefaultImage()){
            StorageReference imageRef = storage.getReferenceFromUrl(contact.getImageURI().toString());

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).into(target);
                }
            });
        } else {
            Picasso.get().load(android.R.drawable.ic_menu_camera).into(target);
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView email;
        private TextView phone;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactsLayoutName);
            email = itemView.findViewById(R.id.contactsLayoutEmail);
            phone = itemView.findViewById(R.id.contactsLayoutPhone);
            image = itemView.findViewById(R.id.contactsLayoutImage);
        }
    }
}
