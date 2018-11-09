package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.*;
import models.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Api(value = "/Team", description = "All operations with team", produces = "application/json")
public class TeamController extends Controller {

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<BrainstormingTeam> teamCollection;

    public TeamController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        teamCollection = database.getCollection("BrainstormingTeam", BrainstormingTeam.class);

    }

    @ApiOperation(
            nickname = "createBrainstormingTeam",
            value = "Create a brainstormingTeam",
            notes = "With this method you can create a brainstormingTeam",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result createBrainstormingTeam(){

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("name") &&
                    body.hasNonNull("purpose") &&
                    body.hasNonNull("nrOfParticipants") &&
                    body.hasNonNull("moderator")) {

            ArrayList<Participant> participants = new ArrayList<>(body.get("nrOfParticipants").asInt());
            Participant moderator = new Participant(body.findPath("username").asText() , body.findPath("password").asText(), body.findPath("firstname").asText(), body.findPath("lastname").asText());
            participants.add(moderator);
            BrainstormingTeam team = new BrainstormingTeam(body.get("name").asText(), body.get("purpose").asText(), body.get("nrOfParticipants").asInt(), 1, participants, moderator);

            teamCollection.insertOne(team, new SingleResultCallback<Void>() {
                @Override
                public void onResult(Void result, Throwable t) {
                    Logger.info("Inserted BrainstormingTeam!");
                }
            });

            return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingTeam successfully inserted")));
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }

    @ApiOperation(
            nickname = "joinBrainstormingTeam",
            value = "Join a brainstormingTeam",
            notes = "With this method you can join a brainstormingTeam",
            httpMethod = "PUT",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result joinBrainstormingTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("username") &&
                    body.hasNonNull("password") &&
                    body.hasNonNull("firstname") &&
                    body.hasNonNull("lastname")) {

            Participant participant = new Participant(body.findPath("username").asText() , body.findPath("password").asText(), body.findPath("firstname").asText(), body.findPath("lastname").asText());
            BrainstormingTeam brainstormingTeam = getBrainstormingTeam(teamIdentifier);

            if (brainstormingTeam!= null && brainstormingTeam.getNrOfParticipants() > brainstormingTeam.getCurrentNrOfParticipants() && brainstormingTeam.joinTeam(participant)) {

                teamCollection.updateOne(eq("identifier", teamIdentifier),combine(set("participants", brainstormingTeam.getParticipants()), inc("currentNrOfParticipants", 1)), new SingleResultCallback<UpdateResult>() {
                    @Override
                    public void onResult(final UpdateResult result, final Throwable t) {
                        Logger.info(result.getModifiedCount() + " Team successfully updated");
                    }
                });

                return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully added to the brainstormingTeam")));

            } else {
                return internalServerError(Json.toJson(new ErrorMessage("Error", "The limit of the team size is reached or the participant is already in the brainstormingTeam or this team does not exist")));
            }
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }

    @ApiOperation(
            nickname = "leaveBrainstormingTeam",
            value = "Leave a brainstormingTeam",
            notes = "With this method you can leave a brainstormingTeam",
            httpMethod = "PUT",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result leaveBrainstormingTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("username") &&
                    body.hasNonNull("password") &&
                    body.hasNonNull("firstname") &&
                    body.hasNonNull("lastname")) {

            Participant participant = new Participant(body.findPath("username").asText() , body.findPath("password").asText(), body.findPath("firstname").asText(), body.findPath("lastname").asText());
            BrainstormingTeam brainstormingTeam = getBrainstormingTeam(teamIdentifier);

            if (brainstormingTeam != null && brainstormingTeam.getCurrentNrOfParticipants() > 0 && brainstormingTeam.leaveTeam(participant)) {

                teamCollection.updateOne(eq("identifier", teamIdentifier),combine(set("participants", brainstormingTeam.getParticipants()), inc("currentNrOfParticipants", -1)), new SingleResultCallback<UpdateResult>() {
                    @Override
                    public void onResult(final UpdateResult result, final Throwable t) {
                        Logger.info(result.getModifiedCount() + " Team successfully updated");
                    }
                });

                return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully removed from the brainstormingTeam")));

            } else {
                return internalServerError(Json.toJson(new ErrorMessage("Error", "There are no more participants in the brainstormingTeam or the participant has already left the brainstormingTeam or this team does not exist")));
            }
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }

    @ApiOperation(
            nickname = "deleteBrainstormingTeam",
            value = "Delete a brainstormingTeam",
            notes = "With this method you can delete a brainstormingTeam",
            httpMethod = "DELETE",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result deleteBrainstormingTeam() throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("identifier") &&
                    body.hasNonNull("moderator")) {

            CompletableFuture<DeleteResult> future = new CompletableFuture<>();

            teamCollection.deleteOne(and(   eq("identifier", body.get("identifier").asText()),
                                            eq("moderator.username", body.findPath("username").asText()),
                                            eq("moderator.password", body.findPath("password").asText())), new SingleResultCallback<DeleteResult>() {
                @Override
                public void onResult(final DeleteResult result, final Throwable t) {
                    Logger.info(result.getDeletedCount() + " Team successfully deleted");
                    future.complete(result);
                }
            });

            if (future.get().getDeletedCount() > 0){
                return ok(Json.toJson(new SuccessMessage("Success", "Team successfully deleted")));
            } else {
                return internalServerError(Json.toJson(new ErrorMessage("Error", "No Team deleted! Does the identifier exist and is moderator's username and password correct?")));
            }
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }


    @ApiOperation(
            nickname = "getBrainstormingTeamForParticipant",
            value = "Get all brainstormingTeams",
            notes = "With this method you can get all brainstormingTeams for specific participant",
            httpMethod = "GET",
            response = BrainstormingTeam.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingTeam.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingTeamForParticipant(@ApiParam(value = "Participant username", name = "participant", required = true) String participant) throws ExecutionException, InterruptedException {
        CompletableFuture<Queue<BrainstormingTeam>> future = new CompletableFuture<>();
        Queue<BrainstormingTeam> queue = new ConcurrentLinkedQueue<>();

        teamCollection.find(eq("participants.username", participant)).forEach(
                new Block<BrainstormingTeam>() {
                    @Override
                    public void apply(final BrainstormingTeam team) {
                        queue.add(team);
                    }
                }, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(final Void result, final Throwable t) {
                        Logger.info("Get all BrainstormingTeams for participant!");
                        future.complete(queue);
                    }
                });

        return ok(Json.toJson(future.get()));
    }

    @ApiOperation(
            nickname = "getBrainstormingTeam",
            value = "Get a brainstormingTeam",
            notes = "With this method you can get a brainstormingTeam by its identifier",
            httpMethod = "GET",
            response = BrainstormingTeam.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingTeam.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingTeamByIdentifier(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {
        BrainstormingTeam team = getBrainstormingTeam(teamIdentifier);

        if (team != null) {
            return ok(Json.toJson(team));
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam found")));
        }
    }

    public BrainstormingTeam getBrainstormingTeam(String teamIdentifier) throws ExecutionException, InterruptedException {
        CompletableFuture<BrainstormingTeam> future = new CompletableFuture<>();

        teamCollection.find(eq("identifier", teamIdentifier)).first(new SingleResultCallback<BrainstormingTeam>() {
            @Override
            public void onResult(BrainstormingTeam team, Throwable t) {
                if (team != null) {
                    Logger.info("Found brainstormingTeam");
                    future.complete(team);
                } else {
                    future.complete(null);
                }
            }
        });

        BrainstormingTeam team = future.get();

        if (team != null) {
            return team;
        } else {
            return null;
        }
    }

}