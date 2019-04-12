package services.mocks;

import models.bo.Participant;
import services.database.DBParticipantInterface;

import java.util.concurrent.CompletableFuture;

public class MockDBParticipantService implements DBParticipantInterface {

    private Participant participant = new Participant("TestParticipant", "MirEgal", "Max", "Mustermann");

    @Override
    public CompletableFuture<Participant> getParticipant(String username, String password) {
        CompletableFuture<Participant> future = new CompletableFuture<>();

        if (username.equals("NotInDBParticipant")){
            future.complete(null);
        } else {
            future.complete(participant);
        }

        return future;
    }

    @Override
    public CompletableFuture<Participant> getParticipant(String username) {
        CompletableFuture<Participant> future = new CompletableFuture<>();

        if (username.equals("NotInDBParticipant")){
            future.complete(null);
        } else {
            future.complete(participant);
        }

        return future;
    }

    @Override
    public void insertParticipant(Participant participant) {

    }

    @Override
    public CompletableFuture<Long> deleteParticipant(Participant participant) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        future.complete(1L);
        return future;
    }
}
