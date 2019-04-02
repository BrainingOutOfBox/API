package services;

import models.bo.Participant;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class ParticipantService {

    @Inject
    private MongoDBParticipantService service;

    public CompletableFuture<Participant> getParticipant(Participant participant){
        return service.getParticipant(participant.getUsername(),participant.getPassword());
    }

    public void insertParticipant(Participant participant){
        service.insertParticipant(participant);
    }

    public CompletableFuture<Long> deleteParticipant(Participant participant){
        return service.deleteParticipant(participant);
    }
}
