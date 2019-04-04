package mapper;


import models.bo.*;
import models.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrainwaveBOUnitTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() {
        modelMapper.createTypeMap(Idea.class, IdeaDTO.class)
                .include(NoteIdea.class, IdeaDTO.class)
                .include(SketchIdea.class, IdeaDTO.class)
                .include(PatternIdea.class, PatternIdeaDTO.class);

        modelMapper.typeMap(NoteIdea.class, IdeaDTO.class).setProvider(request -> {
            NoteIdea idea = (NoteIdea)request.getSource();
            return new NoteIdeaDTO(idea.getDescription());
        });

        modelMapper.typeMap(SketchIdea.class, IdeaDTO.class).setProvider(request -> {
            SketchIdea idea = (SketchIdea)request.getSource();
            return new SketchIdeaDTO(idea.getDescription(), idea.getPictureId());
        });

        modelMapper.typeMap(PatternIdea.class, IdeaDTO.class).setProvider(request -> {
            PatternIdea idea = (PatternIdea)request.getSource();
            return new PatternIdeaDTO(idea.getDescription(), idea.getProblem(), idea.getSolution(), idea.getUrl(), idea.getCategory(), idea.getPictureId());
        });

    }

    @Test
    public void noteIdeaTest() {
        ArrayList<Idea> list = new ArrayList<>();
        list.add(new NoteIdea("Demo"));
        list.add(new NoteIdea("Demo2"));
        Brainwave brainwave = new Brainwave(3, list);

        BrainwaveDTO brainwaveDTO = modelMapper.map(brainwave, BrainwaveDTO.class);


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

        BrainwaveDTO brainwaveDTO = modelMapper.map(brainwave, BrainwaveDTO.class);


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

        BrainwaveDTO brainwaveDTO = modelMapper.map(brainwave, BrainwaveDTO.class);


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

        BrainwaveDTO brainwaveDTO = modelMapper.map(brainwave, BrainwaveDTO.class);

        assertEquals(3, brainwaveDTO.getIdeas().size());
        assertTrue(brainwaveDTO.getIdeas().get(0) instanceof NoteIdeaDTO);
        assertTrue(brainwaveDTO.getIdeas().get(1) instanceof SketchIdeaDTO);
        assertTrue(brainwaveDTO.getIdeas().get(2) instanceof PatternIdeaDTO);


    }
}
