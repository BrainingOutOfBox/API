package services.mocks;

import models.bo.BrainstormingTeam;
import models.bo.Participant;
import services.database.DBTeamInterface;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MockDBTeamService implements DBTeamInterface {

    private Participant moderator = new Participant("TestParticipant", "MirEgal", "Max", "Mustermann");
    private ArrayList<Participant> membersList = new ArrayList<>();
    private BrainstormingTeam team = new BrainstormingTeam("NotInDBTeam", "Test", 4, 1, membersList, moderator);

    @Override
    public CompletableFuture<BrainstormingTeam> getTeam(String id) {
        CompletableFuture<BrainstormingTeam> future = new CompletableFuture<>();

        membersList.add(moderator);
        team.setIdentifier("1111");
        future.complete(team);

        return future;
    }

    @Override
    public CompletableFuture<Queue<BrainstormingTeam>> getAllTeamsOfParticipant(Participant participant) {
        CompletableFuture<Queue<BrainstormingTeam>> future = new CompletableFuture<>();
        Queue<BrainstormingTeam> queue = new ConcurrentLinkedQueue<>();

        membersList.add(moderator);
        team.setIdentifier("1111");
        queue.add(team);
        future.complete(queue);

        return future;
    }

    @Override
    public void changeTeamMembers(BrainstormingTeam team, Number number) {

    }

    @Override
    public void insertTeam(BrainstormingTeam team) {

    }

    @Override
    public CompletableFuture<Long> deleteTeam(BrainstormingTeam team) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        future.complete(1L);
        return future;
    }
}
