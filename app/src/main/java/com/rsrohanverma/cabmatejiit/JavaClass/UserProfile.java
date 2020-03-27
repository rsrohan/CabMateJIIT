package com.rsrohanverma.cabmatejiit.JavaClass;

public class UserProfile {

    String name;
    String phone;
    String gender;
    String bookedNumber;
    String imageURL;
    boolean alreadyBooked;
    boolean isBlocked;
    String pathBooked;

    public UserProfile(String name, String phone, String gender, String bookedNumber, String imageURL, boolean alreadyBooked, boolean isBlocked, String pathBooked) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.bookedNumber = bookedNumber;
        this.imageURL = imageURL;
        this.alreadyBooked = alreadyBooked;
        this.isBlocked = isBlocked;
        this.pathBooked = pathBooked;
    }



    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public UserProfile() {
    }

    public UserProfile(String name) {
        this.name = name;
    }

    public String getBookedNumber() {
        return bookedNumber;
    }

    public void setBookedNumber(String bookedNumber) {
        this.bookedNumber = bookedNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserProfile(String name, String phone, String gender, String bookedNumber, String imageURL, boolean alreadyBooked) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.bookedNumber = bookedNumber;
        this.imageURL = imageURL;
        this.alreadyBooked = alreadyBooked;
    }

    public UserProfile(String name, boolean alreadyBooked) {
        this.name = name;
        this.alreadyBooked = alreadyBooked;
    }

    public boolean isAlreadyBooked() {
        return alreadyBooked;
    }

    public void setAlreadyBooked(boolean alreadyBooked) {
        this.alreadyBooked = alreadyBooked;
    }
    public boolean isIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean blocked) {
        isBlocked = blocked;
    }
    public String getPathBooked() {
        return pathBooked;
    }

    public void setPathBooked(String pathBooked) {
        this.pathBooked = pathBooked;
    }
}
