package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import models.ErrorMessage;
import models.SuccessMessage;
import org.joda.time.DateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Api(value = "/time", description = "All operations with time", produces = "application/json")
public class TimerController extends Controller {

    final long ONE_MINUTE_IN_MILLIS = 60000;
    private DateTime finishDate;
    private DateTime nowDate;

    @ApiOperation(
            nickname = "createTimer",
            value = "Create a timer",
            notes = "With this method you can create a timer",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result createTimer() {
        finishDate = new DateTime().plusMinutes(5);
        return ok(Json.toJson(new SuccessMessage("Success", finishDate.toString())));
    }

    @ApiOperation(
            nickname = "calculateTimeDifference",
            value = "Calculate difference in time",
            notes = "With this method you can calculate the time difference form now to init date",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result calculateDateDifference(){
        long difference = 0;
        if (finishDate != null) {
            nowDate = new DateTime();
            difference = getDateDiff(finishDate, nowDate, TimeUnit.MILLISECONDS);
        }
        return ok(Json.toJson(difference));
    }


    private static long getDateDiff(DateTime date1, DateTime date2, TimeUnit timeUnit) {
        long diffInMillisecondes = date1.getMillis()- date2.getMillis();
        return timeUnit.convert(diffInMillisecondes,TimeUnit.MILLISECONDS);
    }



}
