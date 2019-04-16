package controllers;

import io.swagger.annotations.Api;
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

    public Result getAllPatternIdeas(){

        CompletableFuture<Queue<PatternIdea>> future = service.getAllPatternIdeas();
        Queue<PatternIdeaDTO> list = new LinkedList<>();

        try {

            for (PatternIdea patternIdea : future.get()) {
                PatternIdeaDTO patternIdeaDTO = new PatternIdeaDTO(patternIdea.getDescription(), patternIdea.getProblem(), patternIdea.getSolution(), patternIdea.getUrl(), patternIdea.getCategory(), patternIdea.getPictureId());
                //PatternIdeaDTO patternIdeaDTO = modelsMapper.toPatternIdeaDTO(patternIdea);
                list.add(patternIdeaDTO);
            }

            return ok(Json.toJson(list));

        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }
}
