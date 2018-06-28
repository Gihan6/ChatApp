package com.example.gihan.chatapp.model;

/**
 * Created by Gihan on 7/18/2017.
 */

public class Users {


    private String name;
    private String status;
    private String image;
    private String themp_up;

    public Users(){

    }

    public Users(String name, String status, String image,String themp_up) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.themp_up=themp_up;
    }

    public String getThemp_up() {
        return themp_up;
    }

    public void setThemp_up(String themp_up) {
        this.themp_up = themp_up;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
