package mapper;


import mappers.ModelsMapper;
import models.bo.*;
import models.dto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrainwaveBOUnitTest {

    private ModelsMapper modelsMapper;


    @Before
    public void setUp() {
        modelsMapper = new ModelsMapper();
    }


    @Test
    public void noteIdeaTest() {
        ArrayList<Idea> list = new ArrayList<>();
        list.add(new NoteIdea("Demo"));
        list.add(new NoteIdea("Demo2"));
        Brainwave brainwave = new Brainwave(3, list);

        BrainwaveDTO brainwaveDTO = modelsMapper.toBrainwaveDTO(brainwave);


        NoteIdeaDTO noteIdeaDTO = (NoteIdeaDTO) brainwaveDTO.getIdeas().get(0);
        NoteIdea noteIdea = (NoteIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwaveDTO.getIdeas().size());
        assertTrue(brainwaveDTO.getIdeas().get(0) instanceof NoteIdeaDTO);

        assertEquals(noteIdea.getDescription(), noteIdeaDTO.getDescription());
    }

    @Test
    public void sketchIdeaTest() {
        ArrayList<Idea> list = new ArrayList<>();
        list.add(new SketchIdea("Demo", "01234"));
        list.add(new SketchIdea("Demo2", "56789"));
        Brainwave brainwave = new Brainwave(3, list);

        BrainwaveDTO brainwaveDTO = modelsMapper.toBrainwaveDTO(brainwave);


        SketchIdeaDTO sketchIdeaDTO = (SketchIdeaDTO) brainwaveDTO.getIdeas().get(0);
        SketchIdea sketchIdea = (SketchIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwaveDTO.getIdeas().size());
        assertTrue(brainwaveDTO.getIdeas().get(0) instanceof SketchIdeaDTO);

        assertEquals(sketchIdea.getDescription(), sketchIdeaDTO.getDescription());
        assertEquals(sketchIdea.getPictureId(), sketchIdeaDTO.getPictureId());
    }

    @Test
    public void patternIdeaTest() {
        ArrayList<Idea> list = new ArrayList<>();
        list.add(new PatternIdea("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234"));
        list.add(new PatternIdea("Demo2", "DemoProblem2", "DemoSolution2", "www.microservice-api-patterns.org", "software", "56789"));
        Brainwave brainwave = new Brainwave(3, list);

        BrainwaveDTO brainwaveDTO = modelsMapper.toBrainwaveDTO(brainwave);


        PatternIdeaDTO patternIdeaDTO = (PatternIdeaDTO) brainwaveDTO.getIdeas().get(0);
        PatternIdea patternIdea = (PatternIdea) brainwave.getIdeas().get(0);

        assertEquals(2, brainwaveDTO.getIdeas().size());
        assertTrue(brainwaveDTO.getIdeas().get(0) instanceof PatternIdeaDTO);

        assertEquals(patternIdea.getDescription(), patternIdeaDTO.getDescription());
        assertEquals(patternIdea.getProblem(), patternIdeaDTO.getProblem());
        assertEquals(patternIdea.getSolution(), patternIdeaDTO.getSolution());
        assertEquals(patternIdea.getPictureId(), patternIdeaDTO.getPictureId());

    }

    @Test
    public void allBOTypesTest() {
        ArrayList<Idea> list = new ArrayList<>();
        list.add(new NoteIdea("Demo"));
        list.add(new SketchIdea("Demo", "01234"));
        list.add(new PatternIdea("Demo","DemoProblem", "DemoSolution", "www.microservice-api-patterns.org", "software", "01234"));
        Brainwave brainwave = new Brainwave(3, list);

        BrainwaveDTO brainwaveDTO = modelsMapper.toBrainwaveDTO(brainwave);

        assertEquals(3, brainwaveDTO.getIdeas().size());
        assertTrue(brainwaveDTO.getIdeas().get(0) instanceof NoteIdeaDTO);
        assertTrue(brainwaveDTO.getIdeas().get(1) instanceof SketchIdeaDTO);
        assertTrue(brainwaveDTO.getIdeas().get(2) instanceof PatternIdeaDTO);


    }
}
