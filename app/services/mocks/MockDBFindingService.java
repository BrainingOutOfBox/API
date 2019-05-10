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

        ideas.add(new NoteIdea("Demo Description"));
        ideas.add(new SketchIdea("Demo Sketch", "5cacac634b0a388f2de8418f"));
        ideas.add(new PatternIdea("API Key","How can an API provider identify and authenticate different clients (that make requests)?", "As an API provider, assign each client a unique token – the API Key – that the client can present to the API endpoint for identification purposes.", "https://microservice-api-patterns.org/patterns/quality/qualityManagementAndGovernance/APIKey", "software", "5cc16f231c2cb6761529cd6c"));

        brainwaves.add(new Brainwave(0, this.ideas));
        brainwaves.add(new Brainwave(1, this.ideas));
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
    public void exchangeBrainsheet(BrainstormingFinding finding, Brainsheet newBrainsheet) {

    }

    @Override
    public void nextRound(BrainstormingFinding finding) {

    }

    @Override
    public void lastRound(BrainstormingFinding finding) {

    }
}
