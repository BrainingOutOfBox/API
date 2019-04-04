package services.business;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import services.database.MongoDBParticipantService;
import services.database.MongoDBTeamService;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class TeamService {

    @Inject
    private MongoDBTeamService service;
    @Inject
    private MongoDBParticipantService participantService;

    public void insertTeam(BrainstormingTeam team){ service.insertTeam(team); }

    public CompletableFuture<Long> deleteTeam(BrainstormingTeam team){
        return service.deleteTeam(team);
    }

    public CompletableFuture<Queue<BrainstormingTeam>> getAllTeamsOfParticipant(Participant participant){
        return service.getAllTeamsOfParticipant(participant);
    }

    public CompletableFuture<BrainstormingTeam> getTeam(String id){
        return service.getTeam(id);
    }

    public boolean joinTeam(BrainstormingTeam brainstormingTeam, Participant participant){
        if (brainstormingTeam!= null && brainstormingTeam.getNrOfParticipants() > brainstormingTeam.getCurrentNrOfParticipants() && brainstormingTeam.joinTeam(participant)) {
            service.changeTeamMembers(brainstormingTeam, 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean leaveTeam(BrainstormingTeam brainstormingTeam, Participant participant){
        if (brainstormingTeam != null && brainstormingTeam.getCurrentNrOfParticipants() > 0 && brainstormingTeam.leaveTeam(participant)) {
            service.changeTeamMembers(brainstormingTeam, -1);
            return true;
        } else {
            return false;
        }
    }
}
