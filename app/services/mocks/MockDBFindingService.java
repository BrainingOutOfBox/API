package services.mocks;

import models.bo.*;
import services.database.DBFindingInterface;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MockDBFindingService implements DBFindingInterface {

    private ArrayList<Brainsheet> brainsheets = new ArrayList<>();
    private ArrayList<Brainwave> brainwaves = new ArrayList<>();
    private ArrayList<Idea> ideas = new ArrayList<>();
    private BrainstormingFinding finding;

    @Override
    public CompletableFuture<BrainstormingFinding> getFinding(String id) {
        CompletableFuture<BrainstormingFinding> future = new CompletableFuture<>();

        ideas.add(new NoteIdea(""));
        brainwaves.add(new Brainwave(0, this.ideas));
        brainsheets.add(new Brainsheet(0, this.brainwaves));
        finding = new BrainstormingFinding("TestFinding", "Test", 2, 3, 0, "", "software", brainsheets, 0, "1111");
        finding.setIdentifier("2222");

        if (id.equals("2222")) {
            future.complete(finding);
        } else {
            future.complete(null);
        }

        return future;
    }

    @Override
    public CompletableFuture<Queue<BrainstormingFinding>> getAllFindingsOfTeam(BrainstormingTeam brainstormingTeam) {
        CompletableFuture<Queue<BrainstormingFinding>> future = new CompletableFuture<>();
        Queue<BrainstormingFinding> queue = new ConcurrentLinkedQueue<>();

        ideas.add(new NoteIdea(""));
        brainwaves.add(new Brainwave(0, this.ideas));
        brainsheets.add(new Brainsheet(0, this.brainwaves));
        finding = new BrainstormingFinding("TestFinding", "Test", 2, 3, 0, "", "software", brainsheets, 0, "1111");
        finding.setIdentifier("2222");

        queue.add(finding);
        future.complete(queue);

        return future;
    }

    @Override
    public void insertFinding(BrainstormingFinding finding) {

    }

    @Override
    public void exchangeBrainsheet(BrainstormingFinding finding, Brainsheet oldBrainsheet, Brainsheet newBrainsheet) {

    }

    @Override
    public void nextRound(BrainstormingFinding finding) {

    }

    @Override
    public void lastRound(BrainstormingFinding finding) {

    }
}
