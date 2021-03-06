package services.business;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import services.database.DBTeamInterface;

import javax.inject.Inject;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamService {

    private DBTeamInterface service;

    @Inject
    public TeamService(DBTeamInterface service) {
        this.service = service;
    }

    public void insertTeam(BrainstormingTeam team){
        Participant participant = new Participant(team.getModerator().getUsername(), team.getModerator().getPassword(), team.getModerator().getFirstname(), team.getModerator().getLastname());

        if (team.joinTeam(participant)) {
            team.setCurrentNrOfParticipants(1);
            team.setIdentifier(UUID.randomUUID().toString());
            service.insertTeam(team);
        }
    }

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
