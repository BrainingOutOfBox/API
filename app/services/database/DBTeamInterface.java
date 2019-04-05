package services.database;

import com.google.inject.ImplementedBy;
import models.bo.BrainstormingTeam;
import models.bo.Participant;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@ImplementedBy(MongoDBTeamService.class)
public interface DBTeamInterface {

    CompletableFuture<BrainstormingTeam> getTeam(String id);

    CompletableFuture<Queue<BrainstormingTeam>> getAllTeamsOfParticipant(Participant participant);

    void changeTeamMembers(BrainstormingTeam team, Number number);

    void insertTeam(BrainstormingTeam team);

    CompletableFuture<Long> deleteTeam(BrainstormingTeam team);
}
