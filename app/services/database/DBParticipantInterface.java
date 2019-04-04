package services.database;

import com.google.inject.ImplementedBy;
import models.bo.Participant;

import java.util.concurrent.CompletableFuture;

@ImplementedBy(MongoDBParticipantService.class)
public interface DBParticipantInterface {

    CompletableFuture<Participant> getParticipant(String username, String password);

    CompletableFuture<Participant> getParticipant(String username);

    void insertParticipant(Participant participant);

    CompletableFuture<Long> deleteParticipant(Participant participant);
}
