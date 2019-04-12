package mapper;

import mappers.ModelsMapper;
import models.bo.*;
import models.dto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrainwaveDTOUnitTest {

    private ModelsMapper modelsMapper;

    @Before
    public void setUp(){
        modelsMapper = new ModelsMapper();
    }

    @Test
    public void noteIdeaDTOTest() {
        ArrayList<IdeaDTO> list = new ArrayList<>();
        list.add(new NoteIdeaDTO("Demo"));
        list.add(new NoteIdeaDTO("Demo2"));
        BrainwaveDTO brainwaveDTO = new BrainwaveDTO(3, list);

        Brainwave brainwave = modelsMapper.toBrainwave(brainwaveDTO);

        NoteIdeaDTO noteIdeaDTO = (NoteIdeaDTO) brainwaveDTO.getIdeas().get(0);
        NoteIdea noteIdea = (NoteIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwave.getIdeas().size());
        assertTrue(brainwave.getIdeas().get(0) instanceof NoteIdea);

        assertEquals(noteIdeaDTO.getDescription(), noteIdea.getDescription());
    }

    @Test
    public void sketchIdeaDTOTest() {
        ArrayList<IdeaDTO> list = new ArrayList<>();
        list.add(new SketchIdeaDTO("Demo", "01234"));
        list.add(new SketchIdeaDTO("Demo2", "56789"));
        BrainwaveDTO brainwaveDTO = new BrainwaveDTO(3, list);

        Brainwave brainwave = modelsMapper.toBrainwave(brainwaveDTO);

        SketchIdeaDTO sketchIdeaDTO = (SketchIdeaDTO) brainwaveDTO.getIdeas().get(0);
        SketchIdea sketchIdea = (SketchIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwave.getIdeas().size());
        assertTrue(brainwave.getIdeas().get(0) instanceof SketchIdea);

        assertEquals(sketchIdeaDTO.getDescription(), sketchIdea.getDescription());
        assertEquals(sketchIdeaDTO.getPictureId(), sketchIdea.getPictureId());
    }

    @Test
    public void patternIdeaDTOTest() {
        ArrayList<IdeaDTO> list = new ArrayList<>();
        list.add(new PatternIdeaDTO("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234"));
        list.add(new PatternIdeaDTO("Demo2", "DemoProblem2", "DemoSolution2", "www.microservice-api-patterns.org", "software", "56789"));
        BrainwaveDTO brainwaveDTO = new BrainwaveDTO(3, list);

        Brainwave brainwave = modelsMapper.toBrainwave(brainwaveDTO);

        PatternIdeaDTO patternIdeaDTO = (PatternIdeaDTO) brainwaveDTO.getIdeas().get(0);
        PatternIdea patternIdea = (PatternIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwave.getIdeas().size());
        assertTrue(brainwave.getIdeas().get(0) instanceof PatternIdea);

        assertEquals(patternIdeaDTO.getDescription(), patternIdea.getDescription());
        assertEquals(patternIdeaDTO.getProblem(), patternIdea.getProblem());
        assertEquals(patternIdeaDTO.getSolution(), patternIdea.getSolution());
        assertEquals(patternIdeaDTO.getPictureId(), patternIdea.getPictureId());

    }

    @Test
    public void allDTOTypesTest() {
        ArrayList<IdeaDTO> list = new ArrayList<>();
        list.add(new NoteIdeaDTO("Demo"));
        list.add(new SketchIdeaDTO("Demo", "01234"));
        list.add(new PatternIdeaDTO("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234"));
        BrainwaveDTO brainwaveDTO = new BrainwaveDTO(3, list);

        Brainwave brainwave = modelsMapper.toBrainwave(brainwaveDTO);

        assertEquals(3, brainwave.getIdeas().size());
        assertTrue(brainwave.getIdeas().get(0) instanceof NoteIdea);
        assertTrue(brainwave.getIdeas().get(1) instanceof SketchIdea);
        assertTrue(brainwave.getIdeas().get(2) instanceof PatternIdea);


    }
}
