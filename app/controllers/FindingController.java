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
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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
            if (team != null) {
                BrainstormingFinding finding = createBrainstormingFinding(body, team);

                findingCollection.insertOne(finding, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(Void result, Throwable t) {
                        Logger.info("Inserted BrainstormFinding!");
                    }
                });
            } else {
                return ok(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam with this identifier found")));
            }

        return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully inserted")));
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }


    @ApiOperation(
            nickname = "getBrainstormingFinding",
            value = "Get all brainstormingFinding",
            notes = "With this method you can get all brainstormingFinding from a specific team",
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
                    Logger.info("Get all BrainstormFinding for team!");
                    future.complete(queue);
                }
            });

        return ok(Json.toJson(future.get()));
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
                1,
                new DateTime().plusMinutes(body.get("baseRoundTime").asInt()).toString(),
                brainsheets,
                team.getIdentifier());

        return finding;
    }

}
