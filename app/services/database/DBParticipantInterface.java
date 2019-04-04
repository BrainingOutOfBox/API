package services.database;

import models.bo.Participant;

import java.util.concurrent.CompletableFuture;

public interface DBParticipantInterface {

    CompletableFuture<Participant> getParticipant(String username, String password);

    CompletableFuture<Participant> getParticipant(String username);

    void insertParticipant(Participant participant);

    CompletableFuture<Long> deleteParticipant(Participant participant);
}
