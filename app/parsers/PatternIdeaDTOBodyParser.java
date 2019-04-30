package parsers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import models.ErrorMessage;
import models.dto.ParticipantDTO;
import models.dto.PatternIdeaDTO;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class PatternIdeaDTOBodyParser implements BodyParser<PatternIdeaDTO> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public PatternIdeaDTOBodyParser(BodyParser.Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, PatternIdeaDTO>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);

        return jsonAccumulator.map(resultOrJson -> {
            if (resultOrJson.left.isPresent()) {
                return F.Either.Left(resultOrJson.left.get());
            } else {
                JsonNode json = resultOrJson.right.get();
                try {
                    PatternIdeaDTO patternIdeaDTO = play.libs.Json.fromJson(json, PatternIdeaDTO.class);
                    F.Either<Result, PatternIdeaDTO> check = checkEntirePatternDTO(patternIdeaDTO);

                    return check.left.<F.Either<Result, PatternIdeaDTO>>map(F.Either::Left).orElseGet(() -> F.Either.Right(patternIdeaDTO));

                } catch (Exception e) {
                    return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Unable to read Pattern from json: " + e.getMessage()))));
                }
            }
        }, executor);
    }

    private F.Either<Result, PatternIdeaDTO> checkEntirePatternDTO(PatternIdeaDTO patternIdeaDTO){

        if (patternIdeaDTO.getDescription() == null || patternIdeaDTO.getDescription().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No description present"))));
        }

        if (patternIdeaDTO.getProblem() == null || patternIdeaDTO.getProblem().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No problem present"))));
        }

        if (patternIdeaDTO.getSolution() == null || patternIdeaDTO.getSolution().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No solution present"))));
        }

        if (patternIdeaDTO.getUrl() == null || patternIdeaDTO.getUrl().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No url present"))));
        }

        if (patternIdeaDTO.getCategory() == null || patternIdeaDTO.getCategory().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No category present"))));
        }

        if (patternIdeaDTO.getPictureId() == null || patternIdeaDTO.getPictureId().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No pictureId present"))));
        }


        return F.Either.Right(patternIdeaDTO);
    }


}
