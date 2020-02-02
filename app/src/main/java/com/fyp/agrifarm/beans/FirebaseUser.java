package com.fyp.agrifarm.beans;

public class FirebaseUser {
    private String fullname;
    private String photoUri;


    public FirebaseUser() {
    }

    public FirebaseUser(String fullname, String photoUri) {
        this.fullname = fullname;
        this.photoUri = photoUri;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhoto(String photoUri) {
        this.photoUri = photoUri;
    }
}
