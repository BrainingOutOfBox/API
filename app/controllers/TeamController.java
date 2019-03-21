package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.result.DeleteResult;
import io.swagger.annotations.*;
import mappers.ModelsMapper;
import models.*;
import models.bo.BrainstormingTeam;
import models.bo.Participant;
import models.dto.BrainstormingTeamDTO;
import parsers.BrainstormingTeamDTOBodyParser;
import play.libs.Json;
import play.mvc.*;
import services.MongoDBTeamService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(value = "/Team", description = "All operations with team", produces = "application/json")
public class TeamController extends Controller {

    private MongoDBTeamService service;
    private ModelsMapper modelsMapper;

    @Inject
    TeamController(MongoDBTeamService mongoDBTeamService, ModelsMapper modelsMapper){
        this.service = mongoDBTeamService;
        this.modelsMapper = modelsMapper;
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
    @BodyParser.Of(BrainstormingTeamDTOBodyParser.class)
    public Result createBrainstormingTeam(){
        BrainstormingTeamDTO brainstormingTeamDTO = request().body().as(BrainstormingTeamDTO.class);
        BrainstormingTeam brainstormingTeam = modelsMapper.toBrainstormingTeam(brainstormingTeamDTO);

        Participant participant = new Participant(brainstormingTeamDTO.getModerator().getUsername(), brainstormingTeamDTO.getModerator().getPassword(), brainstormingTeamDTO.getModerator().getFirstname(), brainstormingTeamDTO.getModerator().getLastname());
        brainstormingTeam.getParticipants().add(participant);

        service.insertTeam(brainstormingTeam);
        return ok(Json.toJson(new SuccessMessage("Success", brainstormingTeam.getIdentifier())));

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

                service.changeTeamMembers(brainstormingTeam, 1);
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

                service.changeTeamMembers(brainstormingTeam, -1);
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

            BrainstormingTeam team = new BrainstormingTeam();
            Participant participant = new Participant();

            team.setIdentifier(body.findPath("identifier").asText());
            participant.setUsername(body.findPath("username").asText());
            participant.setPassword(body.findPath("password").asText());
            team.setModerator(participant);

            CompletableFuture<DeleteResult> future = service.deleteTeam(team);

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
    public Result getBrainstormingTeamForParticipant(@ApiParam(value = "Participant username", name = "username", required = true) String username) throws ExecutionException, InterruptedException {
        Participant participant = new Participant();
        participant.setUsername(username);

        CompletableFuture<Queue<BrainstormingTeam>> future = service.getAllTeamsOfParticipant(participant);
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
        CompletableFuture<BrainstormingTeam> future = service.getTeam(teamIdentifier);
        return future.get();
    }

}