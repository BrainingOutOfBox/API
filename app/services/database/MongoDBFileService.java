package services.database;

import com.mongodb.async.client.gridfs.GridFSBucket;
import com.mongodb.async.client.gridfs.GridFSBuckets;
import com.mongodb.async.client.gridfs.GridFSDownloadStream;
import com.mongodb.async.client.gridfs.GridFSUploadStream;
import config.MongoDBEngineProvider;
import org.bson.types.ObjectId;
import play.Logger;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MongoDBFileService implements DBFileInterface {

    private GridFSBucket gridFSBucket;

    @Inject
    public MongoDBFileService(MongoDBEngineProvider mongoDBEngineProvider) {
        this.gridFSBucket = GridFSBuckets.create(mongoDBEngineProvider.getDatabase());
    }

    @Override
    public String uploadFileAsStream(byte[] stream, String fileName) throws ExecutionException, InterruptedException {
        ByteBuffer data = ByteBuffer.wrap(stream);
        CompletableFuture<String> future = new CompletableFuture<>();

        final GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(fileName);
        uploadStream.write(data, (result, t) -> {
            Logger.info("File successfully inserted; ID: " + uploadStream.getObjectId().toHexString());
            future.complete(uploadStream.getObjectId().toHexString());

            uploadStream.close((result1, t1) -> {
                // stream close
            });
        });

        return future.get();
    }

    @Override
    public byte[] downloadFileAsStream(String id) throws ExecutionException, InterruptedException, IllegalArgumentException {
        ObjectId fileId = new ObjectId(id);
        final ByteBuffer dstByteBuffer = ByteBuffer.allocate(1024 * 1024);
        final GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId);
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        downloadStream.read(dstByteBuffer, (result, t) -> {
            dstByteBuffer.flip();
            byte[] bytes = new byte[result];
            dstByteBuffer.get(bytes);
            Logger.info("Found file to download; Size: " + result);
            future.complete(bytes);

            downloadStream.close((result1, t1) -> {
                // stream closed
            });
        });

        return future.get();
    }
}
