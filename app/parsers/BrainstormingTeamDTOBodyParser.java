package parsers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import models.ErrorMessage;
import models.dto.BrainstormingTeamDTO;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class BrainstormingTeamDTOBodyParser implements BodyParser<BrainstormingTeamDTO> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public BrainstormingTeamDTOBodyParser(BodyParser.Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, BrainstormingTeamDTO>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);

        return jsonAccumulator.map(resultOrJson -> {
            if (resultOrJson.left.isPresent()) {
                return F.Either.Left(resultOrJson.left.get());
            } else {
                JsonNode json = resultOrJson.right.get();
                try {
                    BrainstormingTeamDTO brainstormingTeamDTO = play.libs.Json.fromJson(json, BrainstormingTeamDTO.class);
                    F.Either<Result, BrainstormingTeamDTO> check;

                    switch (request.uri()){
                        case "/Team/deleteBrainstormingTeam":
                            check = checkDeleteBrainstormingTeamDTO(brainstormingTeamDTO);
                            break;
                        default:
                            check = checkEntireBrainstormingTeamDTO(brainstormingTeamDTO);
                    }

                    return check.left.<F.Either<Result, BrainstormingTeamDTO>>map(F.Either::Left).orElseGet(() -> F.Either.Right(brainstormingTeamDTO));

                } catch (Exception e) {
                    return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Unable to read BrainstormingTeam from json: " + e.getMessage()))));
                }
            }
        }, executor);

    }

    private F.Either<Result, BrainstormingTeamDTO> checkEntireBrainstormingTeamDTO(BrainstormingTeamDTO brainstormingTeamDTO){

        if (brainstormingTeamDTO.getName() == null || brainstormingTeamDTO.getName().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No name present"))));
        }

        if (brainstormingTeamDTO.getPurpose() == null || brainstormingTeamDTO.getPurpose().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No purpose present"))));
        }

        if (brainstormingTeamDTO.getNrOfParticipants() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No nrOfParticipants present"))));
        }

        if (brainstormingTeamDTO.getModerator() == null){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No moderator present"))));
        }

        if (brainstormingTeamDTO.getModerator().getUsername() == null || brainstormingTeamDTO.getModerator().getUsername().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's name not present"))));
        }

        if (brainstormingTeamDTO.getModerator().getPassword() == null || brainstormingTeamDTO.getModerator().getPassword().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's password not present"))));
        }

        if (brainstormingTeamDTO.getModerator().getLastname() == null || brainstormingTeamDTO.getModerator().getLastname().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's lastname not present"))));
        }

        if (brainstormingTeamDTO.getModerator().getFirstname() == null || brainstormingTeamDTO.getModerator().getFirstname().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's firstname not present"))));
        }


        return F.Either.Right(brainstormingTeamDTO);
    }

    private F.Either<Result, BrainstormingTeamDTO> checkDeleteBrainstormingTeamDTO(BrainstormingTeamDTO brainstormingTeamDTO){

        if (brainstormingTeamDTO.getIdentifier() == null || brainstormingTeamDTO.getIdentifier().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No identifier present"))));
        }

        if (brainstormingTeamDTO.getModerator() == null){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No moderator present"))));
        }

        if (brainstormingTeamDTO.getModerator().getUsername() == null || brainstormingTeamDTO.getModerator().getUsername().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's name not present"))));
        }

        if (brainstormingTeamDTO.getModerator().getPassword() == null || brainstormingTeamDTO.getModerator().getPassword().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Moderator's password not present"))));
        }


        return F.Either.Right(brainstormingTeamDTO);
    }
}
