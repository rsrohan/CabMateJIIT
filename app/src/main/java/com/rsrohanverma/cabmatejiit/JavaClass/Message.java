package com.rsrohanverma.cabmatejiit.JavaClass;

public class Message {

    String message = "-";
    String name;
    String number;


    String timestamp;

    public Message(String message, String name, String number, String timestamp) {
        this.message = message;
        this.name = name;
        this.number = number;
        this.timestamp = timestamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Message(String message, String name, String number) {
        this.message = message;
        this.name = name;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message(String message, String name) {
        this.message = message;
        this.name = name;
    }

    public Message() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
