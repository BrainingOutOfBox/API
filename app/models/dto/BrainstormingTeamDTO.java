package models.dto;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

public final class BrainstormingTeamDTO {
    private ObjectId id;
    private String identifier;
    private String name;
    private String purpose;
    private int nrOfParticipants;
    private int currentNrOfParticipants;
    private ArrayList<ParticipantDTO> participants;
    private ParticipantDTO moderator;

    public BrainstormingTeamDTO() {
        this.identifier = UUID.randomUUID().toString();
        this.participants = new ArrayList<>();
    }

    public BrainstormingTeamDTO(String name, String purpose, int nrOfParticipants, int currentNrOfParticipants, ArrayList<ParticipantDTO> participants, ParticipantDTO moderator) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.purpose = purpose;
        this.nrOfParticipants = nrOfParticipants;
        this.currentNrOfParticipants = currentNrOfParticipants;
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

    public int getCurrentNrOfParticipants() {
        return currentNrOfParticipants;
    }

    public void setCurrentNrOfParticipants(int currentNrOfParticipants) {
        this.currentNrOfParticipants = currentNrOfParticipants;
    }

    public ArrayList<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public ParticipantDTO getModerator() {
        return moderator;
    }

    public void setModerator(ParticipantDTO moderator) {
        this.moderator = moderator;
    }

}
