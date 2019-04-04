package services.business;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import services.database.MongoDBTeamService;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class TeamService {

    @Inject
    private MongoDBTeamService service;

    public void insertTeam(BrainstormingTeam team){
        service.insertTeam(team);
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

    public void changeTeamMembers(BrainstormingTeam team, Number number){
        service.changeTeamMembers(team, number);
    }
}
