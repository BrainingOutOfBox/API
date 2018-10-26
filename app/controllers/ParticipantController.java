package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import models.ErrorMessage;
import models.SuccessMessage;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Api(value = "/Participant", description = "All operations with participant", produces = "application/json")
public class ParticipantController extends Controller {

    @Inject
    private Config config;

    @ApiOperation(
            nickname = "login",
            value = "Login",
            notes = "With this method you can login an get a jwt Token",
            httpMethod = "GET",
            response = Json.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Json.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result login() throws UnsupportedEncodingException {
        JsonNode body = request().body().asJson();

        if (body == null) {
            Logger.error("json body is null");
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        }

        if (body.hasNonNull("username") && body.hasNonNull("password")) {
            ObjectNode result = Json.newObject();
            result.put("access_token", getSignedToken(7l));
            return ok(result);
        } else {
            Logger.error("json body not as expected: {}", body.toString());
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body not as expected")));
        }

    }

    private String getSignedToken(Long userId) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("ThePlayApp")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(120).toInstant()))
                .sign(algorithm);
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok(Json.toJson(new SuccessMessage("Success", "access granted via filter")));
        }).orElse(forbidden(Json.toJson(new ErrorMessage("Error","eh, no verified jwt found"))));
    }
}
