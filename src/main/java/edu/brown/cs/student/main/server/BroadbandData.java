package edu.brown.cs.student.main.server;

public class BroadbandData {

    private String state;
    private String county;
    private double accessPercentage;

    public BroadbandData(){

    }

    public BroadbandData(String state, String county, double accessPercentage){
        this.state = state;
        this.county = county;
        this.accessPercentage = accessPercentage;
    }

    public String getState(){
        return state;
    }

    public String getCounty(){
        return county;
    }

    public double getAccessPercentage(){
        return accessPercentage;
    }
}
