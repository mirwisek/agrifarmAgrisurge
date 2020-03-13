package com.fyp.agrifarm.model;

public class Following {
    private String Username;
    private String photouri;

    public String getPhotouri() {
        return photouri;
    }

    public void setPhotouri(String photouri) {
        this.photouri = photouri;
    }

    public Following(String username, String photouri) {
        Username = username;
        this.photouri = photouri;
    }

    public Following() {
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Following(String username) {
        Username = username;
    }
}
