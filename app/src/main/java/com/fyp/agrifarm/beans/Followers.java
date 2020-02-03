package com.fyp.agrifarm.beans;

public class Followers {

    private String Uid;
    private String Username;
    private String photouri;



    public Followers() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return Username;
    }


    public void setUsername(String username) {
        Username = username;
    }

    public Followers(String uid, String username) {
        Uid = uid;
        Username = username;
    }

    public String getPhotouri() {
        return photouri;
    }

    public void setPhotouri(String photouri) {
        this.photouri = photouri;
    }

    public Followers(String uid, String username, String photouri) {
        Uid = uid;
        Username = username;
        this.photouri = photouri;
    }
}
