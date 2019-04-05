package parsers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import models.ErrorMessage;
import models.dto.ParticipantDTO;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class ParticipantDTOBodyParser implements BodyParser<ParticipantDTO> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public ParticipantDTOBodyParser(BodyParser.Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, ParticipantDTO>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);

        return jsonAccumulator.map(resultOrJson -> {
            if (resultOrJson.left.isPresent()) {
                return F.Either.Left(resultOrJson.left.get());
            } else {
                JsonNode json = resultOrJson.right.get();
                try {
                    ParticipantDTO participantDTO = play.libs.Json.fromJson(json, ParticipantDTO.class);
                    F.Either<Result, ParticipantDTO> check;

                    switch (request.uri()){
                        case "/Participant/login":
                            check = checkLoginParticipantDTO(participantDTO);
                            break;
                        default:
                            check = checkEntireParticipantDTO(participantDTO);
                    }

                    return check.left.<F.Either<Result, ParticipantDTO>>map(F.Either::Left).orElseGet(() -> F.Either.Right(participantDTO));

                } catch (Exception e) {
                    return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","Unable to read Participant from json: " + e.getMessage()))));
                }
            }
        }, executor);

    }

    private F.Either<Result, ParticipantDTO> checkEntireParticipantDTO(ParticipantDTO participantDTO){

        if (participantDTO.getUsername() == null || participantDTO.getUsername().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No username present"))));
        }

        if (participantDTO.getPassword() == null || participantDTO.getPassword().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No password present"))));
        }

        if (participantDTO.getFirstname() == null || participantDTO.getFirstname().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No firstname present"))));
        }

        if (participantDTO.getLastname() == null || participantDTO.getLastname().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No lastname present"))));
        }


        return F.Either.Right(participantDTO);
    }

    private F.Either<Result, ParticipantDTO> checkLoginParticipantDTO(ParticipantDTO participantDTO){

        if (participantDTO.getUsername() == null || participantDTO.getUsername().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No username present"))));
        }

        if (participantDTO.getPassword() == null || participantDTO.getPassword().length() == 0){
            return F.Either.Left(Results.badRequest(play.libs.Json.toJson(new ErrorMessage("Error","No password present"))));
        }

        return F.Either.Right(participantDTO);
    }
}
