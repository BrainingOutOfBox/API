package controllers;

import io.swagger.annotations.*;
import mappers.ModelsMapper;
import models.*;
import models.bo.BrainstormingTeam;
import models.bo.Participant;
import models.dto.BrainstormingTeamDTO;
import models.dto.ParticipantDTO;
import parsers.BrainstormingTeamDTOBodyParser;
import parsers.ParticipantDTOBodyParser;
import play.libs.Json;
import play.mvc.*;
import services.business.TeamService;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(value = "/Team", description = "All operations with team", produces = "application/json")
public class TeamController extends Controller {

    @Inject
    private TeamService service;

    @Inject
    private ModelsMapper modelsMapper;

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
        brainstormingTeam.joinTeam(participant);
        brainstormingTeam.setCurrentNrOfParticipants(1);

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
    @BodyParser.Of(ParticipantDTOBodyParser.class)
    public Result joinBrainstormingTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {
        ParticipantDTO participantDTO = request().body().as(ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);
        BrainstormingTeam brainstormingTeam = getBrainstormingTeam(teamIdentifier);

        if (brainstormingTeam!= null && brainstormingTeam.getNrOfParticipants() > brainstormingTeam.getCurrentNrOfParticipants() && brainstormingTeam.joinTeam(participant)) {
            service.changeTeamMembers(brainstormingTeam, 1);
            return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully added to the brainstormingTeam")));

        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "The limit of the team size is reached or the participant is already in the brainstormingTeam or this team does not exist")));
        }

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
    @BodyParser.Of(ParticipantDTOBodyParser.class)
    public Result leaveBrainstormingTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier) throws ExecutionException, InterruptedException {
        ParticipantDTO participantDTO = request().body().as(ParticipantDTO.class);
        Participant participant = modelsMapper.toParticipant(participantDTO);
        BrainstormingTeam brainstormingTeam = getBrainstormingTeam(teamIdentifier);

        if (brainstormingTeam != null && brainstormingTeam.getCurrentNrOfParticipants() > 0 && brainstormingTeam.leaveTeam(participant)) {

            service.changeTeamMembers(brainstormingTeam, -1);
            return ok(Json.toJson(new SuccessMessage("Success", "Participant successfully removed from the brainstormingTeam")));

        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "There are no more participants in the brainstormingTeam or the participant has already left the brainstormingTeam or this team does not exist")));
        }
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
    @BodyParser.Of(BrainstormingTeamDTOBodyParser.class)
    public Result deleteBrainstormingTeam() throws ExecutionException, InterruptedException {
        BrainstormingTeamDTO brainstormingTeamDTO = request().body().as(BrainstormingTeamDTO.class);
        BrainstormingTeam brainstormingTeam = modelsMapper.toBrainstormingTeam(brainstormingTeamDTO);

        CompletableFuture<Long> future = service.deleteTeam(brainstormingTeam);

        if (future.get() > 0){
            return ok(Json.toJson(new SuccessMessage("Success", "Team successfully deleted")));
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No Team deleted! Does the identifier exist and is moderator's username and password correct?")));
        }
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
        Queue<BrainstormingTeamDTO> list = new LinkedList<>();
        for (BrainstormingTeam brainstormingTeam : future.get()) {
            BrainstormingTeamDTO brainstormingTeamDTO = modelsMapper.tobrainstormingTeamDTO(brainstormingTeam);
            list.add(brainstormingTeamDTO);
        }
        return ok(Json.toJson(list));
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
            BrainstormingTeamDTO brainstormingTeamDTO = modelsMapper.tobrainstormingTeamDTO(team);
            return ok(Json.toJson(brainstormingTeamDTO));
        } else {
            return internalServerError(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam found")));
        }
    }

    BrainstormingTeam getBrainstormingTeam(String teamIdentifier) throws ExecutionException, InterruptedException {
        CompletableFuture<BrainstormingTeam> future = service.getTeam(teamIdentifier);
        return future.get();
    }

}