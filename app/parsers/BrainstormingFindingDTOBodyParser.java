package parsers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import models.ErrorMessage;
import models.dto.BrainstormingFindingDTO;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class BrainstormingFindingDTOBodyParser implements BodyParser<BrainstormingFindingDTO> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public BrainstormingFindingDTOBodyParser(BodyParser.Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, BrainstormingFindingDTO>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);

        return jsonAccumulator.map(resultOrJson -> {
            if (resultOrJson.left.isPresent()) {
                return F.Either.Left(resultOrJson.left.get());
            } else {
                JsonNode json = resultOrJson.right.get();
                try {
                    BrainstormingFindingDTO brainstormingFindingDTO = play.libs.Json.fromJson(json, BrainstormingFindingDTO.class);
                    F.Either<Result, BrainstormingFindingDTO> check = checkBrainstormingDTO(brainstormingFindingDTO);
                    return check.left.<F.Either<Result, BrainstormingFindingDTO>>map(F.Either::Left).orElseGet(() -> F.Either.Right(brainstormingFindingDTO));

                } catch (Exception e) {
                    return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Unable to read BrainstormingFinding from json: " + e.getMessage()))));
                }
            }
        }, executor);

    }

    private F.Either<Result, BrainstormingFindingDTO> checkBrainstormingDTO(BrainstormingFindingDTO brainstormingFindingDTO){

        if (brainstormingFindingDTO.getName().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No name present"))));
        }

        if (brainstormingFindingDTO.getType().equals("")){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No type present"))));
        }

        if (brainstormingFindingDTO.getNrOfIdeas() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No nrOfIdeas present"))));
        }

        if (brainstormingFindingDTO.getBaseRoundTime() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No baseRoundTime present"))));
        }

        return F.Either.Right(brainstormingFindingDTO);
    }
}
