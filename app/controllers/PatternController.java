package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mappers.ModelsMapper;
import models.ErrorMessage;
import models.SuccessMessage;
import models.bo.PatternIdea;
import models.dto.PatternIdeaDTO;
import parsers.PatternIdeaDTOBodyParser;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.business.PatternService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Api(value = "/PatternIdea", description = "All operations with the different PatternIdeas", produces = "application/json")
public class PatternController extends Controller {

    @Inject
    private PatternService service;
    @Inject
    private ModelsMapper modelsMapper;

    @ApiOperation(
            nickname = "getPatternIdeas",
            value = "Get all patternIdeas",
            notes = "With this method you can get all patternIdeas",
            httpMethod = "GET",
            response = PatternIdea.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PatternIdea.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getAllPatternIdeas(){
        CompletableFuture<Queue<PatternIdea>> future = service.getAllPatternIdeas();
        ArrayList<JsonNode> list = new ArrayList<>();

        try {

            for (PatternIdea patternIdea : future.get()) {
                PatternIdeaDTO patternIdeaDTO = modelsMapper.toPatternIdeaDTO(patternIdea);
                JsonNode node = Json.toJson(patternIdeaDTO);
                ((ObjectNode)node).put("type", "patternIdea");
                list.add(node);
            }

            return ok(Json.toJson(list));

        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }

    @ApiOperation(
            nickname = "createPattern",
            value = "Create a pattern",
            notes = "With this method you can create a pattern",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(PatternIdeaDTOBodyParser.class)
    public Result createPattern() {
        PatternIdeaDTO patternIdeaDTO = request().body().as(PatternIdeaDTO.class);
        PatternIdea patternIdea = modelsMapper.toPatternIdea(patternIdeaDTO);

        try {
            if (service.insertPattern(patternIdea)){
                return ok(Json.toJson(new SuccessMessage("Success", "Pattern successfully inserted")));
            } else {
                Logger.info("Pattern already exists");
                return badRequest(Json.toJson(new ErrorMessage("Error", "Pattern with this description already exists")));
            }
        } catch (ExecutionException | InterruptedException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }

    @ApiOperation(
            nickname = "deletePattern",
            value = "Delete a pattern",
            notes = "With this method you can delete a pattern",
            httpMethod = "DELETE",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(PatternIdeaDTOBodyParser.class)
    public Result deletePattern() {
        PatternIdeaDTO patternIdeaDTO = request().body().as(PatternIdeaDTO.class);
        PatternIdea patternIdea = modelsMapper.toPatternIdea(patternIdeaDTO);

        CompletableFuture<Long> future = service.deletePattern(patternIdea);

        try {
            if (future.get() > 0){
                return ok(Json.toJson(new SuccessMessage("Success", "Pattern successfully deleted")));
            } else {
                return badRequest(Json.toJson(new ErrorMessage("Error", "No Pattern deleted!")));
            }
        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }
}
