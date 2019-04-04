package services.business;

import mappers.ModelsMapper;
import models.bo.*;
import models.dto.BrainstormingFindingDTO;
import org.joda.time.DateTime;
import services.database.DBFindingInterface;
import services.database.DBTeamInterface;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.*;

public class FindingService {
    @Inject
    private DBFindingInterface service;
    @Inject
    private DBTeamInterface teamService;
    @Inject
    private ModelsMapper modelsMapper;


    public String insertFinding(BrainstormingFindingDTO brainstormingFindingDTO, String teamIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingTeam team = teamService.getTeam(teamIdentifier).get();

        if (team != null) {
            BrainstormingFinding finding = createBrainstormingFinding(brainstormingFindingDTO, team);
            service.insertFinding(finding);
            return finding.getIdentifier();
        } else {
            return null;
        }

    }

    public CompletableFuture<Queue<BrainstormingFinding>> getAllFindingsOfTeam(BrainstormingTeam team){
        return service.getAllFindingsOfTeam(team);
    }

    public CompletableFuture<BrainstormingFinding> getFinding(String id){
        return service.getFinding(id);
    }

    public boolean exchangeBrainsheet(String findingIdentifier, Brainsheet newBrainsheet) throws ExecutionException, InterruptedException {
        BrainstormingFinding finding = service.getFinding(findingIdentifier).get();

        if (finding == null){
            return false;
        } else {
            if (newBrainsheet.getNrOfSheet() < finding.getBrainsheets().size()) {
                Brainsheet oldBrainsheet = finding.getBrainsheets().get(newBrainsheet.getNrOfSheet());
                service.exchangeBrainsheet(finding, oldBrainsheet, newBrainsheet);
                return true;
            }
            return false;
        }

    }

    public boolean startBrainstorming(String findingIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingFinding finding = service.getFinding(findingIdentifier).get();

        if(finding != null && finding.getCurrentRound() == 0) {
            startWatcherForBrainstormingFinding(finding.getIdentifier());
            nextRound(finding);
            return true;
        }
        return false;
    }

    private void nextRound(BrainstormingFinding finding){
        if (finding != null) {
            service.nextRound(finding);
        }
    }

    private void lastRound(BrainstormingFinding finding){
        if (finding != null) {
            service.lastRound(finding);
        }
    }

    public long calculateRemainingTimeOfFinding(String findingIdentifier) throws ExecutionException, InterruptedException {
        long difference = 0;
        BrainstormingFinding finding = service.getFinding(findingIdentifier).get();

        if (finding != null && !finding.getCurrentRoundEndTime().isEmpty()) {
            DateTime currentRoundEndTime = DateTime.parse(finding.getCurrentRoundEndTime());
            DateTime nowDate = new DateTime();
            difference = getDateDiff(currentRoundEndTime, nowDate, TimeUnit.MILLISECONDS);
        }
        return difference;
    }

    private void startWatcherForBrainstormingFinding(String findingIdentifier){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    BrainstormingFinding finding = service.getFinding(findingIdentifier).get();
                    DateTime endDateTime = DateTime.parse(finding.getCurrentRoundEndTime());

                    /*
                    System.out.println("Task started for " + finding.getIdentifier());
                    System.out.println(finding.getIdentifier() + "_Delivered Sheets: " + finding.getDeliveredBrainsheetsInCurrentRound());
                    System.out.println(finding.getIdentifier() + "_End Time " + finding.getCurrentRoundEndTime());
                    System.out.println(finding.getIdentifier() + "_Currend Round: " + finding.getCurrentRound());
                    */


                    if (endDateTime.plusSeconds(30).isBeforeNow() ||
                            finding.getDeliveredBrainsheetsInCurrentRound() >= finding.getBrainsheets().size()){

                        if (finding.getCurrentRound() == finding.getBrainsheets().size()){
                            //System.out.println("shutdown");
                            lastRound(finding);
                            executor.shutdown();
                        } else {
                            //System.out.println("next Round");
                            nextRound(finding);
                        }
                    }

                    //System.out.println("cancel");
                    cancel();

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        executor.scheduleAtFixedRate(task, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }


    private static long getDateDiff(DateTime date1, DateTime date2, TimeUnit timeUnit) {
        long diffInMillisecondes = date1.getMillis() - date2.getMillis();
        return timeUnit.convert(diffInMillisecondes,TimeUnit.MILLISECONDS);
    }

    private BrainstormingFinding createBrainstormingFinding(BrainstormingFindingDTO brainstormingFindingDTO, BrainstormingTeam team) {

        ArrayList<Brainsheet> brainsheets = new ArrayList<>();
        ArrayList<Brainwave> brainwaves = new ArrayList<>();
        ArrayList<Idea> ideas = new ArrayList<>();

        //creating ideas
        for (int k = 0; k < brainstormingFindingDTO.getNrOfIdeas(); k++){
            ideas.add(new Idea(""));
        }
        //creating brainwaves
        for (int j = 0; j < team.getNrOfParticipants(); j++){

            brainwaves.add(new Brainwave(j, ideas));
        }
        //creating brainsheets
        for(int i = 0; i < team.getNrOfParticipants(); i++){

            brainsheets.add(new Brainsheet(i, brainwaves));
        }

        BrainstormingFinding brainstormingFinding = modelsMapper.toBrainstormingFinding(brainstormingFindingDTO);
        brainstormingFinding.setCurrentRound(0);
        brainstormingFinding.setCurrentRoundEndTime("");
        brainstormingFinding.setBrainsheets(brainsheets);
        brainstormingFinding.setDeliveredBrainsheetsInCurrentRound(0);
        brainstormingFinding.setBrainstormingTeam(team.getIdentifier());

        return brainstormingFinding;
    }
}
