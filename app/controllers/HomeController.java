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
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Api(value = "/home", description = "All operations with home", produces = "application/json")
public class HomeController extends Controller {

    @Inject
    private Config config;

    @ApiOperation(
            nickname = "getHome",
            value = "Get home view",
            notes = "With this method you can get the index view",
            httpMethod = "GET",
            response = HomeController.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = HomeController.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = HomeController.class) })
    public Result index() {
        return ok(index.render("Your new application is ready."));

    }


    public Result login() throws UnsupportedEncodingException {
        JsonNode body = request().body().asJson();

        if (body == null) {
            Logger.error("json body is null");
            return forbidden();
        }

        if (body.hasNonNull("username") && body.hasNonNull("password") && body.get("username").asText().equals("abc")) {
            ObjectNode result = Json.newObject();
            result.put("access_token", getSignedToken(7l));
            return ok(result);
        } else {
            Logger.error("json body not as expected: {}", body.toString());
        }

        return forbidden();
    }

    private String getSignedToken(Long userId) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("ThePlayApp")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(10).toInstant()))
                .sign(algorithm);
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok("access granted via filter");
        }).orElse(forbidden("eh, no verified jwt found, " + oVerifiedJwt.toString()));
    }



}
