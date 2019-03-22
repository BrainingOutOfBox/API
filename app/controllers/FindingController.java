package controllers;

import io.swagger.annotations.*;
import mappers.ModelsMapper;
import models.*;
import models.bo.*;
import models.dto.BrainsheetDTO;
import models.dto.BrainstormingFindingDTO;
import org.joda.time.DateTime;
import parsers.BrainsheetDTOBodyParser;
import parsers.BrainstormingFindingDTOBodyParser;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.MongoDBFindingService;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;


@Api(value = "/BrainstormingFinding", description = "All operations with brainstormingFindings", produces = "application/json")
public class FindingController extends Controller {

    @Inject
    private MongoDBFindingService service;
    @Inject
    private ModelsMapper modelsMapper;

    @ApiOperation(
            nickname = "createBrainstormingFinding",
            value = "Create a brainstormingFinding",
            notes = "With this method you can create a brainstormingFinding",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(BrainstormingFindingDTOBodyParser.class)
    public Result createBrainstormingFindingForTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {
        TeamController teamController = new TeamController();
        BrainstormingTeam team = teamController.getBrainstormingTeam(teamIdentifier);

        BrainstormingFindingDTO brainstormingFindingDTO = request().body().as(BrainstormingFindingDTO.class);

        BrainstormingFinding finding;
        if (team != null) {
            finding = createBrainstormingFinding(brainstormingFindingDTO, team);
            service.insertFinding(finding);
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam with this identifier found")));
        }

        return ok(Json.toJson(new SuccessMessage("Success", finding.getIdentifier())));
    }



    @ApiOperation(
            nickname = "getBrainstormingFindings",
            value = "Get all brainstormingFindings",
            notes = "With this method you can get all brainstormingFindings from a specific team",
            httpMethod = "GET",
            response = BrainstormingFinding.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingFinding.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingFindingFromTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingTeam team = new BrainstormingTeam();
        team.setIdentifier(teamIdentifier);

        CompletableFuture<Queue<BrainstormingFinding>> future = service.getAllFindingsOfTeam(team);
        Queue<BrainstormingFindingDTO> list = new LinkedList<>();
        for (BrainstormingFinding brainstormingFinding : future.get()) {
            BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(brainstormingFinding);
            list.add(brainstormingFindingDTO);
        }
        return ok(Json.toJson(list));
    }

    @ApiOperation(
            nickname = "getBrainstormingFinding",
            value = "Get brainstormingFinding",
            notes = "With this method you can get a brainstormingFinding by its identifier",
            httpMethod = "GET",
            response = BrainstormingFinding.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingFinding.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingFindingByIdentifier(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier) throws ExecutionException, InterruptedException {

        BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);
        BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(finding);

        if (finding != null) {
            return ok(Json.toJson(brainstormingFindingDTO));
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
        }
    }

    @ApiOperation(
            nickname = "putBrainsheet",
            value = "Put a updated brainsheet",
            notes = "With this method you can update the brainsheets from a brainstormingFinding",
            httpMethod = "PUT",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(BrainsheetDTOBodyParser.class)
    public Result putBrainsheet(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier) throws ExecutionException, InterruptedException {
        BrainsheetDTO brainsheetDTO = request().body().as(BrainsheetDTO.class);
        Brainsheet newBrainsheet = modelsMapper.toBrainsheet(brainsheetDTO);

        BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);

        if (finding == null){
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
        }

        Brainsheet oldBrainsheet = finding.getBrainsheets().get(brainsheetDTO.getNrOfSheet());
        service.exchangeBrainsheet(finding, oldBrainsheet, newBrainsheet);

        return ok(Json.toJson(new SuccessMessage("Success", "Brainsheet successfully updated")));
    }

    @ApiOperation(
            nickname = "calculateRemainingTime",
            value = "Calculate remaining time",
            notes = "With this method you can calculate the remaining time of the current round in the selected BrainstormFinding",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result calculateRemainingTimeOfFinding(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier) throws ExecutionException, InterruptedException {
        long difference = 0;
        BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);
        if (finding != null && !finding.getCurrentRoundEndTime().isEmpty()) {
            DateTime currentRoundEndTime = DateTime.parse(finding.getCurrentRoundEndTime());
            DateTime nowDate = new DateTime();
            difference = getDateDiff(currentRoundEndTime, nowDate, TimeUnit.MILLISECONDS);
        }
        return ok(Json.toJson(difference));

    }

    @ApiOperation(
            nickname = "startBrainstormingFinding",
            value = "start a brainstormingFinding",
            notes = "With this method you can start the brainstormingFinding",
            httpMethod = "PUT",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result startBrainstorming(String findingIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);
        if(finding != null && finding.getCurrentRound() == 0) {
            startWatcherForBrainstormingFinding(finding.getIdentifier());
            return nextRound(finding);
        }
        return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found or brainstormingFinding has already started or ended")));
    }

    private void startWatcherForBrainstormingFinding(String findingIdentifier){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);
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

    private Result nextRound(BrainstormingFinding finding){
        if (finding != null) {
            service.nextRound(finding);
            return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully updated")));
        }

        return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
    }

    private Result lastRound(BrainstormingFinding finding){
        if (finding != null) {
            service.lastRound(finding);
            return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully updated")));
        }

        return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
    }

    private BrainstormingFinding getBrainstormingFinding(String findingIdentifier) throws ExecutionException, InterruptedException {
        CompletableFuture<BrainstormingFinding> future = service.getFinding(findingIdentifier);
        return future.get();
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

    private static long getDateDiff(DateTime date1, DateTime date2, TimeUnit timeUnit) {
        long diffInMillisecondes = date1.getMillis() - date2.getMillis();
        return timeUnit.convert(diffInMillisecondes,TimeUnit.MILLISECONDS);
    }

}
