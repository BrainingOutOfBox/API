package services.mocks;

import services.database.DBFileInterface;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MockDBFileService implements DBFileInterface {
    @Override
    public String uploadFileAsStream(byte[] stream, String filename) {
        byte[] bytes = filename.getBytes();
        if (Arrays.equals(bytes, stream)){
            return "1";
        }

        return "";
    }

    @Override
    public byte[] downloadFileAsStream(String id) {
        return id.getBytes();
    }
}
