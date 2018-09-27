package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import play.mvc.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */

@Api(value = "/home", description = "All operations with home", produces = "application/json")
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    @ApiOperation(
            nickname = "getAllEmployee",
            value = "Get all employee engaged in the hospital",
            notes = "With this method you can get all employee and all it's informations",
            httpMethod = "GET",
            response = HomeController.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = HomeController.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = HomeController.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = HomeController.class) })
    public Result index() {

        return ok(index.render("Your new application is ready."));

    }

}
