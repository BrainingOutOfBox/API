package models.dto;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

public class BrainstormingFindingDTO {
    private ObjectId id;
    private String identifier;
    private String name;
    private String problemDescription;
    private int nrOfIdeas;
    private int baseRoundTime;
    private int currentRound;
    private String currentRoundEndTime;
    private ArrayList<BrainsheetDTO> brainsheets;
    private int deliveredBrainsheetsInCurrentRound;
    private String brainstormingTeam;

    public BrainstormingFindingDTO(){
        this.identifier = UUID.randomUUID().toString();
        this.brainsheets = new ArrayList<>();
    }

    public BrainstormingFindingDTO(String name, String problemDescription, int nrOfIdeas, int baseRoundTime, int currentRound, String currentRoundEndTime, ArrayList<BrainsheetDTO> brainsheetsDTO, int deliveredBrainsheetsInCurrentRound, String brainstormingTeam) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.problemDescription = problemDescription;
        this.nrOfIdeas = nrOfIdeas;
        this.baseRoundTime = baseRoundTime;
        this.currentRound = currentRound;
        this.currentRoundEndTime = currentRoundEndTime;
        this.brainsheets = brainsheetsDTO;
        this.deliveredBrainsheetsInCurrentRound = deliveredBrainsheetsInCurrentRound;
        this.brainstormingTeam = brainstormingTeam;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public ArrayList<BrainsheetDTO> getBrainsheets() {
        return brainsheets;
    }

    public void setBrainsheets(ArrayList<BrainsheetDTO> brainsheetsDTO) {
        this.brainsheets = brainsheetsDTO;
    }

    public int getDeliveredBrainsheetsInCurrentRound() {
        return deliveredBrainsheetsInCurrentRound;
    }

    public void setDeliveredBrainsheetsInCurrentRound(int deliveredBrainsheetsInCurrentRound) {
        this.deliveredBrainsheetsInCurrentRound = deliveredBrainsheetsInCurrentRound;
    }

    public String getBrainstormingTeam() {
        return brainstormingTeam;
    }

    public void setBrainstormingTeam(String brainstormingTeam) {
        this.brainstormingTeam = brainstormingTeam;
    }
}
