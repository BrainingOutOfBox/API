package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mappers.ModelsMapper;
import models.ErrorMessage;
import models.bo.PatternIdea;
import models.dto.PatternIdeaDTO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.business.PatternService;

import javax.inject.Inject;
import java.util.LinkedList;
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
        Queue<PatternIdeaDTO> list = new LinkedList<>();

        try {

            for (PatternIdea patternIdea : future.get()) {
                PatternIdeaDTO patternIdeaDTO = modelsMapper.toPatternIdeaDTO(patternIdea);
                list.add(patternIdeaDTO);
            }

            return ok(Json.toJson(list));

        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }
}
