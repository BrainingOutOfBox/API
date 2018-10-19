package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import io.swagger.annotations.*;
import models.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.joda.time.DateTime;
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
    MongoCollection<BrainstormingFinding> collection;


    public FindingController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("BrainstormingFinding", BrainstormingFinding.class);

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
    public Result createBrainstormingFindingForTeam(@ApiParam(value = "BrainstormingTeam Name", name = "teamName", required = true) String teamName){

        JsonNode body = request().body().asJson();
        BrainstormingFinding finding;

        if (body == null) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else {

            BrainstormingTeam team = new BrainstormingTeam("DemoTeam", "Demo", 4, new ArrayList<>(), new Participant());

            ArrayList<Brainsheet> brainsheets = new ArrayList<>();
            for(int i = 0; i < team.getNrOfParticipants(); i++){
                brainsheets.add(new Brainsheet());
            }

            finding = new BrainstormingFinding(
                    body.findPath("name").textValue(),
                    body.findPath("problemDescription").textValue(),
                    body.findPath("nrOfIdeas").asInt(),
                    body.findPath("baseRoundTime").asInt(),
                    1,
                    new DateTime().plusMinutes(body.findPath("baseRoundTime").asInt()).toString(),
                    brainsheets,
                    team);
        }


        collection.insertOne(finding, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable t) {
                System.out.println("Inserted!");
            }
        });

        return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully inserted")));
    }


    @ApiOperation(
            nickname = "getBrainstormingFinding",
            value = "Get all brainstormingFinding",
            notes = "With this method you can get all brainstormingFinding from a specific team",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingFindingFromTeam(@ApiParam(value = "BrainstormingTeam Name", name = "teamName", required = true) String teamName) throws ExecutionException, InterruptedException {

        CompletableFuture<Queue<BrainstormingFinding>> future = new CompletableFuture<>();
        Queue<BrainstormingFinding> queue = new ConcurrentLinkedQueue<>();

        collection.find(eq("brainstormingTeam.name", teamName)).forEach(
            new Block<BrainstormingFinding>() {
                @Override
                public void apply(final BrainstormingFinding document) {
                    queue.add(document);
                }
            }, new SingleResultCallback<Void>() {
                @Override
                public void onResult(final Void result, final Throwable t) {
                    System.out.println("Operation Finished!");
                    future.complete(queue);
                }
            });

        return ok(Json.toJson(future.get()));
    }

}
