package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import play.mvc.*;

import views.html.*;

@Api(value = "/home", description = "All operations with home", produces = "application/json")
public class HomeController extends Controller {

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

}
