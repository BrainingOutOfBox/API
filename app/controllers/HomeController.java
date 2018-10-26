package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

@Api(value = "/Home", description = "All operations with home", produces = "application/json")
public class HomeController extends Controller {

    @ApiOperation(
            nickname = "getHome",
            value = "Get home view",
            notes = "With this method you can get the index view",
            httpMethod = "GET",
            response = TeamController.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = TeamController.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = TeamController.class) })
    public Result index() {
        return ok(index.render("Your new application is ready."));

    }
}
