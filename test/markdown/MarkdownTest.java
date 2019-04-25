package markdown;

import mappers.ModelsMapper;
import models.bo.BrainstormingFinding;
import org.junit.Before;
import org.junit.Test;
import services.business.FindingService;
import services.mocks.MockDBFindingService;
import services.mocks.MockDBTeamService;

import java.util.concurrent.ExecutionException;

public class MarkdownTest {

    private FindingService service;

    @Before
    public void setUp() {
        this.service = new FindingService(new MockDBFindingService(), new MockDBTeamService(), new ModelsMapper());
    }

    @Test
    public void markdownTest() {
        try {
            BrainstormingFinding result = service.getFinding("2222").get();
            StringBuilder sb = new StringBuilder().append(result);

            System.out.println(sb);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
