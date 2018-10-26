package controllers;

import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import io.swagger.annotations.*;
import models.BrainstormingFinding;
import models.ErrorMessage;
import models.SuccessMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@Api(value = "/Timing", description = "All operations with time", produces = "application/json")
public class TimerController extends Controller {

    private MongoClient mongoClient;
    private MongoDatabase database;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<BrainstormingFinding> collection;

    public TimerController(){
        pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(new ConnectionString("mongodb://play:Methode746@localhost:40002/?authSource=admin&authMechanism=SCRAM-SHA-1"));

        database = mongoClient.getDatabase("Test");
        database = database.withCodecRegistry(pojoCodecRegistry);

        collection = database.getCollection("BrainstormingFinding", BrainstormingFinding.class);

    }

    @ApiOperation(
            nickname = "calculateTimeDifference",
            value = "Calculate difference in time",
            notes = "With this method you can calculate the time difference of the current round in the selected BrainstormFinding",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result calculateDateDifferenceOfFindingFromTeam(@ApiParam(value = "BrainstormingTeam Identifier", name = "teamIdentifier", required = true) String teamIdentifier, @ApiParam(value = "BrainstormingFinding Identifier", name = "findingIdentifier", required = true) String findingIdentifier) throws ExecutionException, InterruptedException {

        CompletableFuture<BrainstormingFinding> future = new CompletableFuture<>();
        DateTime currentRoundEndTime;
        DateTime nowDate;

        collection.find(and(eq("brainstormingTeam", teamIdentifier),eq("identifier", findingIdentifier))).first(new SingleResultCallback<BrainstormingFinding>() {
            @Override
            public void onResult(final BrainstormingFinding brainstormFinding, final Throwable t) {
                Logger.info(brainstormFinding.getCurrentRoundEndTime());
                future.complete(brainstormFinding);
            }
        });

        long difference = 0;
        currentRoundEndTime = DateTime.parse(future.get().getCurrentRoundEndTime());
        if (currentRoundEndTime != null) {
            nowDate = new DateTime();
            difference = getDateDiff(currentRoundEndTime, nowDate, TimeUnit.MILLISECONDS);
        }
        return ok(Json.toJson(difference));

    }


    private static long getDateDiff(DateTime date1, DateTime date2, TimeUnit timeUnit) {
        long diffInMillisecondes = date1.getMillis()- date2.getMillis();
        return timeUnit.convert(diffInMillisecondes,TimeUnit.MILLISECONDS);
    }



}
