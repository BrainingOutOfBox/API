package controllers;

import io.swagger.annotations.*;
import mappers.ModelsMapper;
import models.*;
import models.bo.*;
import models.dto.BrainsheetDTO;
import models.dto.BrainstormingFindingDTO;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import parsers.BrainsheetDTOBodyParser;
import parsers.BrainstormingFindingDTOBodyParser;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.business.FindingService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;


@Api(value = "/BrainstormingFinding", description = "All operations with brainstormingFindings", produces = "application/json")
public class FindingController extends Controller {

    @Inject
    private FindingService service;
    @Inject
    private ModelsMapper modelsMapper;

    @ApiOperation(
            nickname = "createBrainstormingFinding",
            value = "Create a brainstormingFinding",
            notes = "With this method you can create a brainstormingFinding",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(BrainstormingFindingDTOBodyParser.class)
    public Result createBrainstormingFindingForTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier){
        BrainstormingFindingDTO brainstormingFindingDTO = request().body().as(BrainstormingFindingDTO.class);

        try {

            String identifier = service.insertFinding(brainstormingFindingDTO, teamIdentifier);

            if (identifier != null) {
                return ok(Json.toJson(new SuccessMessage("Success", identifier)));
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No brainstormingTeam with this identifier found")));
            }

        } catch (ExecutionException | InterruptedException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
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
    public Result getBrainstormingFindingFromTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier){
        BrainstormingTeam team = new BrainstormingTeam();
        team.setIdentifier(teamIdentifier);

        CompletableFuture<Queue<BrainstormingFinding>> future = service.getAllFindingsOfTeam(team);
        Queue<BrainstormingFindingDTO> list = new LinkedList<>();

        try {

            for (BrainstormingFinding brainstormingFinding : future.get()) {
                BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(brainstormingFinding);
                list.add(brainstormingFindingDTO);
            }

            return ok(Json.toJson(list));

        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }

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
    public Result getBrainstormingFindingByIdentifier(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier){
        try {

            BrainstormingFinding finding = service.getFinding(findingIdentifier).get();

            if (finding != null) {
                BrainstormingFindingDTO brainstormingFindingDTO = modelsMapper.toBrainstormingFindingDTO(finding);
                return ok(Json.toJson(brainstormingFindingDTO));
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
            }

        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
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
    @BodyParser.Of(BrainsheetDTOBodyParser.class)
    public Result putBrainsheet(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier){
        BrainsheetDTO brainsheetDTO = request().body().as(BrainsheetDTO.class);
        Brainsheet newBrainsheet = modelsMapper.toBrainsheet(brainsheetDTO);

        try {

            if (service.exchangeBrainsheet(findingIdentifier, newBrainsheet)) {
                return ok(Json.toJson(new SuccessMessage("Success", "Brainsheet successfully updated")));
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No Brainsheet updated")));
            }

        } catch (ExecutionException | InterruptedException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }

    @ApiOperation(
            nickname = "calculateRemainingTime",
            value = "Calculate remaining time",
            notes = "With this method you can calculate the remaining time of the current round in the selected BrainstormFinding",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result calculateRemainingTimeOfFinding(@ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier){
        try {

            long difference = service.calculateRemainingTimeOfFinding(findingIdentifier);
            return ok(Json.toJson(difference));

        } catch (ExecutionException | InterruptedException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
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
    public Result startBrainstorming(String findingIdentifier){
        try {
            if(service.startBrainstorming(findingIdentifier)) {
                return ok(Json.toJson(new SuccessMessage("Success", "BrainstormingFinding successfully started")));
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found or brainstormingFinding has already started or ended")));
            }
        } catch (ExecutionException | InterruptedException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }

    @ApiOperation(
            nickname = "exportBrainstormingFinding",
            value = "export a brainstormingFinding",
            notes = "With this method you can export the brainstormingFinding as markdown",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BrainstormingFinding.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result exportBrainstorming(String findingIdentifier){
        try {

            BrainstormingFinding finding = service.getFinding(findingIdentifier).get();

            if (finding != null) {
                StringBuilder sb = new StringBuilder().append(finding.serialize());
                return ok(sb.toString());
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No brainstormingFinding found")));
            }

        } catch (InterruptedException | ExecutionException | MarkdownSerializationException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }
}
