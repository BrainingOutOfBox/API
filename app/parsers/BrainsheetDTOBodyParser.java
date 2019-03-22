package parsers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import models.ErrorMessage;
import models.dto.BrainsheetDTO;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class BrainsheetDTOBodyParser implements BodyParser<BrainsheetDTO> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public BrainsheetDTOBodyParser(Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, BrainsheetDTO>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);

        return jsonAccumulator.map(resultOrJson -> {
            if (resultOrJson.left.isPresent()) {
                return F.Either.Left(resultOrJson.left.get());
            } else {
                JsonNode json = resultOrJson.right.get();
                try {
                    BrainsheetDTO brainsheetDTO = play.libs.Json.fromJson(json, BrainsheetDTO.class);
                    F.Either<Result, BrainsheetDTO> check = checkBrainsheetDTO(brainsheetDTO);
                    return check.left.<F.Either<Result, BrainsheetDTO>>map(F.Either::Left).orElseGet(() -> F.Either.Right(brainsheetDTO));

                } catch (Exception e) {
                    return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Unable to read BrainstormingFinding from json: " + e.getMessage()))));
                }
            }
        }, executor);

    }

    private F.Either<Result, BrainsheetDTO> checkBrainsheetDTO(BrainsheetDTO brainsheetDTO){

        if (brainsheetDTO.getNrOfSheet() < 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No nrOfSheet present"))));
        }

        if (brainsheetDTO.getBrainwaves().size() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No brainwaves present"))));
        }

        return F.Either.Right(brainsheetDTO);
    }
}
