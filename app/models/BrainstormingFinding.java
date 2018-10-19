package models;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class BrainstormingFinding {
    private ObjectId id;
    private String name;
    private String problemDescription;
    private int nrOfIdeas;
    private int baseRoundTime;
    private int currentRound;
    private String currentRoundEndTime;
    private ArrayList<Brainsheet> brainsheets;
    private BrainstormingTeam brainstormingTeam;

    public BrainstormingFinding(){

    }

    public BrainstormingFinding(String name, String problemDescription, int nrOfIdeas, int baseRoundTime, int currentRound, String currentRoundEndTime, ArrayList<Brainsheet> brainsheets, BrainstormingTeam brainstormingTeam) {
        this.name = name;
        this.problemDescription = problemDescription;
        this.nrOfIdeas = nrOfIdeas;
        this.baseRoundTime = baseRoundTime;
        this.currentRound = currentRound;
        this.currentRoundEndTime = currentRoundEndTime;
        this.brainsheets = brainsheets;
        this.brainstormingTeam = brainstormingTeam;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public int getNrOfIdeas() {
        return nrOfIdeas;
    }

    public void setNrOfIdeas(int nrOfIdeas) {
        this.nrOfIdeas = nrOfIdeas;
    }

    public int getBaseRoundTime() {
        return baseRoundTime;
    }

    public void setBaseRoundTime(int baseRoundTime) {
        this.baseRoundTime = baseRoundTime;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getCurrentRoundEndTime() {
        return currentRoundEndTime;
    }

    public void setCurrentRoundEndTime(String currentRoundEndTime) {
        this.currentRoundEndTime = currentRoundEndTime;
    }

    public ArrayList<Brainsheet> getBrainsheets() {
        return brainsheets;
    }

    public void setBrainsheets(ArrayList<Brainsheet> brainsheets) {
        this.brainsheets = brainsheets;
    }

    public BrainstormingTeam getBrainstormingTeam() {
        return brainstormingTeam;
    }

    public void setBrainstormingTeam(BrainstormingTeam brainstormingTeam) {
        this.brainstormingTeam = brainstormingTeam;
    }
}
