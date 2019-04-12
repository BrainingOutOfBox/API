package mapper;

import mappers.ModelsMapper;
import models.dto.*;
import models.bo.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BrainstormingFindingDTOUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void dtoToBoTest(){
        BrainstormingFindingDTO brainstormingFindingDTO = createBrainstormingFindingDTO();
        BrainstormingFinding brainstormingFinding = modelsMapper.toBrainstormingFinding(brainstormingFindingDTO);

        NoteIdeaDTO noteIdeaDTO = (NoteIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(0);
        SketchIdeaDTO sketchIdeaDTO = (SketchIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(1);
        PatternIdeaDTO patternIdeaDTO = (PatternIdeaDTO) brainstormingFindingDTO.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(2);

        NoteIdea noteIdea = (NoteIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(0);
        SketchIdea sketchIdea = (SketchIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(1);
        PatternIdea patternIdea = (PatternIdea) brainstormingFinding.getBrainsheets().get(0).getBrainwaves().get(0).getIdeas().get(2);

        assertEquals(brainstormingFindingDTO.getName(), brainstormingFinding.getName());
        assertEquals(brainstormingFindingDTO.getProblemDescription(), brainstormingFinding.getProblemDescription());
        assertEquals(brainstormingFindingDTO.getNrOfIdeas(), brainstormingFinding.getNrOfIdeas());
        assertEquals(brainstormingFindingDTO.getBaseRoundTime(), brainstormingFinding.getBaseRoundTime());
        assertEquals(brainstormingFindingDTO.getIdentifier(), brainstormingFinding.getIdentifier());
        assertEquals(brainstormingFindingDTO.getType(), brainstormingFinding.getType());

        assertEquals(noteIdea.getDescription(), noteIdeaDTO.getDescription());

        assertEquals(sketchIdea.getDescription(), sketchIdeaDTO.getDescription());
        assertEquals(sketchIdea.getPictureId(), sketchIdeaDTO.getPictureId());

        assertEquals(patternIdea.getDescription(), patternIdeaDTO.getDescription());
        assertEquals(patternIdea.getProblem(), patternIdeaDTO.getProblem());
        assertEquals(patternIdea.getSolution(), patternIdeaDTO.getSolution());
        assertEquals(patternIdea.getPictureId(), patternIdeaDTO.getPictureId());
    }



    private BrainstormingFindingDTO createBrainstormingFindingDTO(){
        ArrayList<BrainsheetDTO> brainsheetDTOS = createBrainsheetsDTO();

        BrainstormingFindingDTO brainstormingFindingDTO = new BrainstormingFindingDTO("DemoTestDTO", "DemoDTO", 1, 2, 0, "", "software", brainsheetDTOS, 0, "");
        return  brainstormingFindingDTO;
    }

    private ArrayList<BrainsheetDTO> createBrainsheetsDTO(){
        ArrayList<BrainsheetDTO> brainsheetDTOS = new ArrayList<>();
        BrainsheetDTO brainsheetDTO = new BrainsheetDTO();
        brainsheetDTO.setBrainwaves(createBrainwavesDTO());
        brainsheetDTOS.add(brainsheetDTO);
        return brainsheetDTOS;
    }

    private ArrayList<BrainwaveDTO> createBrainwavesDTO(){
        ArrayList<BrainwaveDTO> brainwaveDTOS = new ArrayList<>();
        BrainwaveDTO brainwaveDTO = new BrainwaveDTO();
        brainwaveDTO.setIdeas(createAllTypeOfIdeasDTO());
        brainwaveDTOS.add(brainwaveDTO);

        return brainwaveDTOS;
    }


    private ArrayList<IdeaDTO> createAllTypeOfIdeasDTO(){
        ArrayList<IdeaDTO> ideas = new ArrayList<>();
        NoteIdeaDTO noteIdeaDTO = new NoteIdeaDTO("Demo");
        SketchIdeaDTO sketchIdeaDTO = new SketchIdeaDTO("Demo", "01234");
        PatternIdeaDTO patternIdeaDTO = new PatternIdeaDTO("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234");

        ideas.add(noteIdeaDTO);
        ideas.add(sketchIdeaDTO);
        ideas.add(patternIdeaDTO);

        return ideas;
    }

}
