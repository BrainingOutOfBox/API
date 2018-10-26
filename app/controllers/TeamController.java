package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.typesafe.config.Config;
import io.swagger.annotations.*;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import models.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Api(value = "/Team", description = "All operations with team", produces = "application/json")
public class TeamController extends Controller {

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<BrainstormingTeam> collection;

    public TeamController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("BrainstormingTeam", BrainstormingTeam.class);

    }

    @ApiOperation(
            nickname = "createBrainstormingTeam",
            value = "Create a brainstormingTeam",
            notes = "With this method you can create a brainstormingTeam",
            httpMethod = "POST",
            response = BrainstormingTeam.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingTeam.class),
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

            collection.insertOne(team, new SingleResultCallback<Void>() {
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
            nickname = "getBrainstormingTeam",
            value = "Get all brainstormingTeam",
            notes = "With this method you can get all brainstormingTeams for specific participant",
            httpMethod = "GET",
            response = BrainstormingTeam.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingTeam.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingTeamForParticipant(@ApiParam(value = "Participant username", name = "participant", required = true) String participant) throws ExecutionException, InterruptedException {
        CompletableFuture<Queue<BrainstormingTeam>> future = new CompletableFuture<>();
        Queue<BrainstormingTeam> queue = new ConcurrentLinkedQueue<>();

        collection.find(eq("participants.username", participant)).forEach(
                new Block<BrainstormingTeam>() {
                    @Override
                    public void apply(final BrainstormingTeam team) {
                        queue.add(team);
                    }
                }, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(final Void result, final Throwable t) {
                        Logger.info("Get all BrainstormTeam for participant!");
                        future.complete(queue);
                    }
                });

        return ok(Json.toJson(future.get()));
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok(Json.toJson(new SuccessMessage("Success", "access granted via filter")));
        }).orElse(forbidden(Json.toJson(new ErrorMessage("Error","eh, no verified jwt found"))));
    }



}
