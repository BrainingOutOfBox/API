package controllers;

import io.swagger.annotations.*;
import models.ErrorMessage;
import models.SuccessMessage;
import parsers.MultipartFormDataBodyParser;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.business.FileService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

@Api(value = "/Files", description = "To upload and download Files", produces = "application/json")
public class FileController extends Controller {

    @Inject
    private FileService service;

    @ApiOperation(
            nickname = "upload",
            value = "Upload a file",
            notes = "With this method you can upload a file",
            httpMethod = "POST",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    @BodyParser.Of(MultipartFormDataBodyParser.class)
    public Result uploadFile(){
        try {

            final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
            final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("name");
            final File file = filePart.getFile();

            final byte[] fileData = Files.readAllBytes(file.toPath());
            final String fileName = file.getName();

            String fileId = service.uploadFileAsStream(fileData, fileName);
            return ok(Json.toJson(new SuccessMessage("Success", fileId)));

        } catch (IOException | InterruptedException | ExecutionException  e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }

    @ApiOperation(
            nickname = "download",
            value = "Download a file",
            notes = "With this method you can download a file",
            httpMethod = "GET",
            response = SuccessMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = SuccessMessage.class),
            @ApiResponse(code = 500, message = "Internal Server ErrorMessage", response = ErrorMessage.class) })
    public Result downloadFile(@ApiParam(value = "File Identifier", name = "fileIdentifier", required = true) String fileIdentifier ){
        try {

            byte[] result = service.downloadFileAsStream(fileIdentifier);
            return ok(result);

        } catch (ExecutionException | InterruptedException | IllegalArgumentException e) {
            return internalServerError(Json.toJson(new ErrorMessage("Error", e.getMessage())));
        }
    }
}
