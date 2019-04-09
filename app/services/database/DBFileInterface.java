package services.database;

import com.google.inject.ImplementedBy;

import java.util.concurrent.ExecutionException;

@ImplementedBy(MongoDBFileService.class)
public interface DBFileInterface {

    String uploadFileAsStream(byte[] stream, String filename) throws ExecutionException, InterruptedException;

    byte[] downloadFileAsStream(String id) throws ExecutionException, InterruptedException;

}
