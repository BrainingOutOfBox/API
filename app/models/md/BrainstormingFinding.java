package models.md;

import net.steppschuh.markdowngenerator.MarkdownCascadable;
import net.steppschuh.markdowngenerator.MarkdownElement;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.text.Text;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

public class BrainstormingFinding extends MarkdownElement implements MarkdownCascadable {
    private ObjectId id;
    private String identifier;
    private String name;
    private String problemDescription;
    private int nrOfIdeas;
    private int baseRoundTime;
    private int currentRound;
    private String currentRoundEndTime;
    private String type;
    private ArrayList<Brainsheet> brainsheets;
    private int deliveredBrainsheetsInCurrentRound;
    private String brainstormingTeam;

    public BrainstormingFinding(){

    }

    public BrainstormingFinding(String name, String problemDescription, int nrOfIdeas, int baseRoundTime, int currentRound, String currentRoundEndTime, String type, ArrayList<Brainsheet> brainsheets, int deliveredBrainsheetsInCurrentRound, String brainstormingTeam) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.problemDescription = problemDescription;
        this.nrOfIdeas = nrOfIdeas;
        this.baseRoundTime = baseRoundTime;
        this.currentRound = currentRound;
        this.currentRoundEndTime = currentRoundEndTime;
        this.type = type;
        this.brainsheets = brainsheets;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Brainsheet> getBrainsheets() {
        return brainsheets;
    }

    public void setBrainsheets(ArrayList<Brainsheet> brainsheets) {
        this.brainsheets = brainsheets;
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

    @BsonIgnore
    @Override
    public String getPredecessor() {
        return new Heading(getName(),1).toString() + "\n";
    }

    @BsonIgnore
    @Override
    public String getSuccessor() {
        StringBuilder successor = new StringBuilder();

        successor.append(new BoldText("Basic Information").toString()).append("\n")
                .append(new Text("Description: ").toString()).append(getProblemDescription()).append("\n")
                .append(new Text("Type: ").toString()).append(getType()).append("\n")
                .append(new Text("Number of Ideas: ").toString()).append(getNrOfIdeas()).append("\n")
                .append(new Text("Team: ").toString()).append(getBrainstormingTeam()).append("\n\n")
                .append(new Heading("Sheets", 2).toString()).append("\n");

        return successor.toString();
    }

    @BsonIgnore
    @Override
    public String serialize() throws MarkdownSerializationException {

        if (getName().equals("") || getProblemDescription().equals("") || getNrOfIdeas() == 0 || getType().equals("") || getBrainstormingTeam().equals("")) {
            throw new MarkdownSerializationException("name is null or description");
        }

        StringBuilder brainsheets = new StringBuilder();

        for (Brainsheet brainsheet: getBrainsheets()){
            brainsheets.append(brainsheet.serialize());
        }

        return getPredecessor() + getSuccessor() + brainsheets.toString();
    }
}
