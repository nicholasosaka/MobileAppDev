package com.example.ic12;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Contact implements Serializable {

    //contact info
    private String name;
    private String email;
    private String phone;
    private String imageURI;
    private boolean isDefaultImage;

    //parent user
    private String owner;



    private String ID;

    public Contact(String ID, Map<String, Object> map){
        this.ID = ID;
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.owner = (String) map.get("owner");
        this.phone = (String) map.get("phone");
        String mapURI = (String) map.get("imageURI");
        this.imageURI = mapURI;

        this.isDefaultImage = Objects.equals(mapURI, "");
    }

    public Contact(FirebaseUser owner, String name, String email, String phone) {
        this.owner = owner.getUid();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageURI = "";
        this.isDefaultImage = true;
    }


    public Contact(FirebaseUser owner, String name, String email, String phone, Uri imageURI) {
        this.owner = owner.getUid();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageURI = imageURI.toString();
        this.isDefaultImage = false;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isDefaultImage=" + isDefaultImage +
                ", owner='" + owner + '\'' +
                ", ID='" + ID + '\'' +
                '}';
    }

    public boolean isDefaultImage() {
        return isDefaultImage;
    }

    public void setDefaultImage(boolean defaultImage) {
        isDefaultImage = defaultImage;
    }

    public String getID() {
        return ID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI.toString();
    }
}
