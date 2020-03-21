package com.rsrohanverma.cabmatejiit.JavaClass;

import com.rsrohanverma.cabmatejiit.Cabmate;
import com.rsrohanverma.cabmatejiit.Message;

import java.util.ArrayList;

public class GroupDetails {

    int numberOfVacantSeats;
    ArrayList<Cabmate> cabbies;
    ArrayList<Message> messages;

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
        this.messages = messages;
        this.uniqueGroupName = uniqueGroupName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public GroupDetails(int numberOfVacantSeats, ArrayList<Cabmate> cabbies, ArrayList<Message> messages) {
        this.numberOfVacantSeats = numberOfVacantSeats;
        this.cabbies = cabbies;
        this.messages = messages;
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
}
