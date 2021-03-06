package services.business;

import models.bo.Participant;
import services.database.DBParticipantInterface;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ParticipantService {

    private DBParticipantInterface service;

    @Inject
    public ParticipantService(DBParticipantInterface service) {
        this.service = service;
    }

    public CompletableFuture<Participant> getParticipant(Participant participant){
        return service.getParticipant(participant.getUsername(),participant.getPassword());
    }

    public boolean insertParticipant(Participant participant) throws ExecutionException, InterruptedException {
        CompletableFuture<Participant> future = getParticipant(participant);

        if (future.get() == null){
            service.insertParticipant(participant);
            return true;
        } else {
            return false;
        }
    }

    public CompletableFuture<Long> deleteParticipant(Participant participant){
        return service.deleteParticipant(participant);
    }
}
