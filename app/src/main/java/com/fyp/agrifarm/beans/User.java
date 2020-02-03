package com.fyp.agrifarm.beans;

import androidx.annotation.NonNull;

import java.util.List;

public class User {

    private String location;
    private String age;
    private String occupation;
    private String fullname;
    private String photoUri;
    private List<String> followers ;
    private List<String> following ;


    public User() {

    }

    public User(String location, String age, String occupation, String fullname, String photoUri) {
        this.location = location;
        this.age = age;
        this.occupation = occupation;
        this.fullname = fullname;
        this.photoUri = photoUri;
    }

    public User(String location, String age, String occupation, String fullname, String photoUri, List<String> followers, List<String> following) {
        this.location = location;
        this.age = age;
        this.occupation = occupation;
        this.fullname = fullname;
        this.photoUri = photoUri;
        this.followers = followers;
        this.following = following;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }





    @NonNull
    @Override
    public String toString() {
        return "User: { " + fullname + ", " +
                occupation + ", " +
                age + ", " +
                location + ", " +
                photoUri + " }";

    }
}
