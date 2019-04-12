package services.business;

import services.database.DBFileInterface;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class FileService {

    private DBFileInterface service;

    @Inject
    public FileService(DBFileInterface service) {
        this.service = service;
    }

    public String uploadFileAsStream(byte[] stream, String fileName) throws ExecutionException, InterruptedException {
        return service.uploadFileAsStream(stream,fileName);
    }

    public byte[] downloadFileAsStream(String id) throws ExecutionException, InterruptedException {
        return service.downloadFileAsStream(id);
    }
}
