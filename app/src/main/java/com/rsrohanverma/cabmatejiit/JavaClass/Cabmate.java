package com.rsrohanverma.cabmatejiit.JavaClass;

public class Cabmate {

    String phone;
    String numberofseats;
    String source;
    String destination;
    String timestamp;

    public Cabmate(String phone, String numberofseats, String source, String destination, String timestamp, String name) {
        this.phone = phone;
        this.numberofseats = numberofseats;
        this.source = source;
        this.destination = destination;
        this.timestamp = timestamp;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cabmate(String phone, String numberofseats, String source, String destination, String name) {
        this.phone = phone;
        this.numberofseats = numberofseats;
        this.source = source;
        this.destination = destination;
        this.name = name;
    }

    String name;

    public Cabmate(String phone, String numberofseats, String source, String destination) {
        this.phone = phone;
        this.numberofseats = numberofseats;
        this.source = source;
        this.destination = destination;
    }

    public Cabmate() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNumberofseats() {
        return numberofseats;
    }

    public void setNumberofseats(String numberofseats) {
        this.numberofseats = numberofseats;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
