package models;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

public final class BrainstormingTeam {
    private ObjectId id;
    private String identifier;
    private String name;
    private String purpose;
    private int nrOfParticipants;
    private ArrayList<Participant> participants;
    private Participant moderator;

    public BrainstormingTeam() {

    }

    public BrainstormingTeam(String name, String purpose, int nrOfParticipants, ArrayList<Participant> participants, Participant moderator) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.purpose = purpose;
        this.nrOfParticipants = nrOfParticipants;
        this.participants = participants;
        this.moderator = moderator;
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getNrOfParticipants() {
        return nrOfParticipants;
    }

    public void setNrOfParticipants(int nrOfParticipants) {
        this.nrOfParticipants = nrOfParticipants;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public Participant getModerator() {
        return moderator;
    }

    public void setModerator(Participant moderator) {
        this.moderator = moderator;
    }
}
