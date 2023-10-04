package com.example.classroom;

public class Users {
    String userId,name,profile,mail;

    public Users(String userId, String name, String profile,String mail) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
        this.mail =mail;
    }


    public Users() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
