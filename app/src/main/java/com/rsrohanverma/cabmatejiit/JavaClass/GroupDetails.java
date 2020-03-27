package com.rsrohanverma.cabmatejiit.JavaClass;

import java.util.ArrayList;

public class GroupDetails {

    int numberOfVacantSeats;
    ArrayList<Cabmate> cabbies;
    ArrayList<Cabmate> cabbies_backup;
    String timestamp;
    ArrayList<Message> CHATS;

    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies, ArrayList<Cabmate> cabbies_backup, ArrayList<Message> CHATS, String timestamp, String uniqueGroupName) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies;
        this.cabbies_backup = cabbies_backup;
        this.CHATS = CHATS;
        this.timestamp = timestamp;
        this.uniqueGroupName = uniqueGroupName;
    }

    public ArrayList<Cabmate> getCabbies_backup() {
        return cabbies_backup;
    }

    public void setCabbies_backup(ArrayList<Cabmate> cabbies_backup) {
        this.cabbies_backup = cabbies_backup;
    }



    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies, ArrayList<Message> CHATS, String timestamp, String uniqueGroupName) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies;
        this.CHATS = CHATS;
        this.timestamp = timestamp;
        this.uniqueGroupName = uniqueGroupName;
    }

    String uniqueGroupName;

    public String getUniqueGroupName() {
        return uniqueGroupName;
    }

    public void setUniqueGroupName(String uniqueGroupName) {
        this.uniqueGroupName = uniqueGroupName;
    }

    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies, ArrayList<Message> messages, String uniqueGroupName) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies;
        this.CHATS = messages;
        this.uniqueGroupName = uniqueGroupName;
    }

    public ArrayList<Message> getCHATS() {
        return CHATS;
    }

    public void setCHATS(ArrayList<Message> CHATS) {
        this.CHATS = CHATS;
    }

    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies, ArrayList<Message> CHATS) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies;
        this.CHATS = CHATS;
    }

    public GroupDetails() {
    }

    public int getNumberOfVacantSeats() {
        return numberOfVacantSeats;
    }

    public void setNumberOfVacantSeats(int numberOfVacantSeats) {
        this.numberOfVacantSeats = numberOfVacantSeats;
    }

    public ArrayList<Cabmate> getCabbies() {
        return cabbies;
    }

    public void setCabbies(ArrayList<Cabmate> cabbies) {
        this.cabbies = cabbies;
    }

    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies2) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
