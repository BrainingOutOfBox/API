package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.typesafe.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import models.ErrorMessage;
import models.Participant;
import models.SuccessMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Api(value = "/Participant", description = "All operations with participant", produces = "application/json")
public class ParticipantController extends Controller {

    @Inject
    private Config config;

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<Participant> participantCollection;

    public ParticipantController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        participantCollection = database.getCollection("Participant", Participant.class);

    }

    @ApiOperation(
            nickname = "login",
            value = "Login",
            notes = "With this method you can login an get a jwt Token",
            httpMethod = "GET",
            response = Participant.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Participant.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result login() throws UnsupportedEncodingException, ExecutionException, InterruptedException {
        JsonNode body = request().body().asJson();

        if (body == null) {
            Logger.error("json body is null");
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        }

        if (body.hasNonNull("username") && body.hasNonNull("password")) {
            CompletableFuture<Participant> future = new CompletableFuture<>();

            participantCollection.find(and( eq("username", body.get("username").asText()),
                                            eq("password", body.get("password").asText()))).first(new SingleResultCallback<Participant>() {
                @Override
                public void onResult(Participant participant, Throwable t) {
                    if (participant != null) {
                        Logger.info("Found participant");
                        future.complete(participant);
                    } else {
                        future.complete(null);
                    }
                }
            });

            if (future.get()!= null){
                ObjectNode result = Json.newObject();
                result.putPOJO("participant", future.get());
                result.put("access_token", getSignedToken(7l));
                return ok(result);
            } else {
                Logger.info("username or password not correct");
                return forbidden(Json.toJson(new ErrorMessage("Error", "username or password not correct")));
            }

        } else {
            Logger.error("json body not as expected: {}", body.toString());
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
        }

    }

    @ApiOperation(
            nickname = "createParticipant",
            value = "Create a participant",
            notes = "With this method you can create a participant",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result createParticipant(){

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("username") &&
                body.hasNonNull("password") &&
                body.hasNonNull("firstname") &&
                body.hasNonNull("lastname")) {

            Participant participant = new Participant(body.get("username").asText(), body.get("password").asText(), body.get("firstname").asText(), body.get("lastname").asText());

            participantCollection.insertOne(participant, new SingleResultCallback<Void>() {
                @Override
                public void onResult(Void result, Throwable t) {
                    Logger.info("Inserted Participant!");
                }
            });

            return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully inserted")));
        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }

    @ApiOperation(
            nickname = "deleteParticipant",
            value = "Delete a participant",
            notes = "With this method you can delete a participant",
            httpMethod = "DELETE",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result deleteParticipant() throws ExecutionException, InterruptedException {

        JsonNode body = request().body().asJson();

        if (body == null ) {
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        } else if(  body.hasNonNull("username") &&
                body.hasNonNull("password") &&
                body.hasNonNull("firstname") &&
                body.hasNonNull("lastname")) {

            CompletableFuture<DeleteResult> future = new CompletableFuture<>();

            participantCollection.deleteOne(and(   eq("username", body.get("username").asText()),
                                        eq("password", body.get("password").asText())), new SingleResultCallback<DeleteResult>() {
                @Override
                public void onResult(final DeleteResult result, final Throwable t) {
                    Logger.info(result.getDeletedCount() + " Participant successfully deleted");
                    future.complete(result);
                }
            });

            if (future.get().getDeletedCount() > 0){
                return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully deleted")));
            } else {
                return internalServerError(Json.toJson(new ErrorMessage("Error", "No Participant deleted! Is username and password correct?")));
            }

        }

        return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
    }

    private String getSignedToken(Long userId) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("ThePlayApp")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(120).toInstant()))
                .sign(algorithm);
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok(Json.toJson(new SuccessMessage("Success", "access granted via filter")));
        }).orElse(forbidden(Json.toJson(new ErrorMessage("Error","eh, no verified jwt found"))));
    }
}
