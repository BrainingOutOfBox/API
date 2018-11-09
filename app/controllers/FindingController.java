package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.*;
import models.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;


@Api(value = "/BrainstormingFinding", description = "All operations with brainstormingFindings", produces = "application/json")
public class FindingController extends Controller {

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<BrainstormingFinding> findingCollection;


    public FindingController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        findingCollection = database.getCollection("BrainstormingFinding", BrainstormingFinding.class);
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

            TeamController teamController = new TeamController();
            BrainstormingTeam team = teamController.getBrainstormingTeam(teamIdentifier);
            BrainstormingFinding finding;
            if (team != null) {
                finding = createBrainstormingFinding(body, team);

                findingCollection.insertOne(finding, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(Void result, Throwable t) {
                        Logger.info("Inserted BrainstormFinding!");
                    }
                });
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

        CompletableFuture<Queue<BrainstormingFinding>> future = new CompletableFuture<>();
        Queue<BrainstormingFinding> queue = new ConcurrentLinkedQueue<>();

        findingCollection.find(eq("brainstormingTeam", teamIdentifier)).forEach(
            new Block<BrainstormingFinding>() {
                @Override
                public void apply(final BrainstormingFinding finding) {
                    queue.add(finding);
                }
            }, new SingleResultCallback<Void>() {
                @Override
                public void onResult(final Void result, final Throwable t) {
                    Logger.info("Get all BrainstormFindings for team!");
                    future.complete(queue);
                }
            });

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


            findingCollection.updateOne(eq("identifier", findingIdentifier),pullByFilter(Filters.eq("brainsheets", oldBrainsheet)), new SingleResultCallback<UpdateResult>() {
                @Override
                public void onResult(final UpdateResult result, final Throwable t) {
                    Logger.info(result.getModifiedCount() + " Brainsheet successfully deleted");
                }
            });

            findingCollection.updateOne(eq("identifier", findingIdentifier),pushEach("brainsheets", Arrays.asList(newBrainsheet), new PushOptions().position(newBrainsheet.getNrOfSheet())), new SingleResultCallback<UpdateResult>() {
                @Override
                public void onResult(final UpdateResult result, final Throwable t) {
                    Logger.info(result.getModifiedCount() + " Brainsheet successfully inserted");
                }
            });

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
    public Result startBrainstorming(String findingIdentifer) throws ExecutionException, InterruptedException {
        return nextRound(findingIdentifer);
    }

    private Result nextRound(String findingIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingFinding finding = getBrainstormingFinding(findingIdentifier);

        if (finding != null) {

            findingCollection.updateOne(eq("identifier", findingIdentifier), combine(set("currentRoundEndTime", new DateTime().plusMinutes(finding.getBaseRoundTime()).toString()), inc("currentRound", 1)), new SingleResultCallback<UpdateResult>() {
                @Override
                public void onResult(final UpdateResult result, final Throwable t) {
                    Logger.info(result.getModifiedCount() + " BrainstormingFinding successfully updated");
                }
            });

            return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully updated")));
        }

        return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
    }

    private BrainstormingFinding getBrainstormingFinding(String findingIdentifier) throws ExecutionException, InterruptedException {

        CompletableFuture<BrainstormingFinding> future = new CompletableFuture<>();

        findingCollection.find(eq("identifier", findingIdentifier)).first(new SingleResultCallback<BrainstormingFinding>() {
            @Override
            public void onResult(BrainstormingFinding result, Throwable t) {
                Logger.info("Get BrainstormingFinding by identifier!");
                future.complete(result);
            }
        });

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
                team.getIdentifier());

        return finding;
    }

}
