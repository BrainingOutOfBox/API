package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.typesafe.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import mappers.ModelsMapper;
import models.ErrorMessage;
import models.bo.Participant;
import models.SuccessMessage;
import models.dto.ParticipantDTO;
import parsers.ParticipantDTOBodyParser;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.business.ParticipantService;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(value = "/Participant", description = "All operations with participant", produces = "application/json")
public class ParticipantController extends Controller {

    @Inject
    private Config config;
    @Inject
    private ParticipantService service;
    @Inject
    private ModelsMapper modelsMapper;

    @ApiOperation(
            nickname = "login",
            value = "Login",
            notes = "With this method you can login an get a jwt Token",
            httpMethod = "GET",
            response = Participant.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Participant.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(ParticipantDTOBodyParser.class)
    public Result login() throws UnsupportedEncodingException, ExecutionException, InterruptedException {
        ParticipantDTO participantDTO = request().body().as(ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);

        CompletableFuture<Participant> future = service.getParticipant(participant);

        if (future.get() != null){
            participantDTO = modelsMapper.toParticipantDTO(future.get());
            participantDTO.setAccessToken(getSignedToken(participantDTO.getUsername()));
            return ok(Json.toJson(participantDTO));

        } else {
            Logger.info("Username or password not correct");
            return forbidden(Json.toJson(new ErrorMessage("Error", "Username or password not correct")));
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
    @BodyParser.Of(ParticipantDTOBodyParser.class)
    public Result createParticipant() throws ExecutionException, InterruptedException {

        ParticipantDTO participantDTO = request().body().as(ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);
        CompletableFuture<Participant> future = service.getParticipant(participant);

        if (future.get() == null){
            service.insertParticipant(participant);
            return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully inserted")));
        } else {
            Logger.info("Username already exists");
            return internalServerError(Json.toJson(new ErrorMessage("Error", "Username already exists")));
        }
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
    @BodyParser.Of(ParticipantDTOBodyParser.class)
    public Result deleteParticipant() throws ExecutionException, InterruptedException {

        ParticipantDTO participantDTO = request().body().as(ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);

        CompletableFuture<Long> future = service.deleteParticipant(participant);

        if (future.get() > 0){
            return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully deleted")));
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No Participant deleted! Is username and password correct?")));
        }
    }

    private String getSignedToken(String username) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("ThePlayApp")
                .withClaim("user", username)
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
