package services;

import org.junit.Before;
import org.junit.Test;
import services.business.FileService;
import services.mocks.MockDBFileService;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FileServiceTest {

    private FileService service;
    private byte[] testFileAsBytes;
    private String testFileAsString;

    @Before
    public void setUp() {
        this.service = new FileService(new MockDBFileService());
        testFileAsBytes = "TestFile".getBytes();
        testFileAsString = "TestFile";
    }

    @Test
    public void uploadTest() {
        try {
            String result = service.uploadFileAsStream(testFileAsBytes, testFileAsString);
            assertEquals("1", result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadTest() {
        try {
            byte[] restult = service.downloadFileAsStream(testFileAsString);
            assertArrayEquals(testFileAsBytes, restult);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
