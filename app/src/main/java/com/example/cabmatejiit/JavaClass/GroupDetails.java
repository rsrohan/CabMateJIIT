package com.example.cabmatejiit.JavaClass;

import com.example.cabmatejiit.Cabmate;

import java.util.ArrayList;

public class GroupDetails {

    int numberOfVacantSeats;
    ArrayList<Cabmate> cabbies;

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
