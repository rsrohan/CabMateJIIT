package com.example.cabmatejiit;

public class UserProfile {

    String name;
    String phone;
    String enrollment;
    String gender;
    String year;
    String bookedNumber;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    String imageURL;
    boolean alreadyBooked;

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

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
}
