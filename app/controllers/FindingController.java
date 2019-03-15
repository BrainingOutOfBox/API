package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.*;
import models.*;
import org.joda.time.DateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import services.MongoDBFindingService;
import services.MongoDBTeamService;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;


@Api(value = "/BrainstormingFinding", description = "All operations with brainstormingFindings", produces = "application/json")
public class FindingController extends Controller {

    private MongoDBFindingService service;
    private MongoDBTeamService teamService;

    @Inject
    public FindingController(MongoDBFindingService mongoDBFindingService, MongoDBTeamService mongoDBTeamService){
        this.service = mongoDBFindingService;
        this.teamService = mongoDBTeamService;
    }

    @ApiOperation(
            nickname = "createBrainstormingFinding",
            value = "Create a brainstormingFinding",
            notes = "With this method you can create a brainstormingFinding",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result createBrainstormingFindingForTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("name") &&
                    body.hasNonNull("problemDescription") &&
                    body.hasNonNull("nrOfIdeas") &&
                    body.hasNonNull("baseRoundTime")) {

            TeamController teamController = new TeamController(teamService);
            BrainstormingTeam team = teamController.getBrainstormingTeam(teamIdentifier);
            BrainstormingFinding finding;
            if (team != null) {
                finding = createBrainstormingFinding(body, team);
                service.insertFinding(finding);

            } else {
                return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam with this identifier found")));
            }


        return ok(Json.toJson(new SuccessMessage("Success", finding.getIdentifier())));
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
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
        return ok(Json.toJson(future.get()));
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

        if (finding != null) {
            return ok(Json.toJson(finding));
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
    public Result putBrainsheet(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier) throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();
        JsonNode brainwaves = body.findPath("brainwaves");
        JsonNode nrOfSheet = body.findPath("nrOfSheet");

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  !brainwaves.isNull() &&
                    !nrOfSheet.isNull()){

            BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);

            if (finding == null){
                return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
            }

            Brainsheet oldBrainsheet = finding.getBrainsheets().get(nrOfSheet.asInt());
            Brainsheet newBrainsheet = createBrainsheet(body);

            service.exchangeBrainsheet(finding, oldBrainsheet, newBrainsheet);

            return ok(Json.toJson(new SuccessMessage("Success", "Brainsheet successfully updated")));
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
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

    private Brainsheet createBrainsheet(JsonNode body){
        JsonNode brainwaves = body.findPath("brainwaves");
        JsonNode nrOfSheet = body.findPath("nrOfSheet");

        Brainsheet brainsheet = new Brainsheet();

        for (JsonNode wave : brainwaves){
            JsonNode ideas = wave.findPath("ideas");
            Brainwave brainwave = new Brainwave();

            for (JsonNode idea : ideas){
                brainwave.addIdea(new Idea(idea.findPath("description").asText()));
            }

            brainwave.setNrOfBrainwave(wave.findPath("nrOfBrainwave").asInt());
            brainsheet.addBrainwave(brainwave);
        }

        brainsheet.setNrOfSheet(nrOfSheet.asInt());

        return brainsheet;
    }

    private BrainstormingFinding createBrainstormingFinding(JsonNode body, BrainstormingTeam team) {

        ArrayList<Brainsheet> brainsheets = new ArrayList<>();
        ArrayList<Brainwave> brainwaves = new ArrayList<>();
        ArrayList<Idea> ideas = new ArrayList<>();

        //creating ideas
        for (int k = 0; k < body.get("nrOfIdeas").asInt(); k++){
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

        BrainstormingFinding finding = new BrainstormingFinding(
                body.get("name").asText(),
                body.get("problemDescription").asText(),
                body.get("nrOfIdeas").asInt(),
                body.get("baseRoundTime").asInt(),
                0,
                "",
                brainsheets,
                0,
                team.getIdentifier());

        return finding;
    }

}
