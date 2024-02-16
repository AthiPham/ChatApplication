package com.thiPNA219166.onlinechatapp.Model;

public class User {
    private String name,password,imageURL;

    public User() {
    }

    public User(String name, String password, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
