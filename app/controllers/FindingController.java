package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import io.swagger.annotations.*;
import models.ErrorMessage;
import models.SuccessMessage;
import org.bson.Document;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Api(value = "/BrainstormingFinding", description = "All operations with brainstormingFindings", produces = "application/json")
public class FindingController extends Controller {

    MongoClient mongoClient;
    MongoDatabase database;

    @ApiOperation(
            nickname = "createBrainstormingFinding",
            value = "Create a brainstormingFinding",
            notes = "With this method you can create a brainstormingFinding",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result createBrainstormingFindingForTeam(@ApiParam(value = "BrainstormingTeam Name", name = "teamName", required = true) String teamName){

        JsonNode body = request().body().asJson();

        if (body == null) {
            Logger.error("json body is null");
            return forbidden(Json.toJson(new ErrorMessage("Error", "json body is null")));
        }



        return null;
    }


    @ApiOperation(
            nickname = "getBrainstormingFinding",
            value = "Get all brainstormingFinding",
            notes = "With this method you can get all brainstormingFinding from a specific team",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result getBrainstormingFindingFromTeam(@ApiParam(value = "BrainstormingTeam Name", name = "teamName", required = true) String teamName) throws ExecutionException, InterruptedException {

        mongoClient = MongoClients.create(new ConnectionString("mongodb://api:Methode746@localhost:40002/?authSource=admin"));
        database = mongoClient.getDatabase("Test");
        MongoCollection<Document> collection = database.getCollection("BrainstormingFinding");

        CompletableFuture<String> res = new CompletableFuture<>();
        final String[] test = {new String()};
/*
        collection.find(eq("brainstormingTeam", "DemoTeam")).first(
        new SingleResultCallback<Document>() {
            @Override
            public void onResult(final Document document, final Throwable t) {
                System.out.println(document.toJson());
                res.complete(document.toJson());
            }
        });


        return ok(result.get());
*/

        collection.find(eq("brainstormingTeam", teamName)).forEach(
                new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        System.out.println(document.toJson());
                        test[0] += document.toJson();
                    }
                }, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(final Void result, final Throwable t) {
                        System.out.println("Operation Finished!");
                        res.complete(test[0]);
                    }
                });
        return ok(res.get());
    }

}
